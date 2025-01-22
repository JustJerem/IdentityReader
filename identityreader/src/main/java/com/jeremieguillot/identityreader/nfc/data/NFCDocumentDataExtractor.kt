package com.jeremieguillot.identityreader.nfc.data

import com.jeremieguillot.identityreader.core.domain.DocumentType
import com.jeremieguillot.identityreader.core.domain.IdentityDocument
import com.jeremieguillot.identityreader.core.extension.toSlashStringDate
import net.sf.scuba.data.Gender
import org.jmrtd.PassportService
import org.jmrtd.lds.icao.DG11File
import org.jmrtd.lds.icao.DG12File
import org.jmrtd.lds.icao.DG1File

class NFCDocumentDataExtractor {

    private val zipCodeRegex = """\b\d{5}\b""".toRegex()  // Pattern to match 5 digits (zip code)

    // Extracts the identity document information from the passport service
    fun extract(
        passportService: PassportService,
        documentType: DocumentType,
    ): IdentityDocument {
        val dg1File = DG1File(
            passportService.getInputStream(
                PassportService.EF_DG1,
                NFCDocument.DEFAULT_MAX_BLOCK_SIZE
            )
        )
        val dg11File = DG11File(
            passportService.getInputStream(
                PassportService.EF_DG11,
                NFCDocument.DEFAULT_MAX_BLOCK_SIZE
            )
        )
        val dg12File = DG12File(
            passportService.getInputStream(
                PassportService.EF_DG12,
                NFCDocument.DEFAULT_MAX_BLOCK_SIZE
            )
        )

        return when (documentType) {
            DocumentType.RESIDENT_PERMIT -> extractResidentPermit(dg1File, dg11File, dg12File)
            else -> extractDefault(documentType, dg1File, dg11File, dg12File)
        }
    }

    private fun extractDefault(
        type: DocumentType,
        dg1File: DG1File,
        dg11File: DG11File,
        dg12File: DG12File,
    ): IdentityDocument {
        return IdentityDocument(
            type = type,
            documentNumber = dg1File.mrzInfo.documentNumber,
            firstName = extractLastNames(dg11File.nameOfHolder),
            lastName = dg1File.mrzInfo.primaryIdentifier,
            gender = dg1File.mrzInfo.gender.toLetter(),
            issuingIsO3Country = dg1File.mrzInfo.issuingState,
            nationality = dg1File.mrzInfo.nationality,
            address = dg11File.permanentAddress.firstOrNull()?.trimStart().orEmpty(),
            city = dg11File.permanentAddress.getOrNull(2).orEmpty(),
            zipCode = dg11File.permanentAddress.getOrNull(1).orEmpty(),
            country = dg11File.permanentAddress.getOrNull(4).orEmpty(),
            placeOfBirth = dg11File.placeOfBirth.joinToString(),
            birthDate = dg1File.mrzInfo.dateOfBirth.toSlashStringDate(forceDateInPast = true),
            expirationDate = dg1File.mrzInfo.dateOfExpiry.toSlashStringDate(),
            deliveryDate = dg12File.dateOfIssue.toSlashStringDate(pattern = "yyyyMMdd")
        )
    }

    private fun extractResidentPermit(
        dg1File: DG1File,
        dg11File: DG11File,
        dg12File: DG12File,
    ): IdentityDocument {
        var address = dg11File.permanentAddress.firstOrNull()?.trimStart().orEmpty()
        var city = dg11File.permanentAddress.getOrNull(2).orEmpty()
        var zipCode = dg11File.permanentAddress.getOrNull(1).orEmpty()

        //The resident permit is not well coded by the authorities and so, we need to move some elements
        when {
            address.isEmpty() && zipCodeRegex.containsMatchIn(city) -> {
                val (newZipCode, newCity) = extractCityAndZip(city)
                address = zipCode
                zipCode = newZipCode
                city = newCity
            }

            city.isEmpty() && zipCodeRegex.containsMatchIn(zipCode) -> {
                val (newZipCode, newCity) = extractCityAndZip(zipCode)
                zipCode = newZipCode
                city = newCity
            }
        }

        return IdentityDocument(
            type = DocumentType.PASSPORT,
            documentNumber = dg1File.mrzInfo.documentNumber,
            firstName = extractLastNames(dg11File.nameOfHolder),
            lastName = dg1File.mrzInfo.primaryIdentifier,
            gender = dg1File.mrzInfo.gender.toLetter(),
            issuingIsO3Country = dg1File.mrzInfo.issuingState,
            nationality = dg1File.mrzInfo.nationality,
            address = address,
            city = city,
            zipCode = zipCode,
            country = dg11File.permanentAddress.getOrNull(4).orEmpty(),
            placeOfBirth = dg11File.placeOfBirth.joinToString(),
            birthDate = dg1File.mrzInfo.dateOfBirth.toSlashStringDate(forceDateInPast = true),
            expirationDate = dg1File.mrzInfo.dateOfExpiry.toSlashStringDate(),
            deliveryDate = dg12File.dateOfIssue.toSlashStringDate(pattern = "yyyyMMdd")
        )
    }


    private fun extractCityAndZip(input: String): Pair<String, String> {
        val matchResult = zipCodeRegex.find(input)
        return if (matchResult != null) {
            val zipCode = matchResult.value
            val city =
                input.replaceFirst(zipCode, "").trim()  // The rest of the string is the city name
            zipCode to city
        } else {
            "" to ""  // If no zip code found, return an empty text
        }
    }

    private fun extractLastNames(input: String): String {
        val parts = input.split("<<")

        // Get the part after "<<", split by "<", filter out any empty parts, and join names with spaces
        return parts.getOrNull(1)?.split("<")?.filter { it.isNotEmpty() }?.joinToString(" ") ?: ""
    }

    private fun Gender.toLetter(): String {
        return when (this) {
            Gender.MALE -> "M"
            Gender.FEMALE -> "F"
            else -> ""
        }
    }
}