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
import org.jmrtd.PassportService
import org.jmrtd.lds.CardAccessFile
import org.jmrtd.lds.PACEInfo
import org.jmrtd.lds.icao.DG11File
import org.jmrtd.lds.icao.DG12File
import org.jmrtd.lds.icao.DG1File
import java.io.IOException


class NFCDocument {

    fun startReadTask(isoDep: IsoDep, mrz: MRZ): Result<IdentityDocument, Error> {
        val bacKey: BACKeySpec = BACKey(mrz.documentNumber, mrz.dateOfBirth, mrz.dateOfExpiry)
        return readPassport(isoDep, bacKey)
    }

    private fun readPassport(
        isoDep: IsoDep,
        bacKey: BACKeySpec
    ): Result<IdentityDocument, Error> {

        try {
            isoDep.timeout = 15_000
//            performBAC(isoDep, bacKey)
            val cardService = CardService.getInstance(isoDep).apply { open() }
            val service = PassportService(
                /* service = */ cardService,
                /* maxTranceiveLengthForSecureMessaging = */
                PassportService.NORMAL_MAX_TRANCEIVE_LENGTH,
                /* maxBlockSize = */
                PassportService.DEFAULT_MAX_BLOCKSIZE,
                /* isSFIEnabled = */
                false,
                /* shouldCheckMAC = */
                false
            ).apply { open() }



            val paceSucceeded = doPace(service, bacKey)
            service.sendSelectApplet(paceSucceeded)
            if (!paceSucceeded) try {
                service.getInputStream(PassportService.EF_COM).read()
            } catch (e: Exception) {
                service.doBAC(bacKey)
            }

            val dg1File = DG1File(service.getInputStream(PassportService.EF_DG1))
            val dg11File = DG11File(service.getInputStream(PassportService.EF_DG11))
            val dg12File = DG12File(service.getInputStream(PassportService.EF_DG12))


            return Result.Success(
                IdentityDocument(
                    type = DocumentType.PASSPORT,
                    documentNumber = dg1File.mrzInfo.documentNumber,
                    firstName = dg1File.mrzInfo.primaryIdentifier,
                    lastName = dg1File.mrzInfo.secondaryIdentifier,
                    gender = dg1File.mrzInfo.gender.toLetter(),
                    issuingIsO3Country = dg1File.mrzInfo.issuingState,
                    nationality = dg1File.mrzInfo.nationality,
                    address = dg11File.permanentAddress.first().trimStart(),
                    city = dg11File.permanentAddress[2],
                    postalCode = dg11File.permanentAddress[1],
                    country = dg11File.permanentAddress[4],
                    placeOfBirth = dg11File.placeOfBirth.joinToString { it },
                    birthDate = dg1File.mrzInfo.dateOfBirth.toSlashStringDate(forceDateInPast = true),
                    expirationDate = dg1File.mrzInfo.dateOfExpiry.toSlashStringDate(),
                    deliveryDate = dg12File.dateOfIssue.toSlashStringDate(pattern = "yyyyMMdd"),
                )
            )

        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return Result.Error(DataError.Local.INVALID_DATA)
        }
    }

    private fun doPace(service: PassportService, bacKey: BACKeySpec): Boolean = runCatching {

        val inputStream = service.getInputStream(
//            0x3F00,
            PassportService.EF_CARD_ACCESS,
            PassportService.DEFAULT_MAX_BLOCKSIZE
        )
        CardAccessFile(inputStream)
            .securityInfos
            .filterIsInstance<PACEInfo>()
            .forEach {
                service.doPACE(
                    bacKey,
                    it.objectIdentifier,
                    PACEInfo.toParameterSpec(it.parameterId),
                    null
                )
            }
        true
    }.getOrElse {
        Log.w(TAG, it)
        false
    }

    companion object {
        private const val TAG = "ReadTask"
    }
}

fun performBAC(isoDep: IsoDep, bacKey: BACKeySpec): Boolean {
    try {
        isoDep.connect()

        // Step 1: GET CHALLENGE
        val getChallengeCommand = byteArrayOf(0x00, 0x84.toByte(), 0x00, 0x00, 0x00)
        val challengeResponse = isoDep.transceive(getChallengeCommand)

        if (challengeResponse.isEmpty()) {
            Log.e(TAG, "Failed to get challenge from the card")
            return false
        }

        // Step 2: Compute challenge response
        val computedResponse = computeChallengeResponse(challengeResponse, bacKey)

        // Step 3: COMPUTE CHALLENGE
        val computeChallengeCommand = byteArrayOf(
            0x00, 0x82.toByte(), 0x00, 0x00, computedResponse.size.toByte()
        ) + computedResponse

        val computeResponse = isoDep.transceive(computeChallengeCommand)

        // Check the response for success (implementation-specific)
        val success = checkBACSuccess(computeResponse)
        if (!success) {
            Log.e(TAG, "BAC authentication failed")
        }

        return success

    } catch (e: IOException) {
        Log.e(TAG, "Error communicating with the card", e)
        return false
    } finally {
        try {
            isoDep.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing ISO-DEP connection", e)
        }
    }
}

private fun computeChallengeResponse(challenge: ByteArray, bacKey: BACKeySpec): ByteArray {
    // Implement the computation of the response using BAC key
    // This typically involves cryptographic operations
    // For simplicity, this is a placeholder
    return ByteArray(0) // Replace with actual computation
}

private fun checkBACSuccess(response: ByteArray): Boolean {
    // Implement checking of the BAC response
    // This typically involves checking the response status word or data
    return true // Replace with actual check
}



fun Gender.toLetter(): String {
    return when (this) {
        Gender.MALE -> "M"
        Gender.FEMALE -> "F"
        else -> ""
    }
}