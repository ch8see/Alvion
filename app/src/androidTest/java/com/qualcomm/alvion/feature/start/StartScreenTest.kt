package com.qualcomm.alvion.feature.start

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
// Importing the actual StartScreen from the feature package

class StartScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun startButton_isDisplayed_andCallsOnStart() {
        var started = false

        composeRule.setContent {
            StartScreen(onStart = { started = true })
        }

        composeRule
            .onNodeWithText("Start Session")
            .assertIsDisplayed()
            .performClick()

        // Using named arguments to fix the type mismatch
        assertTrue(message = "onStart should be called when Start Session is clicked", condition = started)
    }
}
