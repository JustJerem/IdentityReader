package com.jeremieguillot.identityreader.scan.data

import com.google.mlkit.vision.text.Text
import com.jeremieguillot.identityreader.core.domain.DataDocument
import java.util.Locale


/**
 * The optical zone of a biometric passport, also known as the Machine Readable Zone (MRZ), consists of two lines of text containing essential information about the passport holder and the document itself. This information is encoded to be quickly readable by machines. Here's the composition of each line, section by section:
 *
 * ### First line of the Machine Readable Zone (MRZ)
 * 1. **Positions 1-2**: Document type
 *    - Typically "P" for passport.
 * 2. **Positions 3-5**: Issuing country code
 *    - A three-letter code corresponding to ISO 3166-1 alpha-3.
 * 3. **Positions 6-44**: Full name
 *    - The name is separated by two chevrons ("<<") between the surname and given names. Spaces in names are also represented by chevrons.
 *
 * ### Second line of the Machine Readable Zone (MRZ)
 * 1. **Positions 1-9**: Passport number
 *    - Followed by a check digit at position 10.
 * 2. **Positions 11-13**: Nationality code
 *    - A three-letter code corresponding to ISO 3166-1 alpha-3 of the holder's nationality.
 * 3. **Positions 14-19**: Date of birth
 *    - YYMMDD format, followed by a check digit at position 20.
 * 4. **Position 21**: Gender
 *    - "M" for male, "F" for female, or "X" for unspecified.
 * 5. **Positions 22-27**: Passport expiration date
 *    - YYMMDD format, followed by a check digit at position 28.
 * 6. **Positions 29-42**: Personal identification number (if applicable)
 *    - Sometimes used for a national identity card number or other relevant identifier, followed by a check digit at position 43.
 * 7. **Positions 44-44**: Overall check character
 *    - This check character is calculated over all the data from both lines to verify the integrity of the information.
 *
 * ### Example MRZ for a passport:
 * First line:
 * ```
 * P<UTOERIKSSON<<ANNA<MARIA<<<<<<<<<<<<<<<<<<<
 * ```
 * Second line:
 * ```
 * L898902C36UTO7408122F1204159<<<<<<<<<<<<<<00
 * ```
 *
 * ### Explanation of the example:
 *
 * 1. **P**: Document type (Passport)
 * 2. **UTO**: Issuing country code (Utopia)
 * 3. **ERIKSSON<<ANNA<MARIA**: Surname (ERIKSSON), Given name (ANNA MARIA)
 * 4. **L898902C3**: Passport number
 * 5. **6**: Check digit for passport number
 * 6. **UTO**: Nationality (Utopia)
 * 7. **740812**: Date of birth (12 August 1974)
 * 8. **2**: Check digit for date of birth
 * 9. **F**: Gender (Female)
 * 10. **120415**: Expiration date (15 April 2012)
 * 11. **9**: Check digit for expiration date
 * 12. **<<<<<<<<<<<<<<00**: Personal identifier and overall check character
 */
sealed class MRZResult {
    data class Success(val data: DataDocument) : MRZResult()
    data object Failure : MRZResult()
}

class MRZRecognitionOCR {
    fun recognize(blocks: List<Text.TextBlock>): MRZResult {
        val fullRead = blocks.joinToString("-") { block ->
            block.lines.joinToString("-") { line ->
                line.text.trim()
                    .replace("\r", "")
                    .replace("\n", "")
                    .replace("\t", "")
                    .replace(" ", "")
            }
        }.uppercase(Locale.getDefault())
        return DocumentTypeIdentifier(fullRead).identify()
    }
}