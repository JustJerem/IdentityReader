package com.jeremieguillot.identityreader.nfc.data

import android.nfc.tech.IsoDep
import android.util.Log
import com.jeremieguillot.identityreader.core.domain.DocumentType
import com.jeremieguillot.identityreader.core.domain.IdentityDocument
import com.jeremieguillot.identityreader.core.domain.MRZ
import com.jeremieguillot.identityreader.core.domain.util.DataError
import com.jeremieguillot.identityreader.core.domain.util.Error
import com.jeremieguillot.identityreader.core.domain.util.Result
import com.jeremieguillot.identityreader.core.extension.toSlashStringDate
import net.sf.scuba.data.Gender
import net.sf.scuba.smartcards.CardService
import org.jmrtd.BACKey
import org.jmrtd.BACKeySpec
import org.jmrtd.PACEKeySpec
import org.jmrtd.PassportService
import org.jmrtd.lds.CardAccessFile
import org.jmrtd.lds.PACEInfo
import org.jmrtd.lds.icao.DG11File
import org.jmrtd.lds.icao.DG12File
import org.jmrtd.lds.icao.DG1File


class NFCDocument {

    fun startReadTask(isoDep: IsoDep, mrz: MRZ): Result<IdentityDocument, Error> {
        val bacKey = BACKey(mrz.documentNumber, mrz.dateOfBirth, mrz.dateOfExpiry)
        return readPassport(isoDep, bacKey)
    }

    private fun readPassport(isoDep: IsoDep, bacKey: BACKeySpec): Result<IdentityDocument, Error> {
        val paceKey = PACEKeySpec.createMRZKey(bacKey)

        return runCatching {
            isoDep.timeout = 15_000
            val cardService = createCardService(isoDep)
            val passportService = createPassportService(cardService)

            if (performPace(passportService, paceKey)) {
                passportService.sendSelectApplet(true)
            } else {
                performBacFallback(passportService, bacKey)
            }

            val identityDocument = extractIdentityDocument(passportService)
            Result.Success(identityDocument)
        }.getOrElse { exception ->
            Log.e(TAG, "Failed to read passport: ${exception.message}", exception)
            Result.Error(DataError.Local.INVALID_DATA)
        }
    }

    // Creates and opens the CardService
    private fun createCardService(isoDep: IsoDep): CardService {
        return CardService.getInstance(isoDep).apply { open() }
    }

    // Creates and opens the PassportService
    private fun createPassportService(cardService: CardService): PassportService {
        return PassportService(
            /* service = */ cardService,
            /* maxTranceiveLengthForSecureMessaging = */
            PassportService.NORMAL_MAX_TRANCEIVE_LENGTH,
            /* maxBlockSize = */
            PassportService.DEFAULT_MAX_BLOCKSIZE,
            /* isSFIEnabled = */
            true,
            /* shouldCheckMAC = */
            false
        ).apply { open() }
    }

    // Attempts to perform PACE and returns whether it succeeded
    private fun performPace(passportService: PassportService, paceKey: PACEKeySpec): Boolean {
        return runCatching {
            passportService.getInputStream(PassportService.EF_CARD_ACCESS).use { stream ->
                val paceInfo = CardAccessFile(stream).securityInfos
                    .filterIsInstance<PACEInfo>()
                    .firstOrNull() ?: throw IllegalArgumentException("PACEInfo not found")

                val parameterSpec = PACEInfo.toParameterSpec(paceInfo.parameterId)
                passportService.doPACE(paceKey, paceInfo.objectIdentifier, parameterSpec)
            }
            true
        }.onFailure { exception ->
            Log.w(TAG, "PACE failed: ${exception.message}", exception)
        }.getOrDefault(false)
    }

    // Fallback to BAC if PACE fails
    private fun performBacFallback(passportService: PassportService, bacKey: BACKeySpec) {
        try {
            passportService.getInputStream(PassportService.EF_COM).read()
        } catch (e: Exception) {
            passportService.doBAC(bacKey)
        }
    }

    // Extracts the identity document information from the passport service
    private fun extractIdentityDocument(passportService: PassportService): IdentityDocument {
        val dg1File = DG1File(passportService.getInputStream(PassportService.EF_DG1))
        val dg11File = DG11File(passportService.getInputStream(PassportService.EF_DG11))
        val dg12File = DG12File(passportService.getInputStream(PassportService.EF_DG12))

        return IdentityDocument(
            type = DocumentType.PASSPORT,
            documentNumber = dg1File.mrzInfo.documentNumber,
            firstName = dg1File.mrzInfo.primaryIdentifier,
            lastName = extractLastNames(dg11File.nameOfHolder),
            gender = dg1File.mrzInfo.gender.toLetter(),
            issuingIsO3Country = dg1File.mrzInfo.issuingState,
            nationality = dg1File.mrzInfo.nationality,
            address = dg11File.permanentAddress.firstOrNull()?.trimStart().orEmpty(),
            city = dg11File.permanentAddress.getOrNull(2).orEmpty(),
            postalCode = dg11File.permanentAddress.getOrNull(1).orEmpty(),
            country = dg11File.permanentAddress.getOrNull(4).orEmpty(),
            placeOfBirth = dg11File.placeOfBirth.joinToString(),
            birthDate = dg1File.mrzInfo.dateOfBirth.toSlashStringDate(forceDateInPast = true),
            expirationDate = dg1File.mrzInfo.dateOfExpiry.toSlashStringDate(),
            deliveryDate = dg12File.dateOfIssue.toSlashStringDate(pattern = "yyyyMMdd")
        )
    }

    companion object {
        private const val TAG = "NFCDocument"
    }
}


fun extractLastNames(input: String): String {
    val parts = input.split("<<")

    // Get the part after "<<", split by "<", filter out any empty parts, and join them with spaces
    return parts.getOrNull(1)?.split("<")?.filter { it.isNotEmpty() }?.joinToString(" ") ?: ""
}

fun Gender.toLetter(): String {
    return when (this) {
        Gender.MALE -> "M"
        Gender.FEMALE -> "F"
        else -> ""
    }
}