package com.jeremieguillot.identityreader.nfc.data

import android.nfc.tech.IsoDep
import com.jeremieguillot.identityreader.core.domain.DocumentType
import com.jeremieguillot.identityreader.core.domain.IdentityDocument
import com.jeremieguillot.identityreader.core.domain.MRZ
import com.jeremieguillot.identityreader.core.domain.util.Error
import com.jeremieguillot.identityreader.core.domain.util.Result
import com.sncf.android.internal.identityreader.core.domain.util.DataError
import net.sf.scuba.smartcards.CardService
import org.jmrtd.BACKey
import org.jmrtd.BACKeySpec
import org.jmrtd.PACEKeySpec
import org.jmrtd.PassportService
import org.jmrtd.lds.CardAccessFile
import org.jmrtd.lds.PACEInfo
import timber.log.Timber


class NFCDocument {
    companion object {
        private const val TAG = "NFCDocument"
    }

    fun startReadTask(
        isoDep: IsoDep,
        mrz: MRZ,
        documentType: DocumentType,
    ): Result<IdentityDocument, Error> {
        val bacKey = BACKey(mrz.documentNumber, mrz.dateOfBirth, mrz.dateOfExpiry)
        return readDocument(isoDep, bacKey, documentType)
    }

    private fun readDocument(
        isoDep: IsoDep,
        bacKey: BACKeySpec,
        documentType: DocumentType,
    ): Result<IdentityDocument, Error> {
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

            val identityDocument = NFCDocumentDataExtractor().extract(passportService, documentType)
            Result.Success(identityDocument)
        }.getOrElse { exception ->
            Timber.tag(TAG).e(exception, "Failed to read : %s", exception.message)
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
            Timber.tag(TAG).w(exception, "PACE failed: %s", exception.message)
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
}