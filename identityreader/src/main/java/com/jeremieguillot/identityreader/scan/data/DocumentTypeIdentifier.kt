package com.jeremieguillot.identityreader.scan.data

import com.jeremieguillot.identityreader.core.domain.DataDocument
import com.jeremieguillot.identityreader.core.domain.DocumentType
import com.jeremieguillot.identityreader.core.domain.util.DataError
import com.jeremieguillot.identityreader.core.domain.util.Result
import java.util.regex.Pattern

class DocumentTypeIdentifier(private val fullRead: String) {

    //Matcher for identity card
    private val identityCardMatcher1 =
        Pattern.compile(RegexPatterns.REGEX_NEW_CARD_LINE_1).matcher(fullRead)
    private val identityCardMatcher2 =
        Pattern.compile(RegexPatterns.REGEX_NEW_CARD_LINE_2).matcher(fullRead)
    private val identityCardMatcher3 =
        Pattern.compile(RegexPatterns.REGEX_NEW_CARD_LINE_3).matcher(fullRead)

    //Matcher for old identity card
    private val oldIdentityCardMatcher1 =
        Pattern.compile(RegexPatterns.REGEX_OLD_FRENCH_CARD_ID).matcher(fullRead)
    private val oldIdentityCardMatcher2 =
        Pattern.compile(RegexPatterns.REGEX_OLD_FRENCH_CARD_ID_2).matcher(fullRead)

    //Matcher for passport
    private val passportMatcher1 =
        Pattern.compile(RegexPatterns.REGEX_PASSPORT_FIRST_LINE).matcher(fullRead)
    private val passportMatcher2 =
        Pattern.compile(RegexPatterns.REGEX_PASSPORT).matcher(fullRead)

    //Matcher for driving licence
    private val drivingLicenceMatcher =
        Pattern.compile(RegexPatterns.REGEX_DRIVING_LICENCE).matcher(fullRead)

    //Matcher for resident permit
    private val residencePermitMatcher1 =
        Pattern.compile(RegexPatterns.REGEX_RESIDENCE_PERMIT_1).matcher(fullRead)
    private val residencePermitMatcher2 =
        Pattern.compile(RegexPatterns.REGEX_RESIDENCE_PERMIT_2).matcher(fullRead)
    private val residencePermitMatcher3 =
        Pattern.compile(RegexPatterns.REGEX_RESIDENCE_PERMIT_3).matcher(fullRead)

    fun identify(): MRZResult {
        return when {
            matchNewIdentityCard() -> processIdentityCard()
            matchPassport() -> processPassport()
            matchOldIdentityCard() -> processOldIdentityCard()
            matchDrivingLicence() -> processDrivingLicence()
            matchResidentPermit() -> processResidentPermit()
            else -> MRZResult.Failure
        }
    }

    private fun matchNewIdentityCard(): Boolean {
        return identityCardMatcher1.find() && identityCardMatcher2.find() && identityCardMatcher3.find()
    }

    private fun matchPassport(): Boolean {
        return passportMatcher1.find() && passportMatcher2.find()
    }

    private fun matchOldIdentityCard(): Boolean {
        return oldIdentityCardMatcher1.find() && oldIdentityCardMatcher2.find()
    }

    private fun matchDrivingLicence(): Boolean {
        return drivingLicenceMatcher.find()
    }

    private fun matchResidentPermit(): Boolean {
        return residencePermitMatcher1.find() && residencePermitMatcher2.find() && residencePermitMatcher3.find()
    }

    private fun processOldIdentityCard(): MRZResult {
        return processDocument(
            type = DocumentType.OLD_ID_CARD,
            issuingCountry = oldIdentityCardMatcher1.group(RegexPatterns.ISSUING_COUNTRY),
            documentNumber = oldIdentityCardMatcher2.group(RegexPatterns.ISSUE_DATE) + oldIdentityCardMatcher2.group(
                RegexPatterns.DOCUMENT_NUMBER
            ),
            checkDigitDocumentNumber = oldIdentityCardMatcher2.group(RegexPatterns.CHECK_DOCUMENT_NUMBER),
            expirationDate = "",
            dateOfBirth = oldIdentityCardMatcher2.group(RegexPatterns.BIRTH_DATE),
            checkDigitDateOfBirth = oldIdentityCardMatcher2.group(RegexPatterns.CHECK_BIRTH_DATE), //hard coded because only working with France
            deliveryDate = oldIdentityCardMatcher2.group(RegexPatterns.ISSUE_DATE) + "01",
            firstName = oldIdentityCardMatcher2.group(RegexPatterns.FIRST_NAME),
            lastName = oldIdentityCardMatcher1.group(RegexPatterns.LAST_NAME),
            nationality = "FRA", //date only contains month and year, adding first day of month
            sex = oldIdentityCardMatcher2.group(RegexPatterns.SEX),
        )
    }

    private fun processPassport(): MRZResult {
        return processDocument(
            type = DocumentType.PASSPORT,
            issuingCountry = passportMatcher1.group(RegexPatterns.ISSUING_COUNTRY),
            documentNumber = passportMatcher2.group(RegexPatterns.DOCUMENT_NUMBER),
            checkDigitDocumentNumber = passportMatcher2.group(RegexPatterns.CHECK_DOCUMENT_NUMBER),
            expirationDate = passportMatcher2.group(RegexPatterns.EXPIRATION_DATE),
            checkDigitExpirationDate = passportMatcher2.group(RegexPatterns.CHECK_EXPIRATION_DATE),
            dateOfBirth = passportMatcher2.group(RegexPatterns.BIRTH_DATE),
            checkDigitDateOfBirth = passportMatcher2.group(RegexPatterns.CHECK_BIRTH_DATE),
            lastName = passportMatcher1.group(RegexPatterns.LAST_NAME),
            nationality = passportMatcher2.group(RegexPatterns.NATIONALITY),
            sex = passportMatcher2.group(RegexPatterns.SEX)
        )
    }

    private fun processIdentityCard(): MRZResult {
        return processDocument(
            type = DocumentType.ID_CARD,
            issuingCountry = identityCardMatcher1.group(RegexPatterns.ISSUING_COUNTRY),
            documentNumber = identityCardMatcher1.group(RegexPatterns.DOCUMENT_NUMBER),
            checkDigitDocumentNumber = identityCardMatcher1.group(RegexPatterns.CHECK_DOCUMENT_NUMBER),
            expirationDate = identityCardMatcher2.group(RegexPatterns.EXPIRATION_DATE),
            checkDigitExpirationDate = identityCardMatcher2.group(RegexPatterns.CHECK_EXPIRATION_DATE),
            dateOfBirth = identityCardMatcher2.group(RegexPatterns.BIRTH_DATE),
            checkDigitDateOfBirth = identityCardMatcher2.group(RegexPatterns.CHECK_BIRTH_DATE),
            lastName = identityCardMatcher3.group(RegexPatterns.LAST_NAME),
            nationality = identityCardMatcher2.group(RegexPatterns.NATIONALITY),
            sex = identityCardMatcher2.group(RegexPatterns.SEX)
        )
    }

    private fun processDrivingLicence(): MRZResult {
        val checkDigit = drivingLicenceMatcher.group(RegexPatterns.CHECK_LINE).toInt()
        return when (cleanDocumentNumber(fullRead, checkDigit)) {
            is Result.Error -> MRZResult.Failure
            is Result.Success -> processDocument(
                type = DocumentType.DRIVING_LICENCE,
                issuingCountry = drivingLicenceMatcher.group(RegexPatterns.ISSUING_COUNTRY),
                documentNumber = drivingLicenceMatcher.group(RegexPatterns.DOCUMENT_NUMBER),
                checkDigitDocumentNumber = drivingLicenceMatcher.group(RegexPatterns.CHECK_DOCUMENT_NUMBER),
                expirationDate = drivingLicenceMatcher.group(RegexPatterns.EXPIRATION_DATE),
                lastName = drivingLicenceMatcher.group(RegexPatterns.LAST_NAME)
            )
        }
    }

    private fun processResidentPermit(): MRZResult {
        return processDocument(
            type = DocumentType.RESIDENT_PERMIT,
            issuingCountry = residencePermitMatcher1.group(RegexPatterns.ISSUING_COUNTRY),
            documentNumber = residencePermitMatcher1.group(RegexPatterns.DOCUMENT_NUMBER),
            checkDigitDocumentNumber = residencePermitMatcher1.group(RegexPatterns.CHECK_DOCUMENT_NUMBER),
            expirationDate = residencePermitMatcher2.group(RegexPatterns.EXPIRATION_DATE),
            checkDigitExpirationDate = residencePermitMatcher2.group(RegexPatterns.CHECK_EXPIRATION_DATE),
            dateOfBirth = residencePermitMatcher2.group(RegexPatterns.BIRTH_DATE),
            checkDigitDateOfBirth = residencePermitMatcher2.group(RegexPatterns.CHECK_BIRTH_DATE),
            lastName = residencePermitMatcher3.group(RegexPatterns.LAST_NAME),
            nationality = residencePermitMatcher2.group(RegexPatterns.NATIONALITY),
            sex = residencePermitMatcher2.group(RegexPatterns.SEX)
        )
    }

    private fun processDocument(
        type: DocumentType,
        issuingCountry: String,
        documentNumber: String,
        checkDigitDocumentNumber: String,
        expirationDate: String = "",
        checkDigitExpirationDate: String = "",
        dateOfBirth: String = "",
        checkDigitDateOfBirth: String = "",
        deliveryDate: String = "",
        firstName: String = "",
        lastName: String = "",
        nationality: String = "",
        sex: String = "",
    ): MRZResult {
        val localResult = cleanDocumentNumber(
            documentNumber = documentNumber,
            checkDigit = cleanDigit(checkDigitDocumentNumber).toInt()
        )
        val resultDateOfBirth =
            when (val result = checkDocumentNumber(dateOfBirth, checkDigitDateOfBirth)) {
                is Result.Error -> return MRZResult.Failure
                is Result.Success -> result.data
            }
        val resultExpirationDate =
            when (val result = checkDocumentNumber(expirationDate, checkDigitExpirationDate)) {
                is Result.Error -> return MRZResult.Failure
                is Result.Success -> result.data
            }

        return when (localResult) {
            is Result.Error -> MRZResult.Failure
            is Result.Success -> MRZResult.Success(
                DataDocument(
                    type = type,
                    issuingCountry = issuingCountry,
                    documentNumber = localResult.data,
                    nationality = nationality,
                    lastName = cleanCharacter(lastName),
                    dateOfBirth = cleanDigit(resultDateOfBirth),
                    dateOfExpiry = cleanDigit(resultExpirationDate),
                    sex = sex,
                    firstName = firstName,
                    deliveryDate = deliveryDate
                )
            )
        }
    }

    private fun checkDocumentNumber(
        date: String,
        checkDigit: String
    ): Result<String, DataError.Local> {
        if (checkDigit.isBlank()) return Result.Success(date)
        return cleanDocumentNumber(
            date,
            cleanDigit(checkDigit).toInt()
        )
    }

    private fun cleanDocumentNumber(
        documentNumber: String,
        checkDigit: Int
    ): Result<String, DataError.Local> {
        // Replace all 'O' with '0'
        val tempDocumentNumber = documentNumber.replace("O", "0")

        // Calculate check digit of the document number
        var calculatedCheckDigit = checkDigit(tempDocumentNumber)

        // If check digits match, return the document number
        if (checkDigit == calculatedCheckDigit) {
            return Result.Success(tempDocumentNumber)
        }

        // Try replacing each '0' with 'O' one at a time to see if it matches the check digit
        tempDocumentNumber.forEachIndexed { index, char ->
            if (char == '0') {
                val modifiedDocumentNumber = tempDocumentNumber.replaceRange(index, index + 1, "O")
                calculatedCheckDigit = checkDigit(modifiedDocumentNumber)
                if (checkDigit == calculatedCheckDigit) {
                    return Result.Success(modifiedDocumentNumber)
                }
            }
        }

        // If no match is found, return null
        return Result.Error(DataError.Local.INVALID_DATA)
    }

    /**
     * The check digit for the passport number is a digit calculated from the characters of the passport number, used to verify the accuracy and integrity of the data. This check digit is generated by applying a checksum algorithm (modulo 10) to the characters of the passport number.
     *
     * ### Calculation of the check digit
     * The calculation is done in the following steps:
     * 1. **Assignment of numerical values to characters**:
     *    - Digits from 0 to 9 retain their numerical values (e.g., 0 → 0, 1 → 1, ..., 9 → 9).
     *    - Letters from A to Z are converted to numerical values: A = 10, B = 11, ..., Z = 35.
     * 2. **Multiplication by weights**:
     *    - Each character is multiplied by a cyclic weight of 7, 3, and 1. These weights are applied repetitively.
     * 3. **Sum of results**:
     *    - The obtained products are summed.
     * 4. **Calculation of modulo 10**:
     *    - The total sum is divided by 10, and the remainder of this division is the check digit.
     *
     * ### Detailed Example
     * Let's take an example with the passport number: **L898902C3**.
     * 1. **Conversion of characters to numerical values**:
     *    - L = 21
     *    - 8 = 8
     *    - 9 = 9
     *    - 8 = 8
     *    - 9 = 9
     *    - 0 = 0
     *    - 2 = 2
     *    - C = 12
     *    - 3 = 3
     * 2. **Multiplication by weights (7, 3, 1)**:
     *    - 21 × 7 = 147
     *    - 8 × 3 = 24
     *    - 9 × 1 = 9
     *    - 8 × 7 = 56
     *    - 9 × 3 = 27
     *    - 0 × 1 = 0
     *    - 2 × 7 = 14
     *    - 12 × 3 = 36
     *    - 3 × 1 = 3
     * 3. **Sum of products**:
     *    - 147 + 24 + 9 + 56 + 27 + 0 + 14 + 36 + 3 = 316
     * 4. **Calculation of modulo 10**:
     *    - 316 % 10 = 6
     * So, the check digit for the passport number **L898902C3** is **6**.
     * This check digit is then placed at the next position of the passport number in the Machine Readable Zone (MRZ) to verify the integrity of the data during machine reading.
     */
    private fun checkDigit(documentNumber: String): Int {

        val weights = intArrayOf(7, 3, 1)
        var sum = 0

        for (i in documentNumber.indices) {
            val char = documentNumber[i]
            val value = if (char.isLetter()) {
                // Convert letters to corresponding numerical values (A = 10, B = 11, ..., Z = 35)
                char.uppercaseChar() - 'A' + 10
            } else {
                // Convert digits to numerical values
                char - '0'
            }
            // Multiply by the appropriate weight (7, 3, 1)
            sum += value * weights[i % 3]
        }
        // Return the remainder of the sum divided by 10
        return sum % 10
    }

    private fun cleanDigit(digit: String): String {
        return digit.map { char ->
            when (char) {
                'I', 'L' -> '1'
                'D', 'O' -> '0'
                'S' -> '5'
                'G' -> '6'
                else -> char
            }
        }.joinToString("")
    }

    private fun cleanCharacter(digit: String): String {
        return digit.map { char ->
            when (char) {
                '1' -> "I"
                '0' -> "O"
                '5' -> "S"
                '6' -> "G"
                else -> char.toString() // If the character is not a digit, keep it unchanged
            }
        }.joinToString("")
    }
}