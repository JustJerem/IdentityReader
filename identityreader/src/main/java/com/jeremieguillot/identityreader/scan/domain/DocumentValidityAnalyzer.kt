package com.jeremieguillot.identityreader.scan.domain

import com.jeremieguillot.identityreader.core.domain.IdentityDocument
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

enum class DocumentValidityIssue {
    INCORRECT_CITY_ZIPCODE,
}

class DocumentValidityAnalyzer(val document: IdentityDocument) {

    fun processIrregularities(): MutableList<DocumentValidityIssue> {
        val irregularities = mutableListOf<DocumentValidityIssue>()
        val isCityNameCorrect =
            document.city.matches(Regex("^[\\p{L} '-]+$")) && document.city.isNotBlank()

        val isZipCodeCorrect =
            document.zipCode.all { it.isDigit() || it == 'A' || it == 'B' }//AB for Corsica

        if (!isCityNameCorrect || !isZipCodeCorrect) {
            document.city = ""
            document.zipCode = ""
            irregularities.add(DocumentValidityIssue.INCORRECT_CITY_ZIPCODE)
        }
        return irregularities
    }


    fun expirationDateIsInThePast(): Boolean {
        val expirationDate = document.expirationDate
        if (expirationDate.isBlank()) return false
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val expiration = LocalDate.parse(expirationDate, formatter)
            expiration.isBefore(LocalDate.now())
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Invalid date format. Please use 'dd/MM/yyyy'.")
        }
    }
}