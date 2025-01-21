package com.jeremieguillot.identityreader.nfc.presentation.reader.components.documentcard.passport

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.jeremieguillot.identityreader.core.domain.DocumentType
import com.jeremieguillot.identityreader.core.domain.IdentityDocument
import org.junit.Rule
import org.junit.Test

class PassportCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val document = IdentityDocument(
        type = DocumentType.PASSPORT,
        documentNumber = "13A000026",
        issuingIsO3Country = "FRA",
        lastName = "Doe",
        firstName = "John, Peter, Maxwell",
        nationality = "FR",
        gender = "M",
        birthDate = "03/04/1982",
        expirationDate = "23/12/2045",
        placeOfBirth = "Strasbourg",
        address = "Boulevard du General de Gaule",
        zipCode = "75001",
        city = "Saint Romain en Provence",
        country = "France"
    )

    @Test
    fun passportCard_displaysCorrectInformation() {
        // Set the content to test
        composeTestRule.setContent {
            PassportCard(identityDocument = document)
        }

        // Assertions for text content
        composeTestRule.onNodeWithText("PASSPORT").assertExists() // Header text
        composeTestRule.onNodeWithText("P").assertExists() // Type
        composeTestRule.onNodeWithText("FRA").assertExists() // Country code
        composeTestRule.onNodeWithText("13A000026").assertExists() // Document number
        composeTestRule.onNodeWithText("Doe".uppercase()).assertExists() // Last name
        composeTestRule.onNodeWithText("John, Peter, Maxwell").assertExists() // First name
        composeTestRule.onNodeWithText("FRA").assertExists() // Nationality
        composeTestRule.onNodeWithText("M").assertExists() // Gender
        composeTestRule.onNodeWithText("03/04/1982").assertExists() // Birthdate
        composeTestRule.onNodeWithText("23/12/2045").assertExists() // Expiration Date
        composeTestRule.onNodeWithText("Strasbourg").assertExists() // Place of birth
        composeTestRule.onNodeWithText("Boulevard du General de Gaule").assertExists() // Address
        composeTestRule.onNodeWithText("75001 Saint Romain en Provence")
            .assertExists() // Zip code and city
        composeTestRule.onNodeWithText("France").assertExists() // Country
    }

    @Test
    fun passportCard_handlesMissingDataGracefully() {
        // Prepare a document with missing data
        val incompleteDocument = document.copy(
            nationality = "",
            address = "",
            zipCode = "",
            city = "",
            country = ""
        )

        // Set the content to test
        composeTestRule.setContent {
            PassportCard(identityDocument = incompleteDocument)
        }

        // Assertions for existing and missing data
        composeTestRule.onNodeWithText("PASSPORT").assertExists() // Header text
        composeTestRule.onNodeWithText("P").assertExists() // Type
        composeTestRule.onNodeWithText("13A000026").assertExists() // Document number
        composeTestRule.onNodeWithText("Doe".uppercase()).assertExists() // Last name
        composeTestRule.onNodeWithText("John, Peter, Maxwell").assertExists() // First name
        composeTestRule.onNodeWithText("FRA").assertExists() // Nationality

        // Ensure missing data nodes are not displayed
        composeTestRule.onNodeWithText("Boulevard du General de Gaule")
            .assertDoesNotExist() // Address
        composeTestRule.onNodeWithText("75001 Saint Romain en Provence")
            .assertDoesNotExist() // Zip code and city
        composeTestRule.onNodeWithText("France").assertDoesNotExist() // Country
    }
}