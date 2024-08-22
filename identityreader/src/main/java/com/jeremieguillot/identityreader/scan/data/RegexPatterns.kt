package com.jeremieguillot.identityreader.scan.data


/**
 * This object contains regular expressions (regex) patterns and constant values used for parsing
 * and validating various document formats, specifically passport and identity card formats.
 *
 * The regex patterns are designed to match different parts of the document and extract relevant
 * information. The constants are used as named groups within the regex patterns to facilitate
 * extraction and identification of specific data fields.
 *
 * The regex patterns included are for (example):
 *
 * 1. **Passport**:
 *    - `REGEX_PASSPORT_FIRST_LINE`: Matches the first line of a passport, including issuing country, last name, and first name.
 *    - `REGEX_PASSPORT`: Matches the main data fields of a passport, such as document number, date of birth, sex, expiration date, and their respective check digits.
 *
 * 2. **Identity Card**:
 *    - `REGEX_NEW_CARD_LINE_1`: Matches the first line of a new identity card, including issuing country, document number, and check digit.
 *    - `REGEX_NEW_CARD_LINE_2`: Matches the second line of a new identity card, including date of birth, sex, expiration date, and nationality.
 *    - `REGEX_NEW_CARD_LINE_3`: Matches the third line of a new identity card, including last name and first name.
 *
 * 3. **Old Identity Card**:
 *    - `REGEX_OLD_FRENCH_CARD_ID`: Matches the initial part of an old French identity card, including issuing country and last name.
 *    - `REGEX_OLD_FRENCH_CARD_ID_2`: Matches the remaining part of an old French identity card, including issue date, document number, first name, date of birth, and sex.
 *
 * Constants (example) :
 * - `DOCUMENT_NUMBER`: Named group for document number.
 * - `DIGIT_DOCUMENT_NUMBER`: Named group for check digit of the document number.
 * - `BIRTH_DATE`: Named group for date of birth.
 * - `CHECK_BIRTH_DATE`: Named group for check digit of the date of birth.
 * - `ISSUE_DATE`: Named group for issue date.
 * - `EXPIRATION_DATE`: Named group for expiration date.
 * - `CHECK_EXPIRATION_DATE`: Named group for check digit of the expiration date.
 * - `SEX`: Named group for sex.
 * - `ISSUING_COUNTRY`: Named group for issuing country.
 * - `NATIONALITY`: Named group for nationality.
 * - `LAST_NAME`: Named group for last name.
 * - `FIRST_NAME`: Named group for first name.
 *
 * Usage:
 * - Use the defined regex patterns to validate and extract information from passport and identity card data.
 * - Utilize the named groups to easily access specific data fields from the matches.
 *
 */

object RegexPatterns {


    const val DOCUMENT_NUMBER = "documentNumber"
    const val CHECK_DOCUMENT_NUMBER = "checkDigitDocumentNumber"
    const val BIRTH_DATE = "dateOfBirth"
    const val CHECK_BIRTH_DATE = "checkDigitDateOfBirth"
    const val ISSUE_DATE = "issueDate"
    const val EXPIRATION_DATE = "expirationDate"
    const val CHECK_EXPIRATION_DATE = "checkDigitExpirationDate"
    const val SEX = "sex"
    const val ISSUING_COUNTRY = "issuingCountry"
    const val NATIONALITY = "nationality"
    const val LAST_NAME = "lastName"
    const val FIRST_NAME = "firstName"
    const val CHECK_LINE = "checkLine"

    //Regex for PASSPORT
    const val REGEX_PASSPORT_FIRST_LINE =
        "P<(?<$ISSUING_COUNTRY>[A-Z<]{3})(?<$LAST_NAME>[A-Z]{2,})<<([A-Z]+)"
    const val REGEX_PASSPORT =
        "(?<$DOCUMENT_NUMBER>[A-Z0-9<]{9})(?<$CHECK_DOCUMENT_NUMBER>[0-9ILDSOG]{1})(?<nationality>[A-Z<]{3})(?<$BIRTH_DATE>[0-9ILDSOG]{6})(?<$CHECK_BIRTH_DATE>[0-9ILDSOG]{1})(?<sex>[FM<]){1}(?<$EXPIRATION_DATE>[0-9ILDSOG]{6})(?<$CHECK_EXPIRATION_DATE>[0-9ILDSOG]{1})"


    //Regex for IDENTITY CARD
    const val REGEX_NEW_CARD_LINE_1 =
        "ID(?<$ISSUING_COUNTRY>[A-Z<]{3})(?<$DOCUMENT_NUMBER>[A-Z0-9<]{9})(?<$CHECK_DOCUMENT_NUMBER>[0-9]{1})"
    const val REGEX_NEW_CARD_LINE_2 =
        "(?<$BIRTH_DATE>[0-9]{6})(?<$CHECK_BIRTH_DATE>[0-9]{1})(?<$SEX>[FM]{1})(?<$EXPIRATION_DATE>[0-9]{6})(?<$CHECK_EXPIRATION_DATE>[0-9]{1})(?<$NATIONALITY>[A-Z<]{3})"
    const val REGEX_NEW_CARD_LINE_3 = "(?<$LAST_NAME>[A-Z]{2,})<<([A-Z]+)"

    //Regex for OLD IDENTITY CARD
    const val REGEX_OLD_FRENCH_CARD_ID = "ID(?<$ISSUING_COUNTRY>[A-Z]{3})(?<$LAST_NAME>[A-Z]{2,})<"
    const val REGEX_OLD_FRENCH_CARD_ID_2 =
        "(?<$ISSUE_DATE>[0-9]{4})(?<$DOCUMENT_NUMBER>[0-9]{8})(?<$CHECK_DOCUMENT_NUMBER>[0-9])(?<$FIRST_NAME>[A-Z]{2,})<<[A-Z0-9]{2,}<(?<$BIRTH_DATE>[0-9]{6})(?<$CHECK_BIRTH_DATE>[0-9])(?<$SEX>[FM])"

    //Regex for DRIVING LICENCE
    const val REGEX_DRIVING_LICENCE =
        "D1(?<$ISSUING_COUNTRY>[A-Z]{3})(?<$DOCUMENT_NUMBER>[A-Z0-9<]{9})(?<$CHECK_DOCUMENT_NUMBER>[0-9]{1})(?<$EXPIRATION_DATE>[0-9]{6})(?<$LAST_NAME>[A-Z]{2,})<{1,}(?<$CHECK_LINE>[0-9]{1})"

    //Regex for RESIDENCE PERMIT
    const val REGEX_RESIDENCE_PERMIT_1 =
        "IR(?<$ISSUING_COUNTRY>[A-Z]{3})(?<$DOCUMENT_NUMBER>[A-Z0-9<]{9})(?<$CHECK_DOCUMENT_NUMBER>[0-9]{1})<"
    const val REGEX_RESIDENCE_PERMIT_2 =
        "(?<$BIRTH_DATE>[0-9]{6})(?<$CHECK_BIRTH_DATE>[0-9]{1})(?<$SEX>[FM]{1})(?<$EXPIRATION_DATE>[0-9]{6})(?<$CHECK_EXPIRATION_DATE>[0-9]{1})(?<$NATIONALITY>[A-Z<]{3})<{1,}(?<$CHECK_LINE>[0-9]{1})"
    const val REGEX_RESIDENCE_PERMIT_3 = "(?<$LAST_NAME>[A-Z]{2,})<<([A-Z]+)"

}