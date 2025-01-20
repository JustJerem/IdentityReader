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

    /**
     * Initiates the task to start reading the passport data using the provided IsoDep object and MRZ.
     *
     * @param isoDep The IsoDep object that represents the ISO-DEP (ISO 14443-4) communication interface.
     * @param mrz The MRZ (Machine Readable Zone) data of the passport.
     * @return A Result object that encapsulates the identity document data if successful, or an error if unsuccessful.
     */
    fun startReadTask(isoDep: IsoDep, mrz: MRZ): Result<IdentityDocument, Error> {
        val bacKey = BACKey(mrz.documentNumber, mrz.dateOfBirth, mrz.dateOfExpiry)
        return readPassport(isoDep, bacKey)
    }

    /**
     * Reads the passport data using the provided IsoDep object and a BAC key.
     * This function attempts to perform PACE (Password Authenticated Connection Establishment) using the generated PACE key,
     * and falls back to BAC (Basic Access Control) if PACE fails.
     *
     * @param isoDep The IsoDep object that represents the ISO-DEP (ISO 14443-4) communication interface.
     * @param bacKey The BAC key specification used for authentication if PACE fails.
     *
     * @return A Result object that encapsulates the identity document data if successful, or an error if unsuccessful.
     * If successful, the Result contains an IdentityDocument object. If unsuccessful, the Result contains an Error object.
     */
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

        /**
     * Performs Password Authenticated Connection Establishment (PACE) using the provided passport service and PACE key.
     *
     * @param passportService The PassportService object used for communication with the passport.
     * @param paceKey The PACEKeySpec object containing the key for PACE authentication.
     * @return True if PACE authentication is successful; false otherwise.
     */
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

    /**
     * Extracts identity document data from the given passport service.
     *
     * This function reads the data from the passport service's specific files (DG1, DG11, and DG12),
     * and extracts various pieces of information to create an IdentityDocument object. The extracted
     * information includes the document type, document number, names, gender, issuing state, nationality,
     * address, place of birth, birth date, expiration date, and delivery date.
     *
     * @param passportService The PassportService object that provides access to the passport data.
     * @return An IdentityDocument object containing the extracted information.
     */
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
            postalCode = dg11File.permanentAddress.getOrNull(1).orEmpty(),
            city = dg11File.permanentAddress.getOrNull(2).orEmpty(),
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


/**
 * Extracts the last names from the provided input string.
 *
 * This function splits the input string by "<<" and takes the part after this delimiter.
 * Then it further splits this part by "<" and filters out any empty parts. Finally, it joins
 * the remaining parts with spaces to form the last names.
 *
 * @param input The input string from which the last names should be extracted. It is expected to
 *              be in a specific format, containing "<<" and "<" as delimiters.
 * @return The extracted last names as a string. If no last names are found in the input string,
 *         an empty string is returned.
 */
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