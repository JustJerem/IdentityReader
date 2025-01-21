package com.jeremieguillot.example

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jeremieguillot.identityreader.ReaderActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        // Initialize Espresso Intents to capture and verify intents
        Intents.init()
    }

    @After
    fun tearDown() {
        // Release Espresso Intents
        Intents.release()
    }

    @Test
    fun navigateToReaderActivity_whenScanButtonClicked() {
        // Perform click on the "Scan an ID document" button
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.scan_doc))
            .performClick()

        // Verify the intent was sent to ReaderActivity
        Intents.intended(IntentMatchers.hasComponent(ReaderActivity::class.java.name))
    }
}