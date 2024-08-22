package com.jeremieguillot.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import com.jeremieguillot.identityreader.ReaderActivity
import com.jeremieguillot.identityreader.ReaderResult
import com.jeremieguillot.identityreader.core.domain.IdentityDocument

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val context = LocalContext.current
            val haptic = LocalHapticFeedback.current
            var identity by remember { mutableStateOf<IdentityDocument?>(null) }

            val startForResult =
                rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                    if (result.resultCode == RESULT_OK) {
                        val test = result.data?.getParcelableExtra<IdentityDocument>(ReaderResult)
                        identity = test
                    }
                }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    val intent = Intent(
                        context,
                        ReaderActivity::class.java
                    )
                    startForResult.launch(intent)

                }) {
                    Text(text = "Scanner une pièce d'identité")
                }

                DisplayIdentity(identity)
            }
        }
    }
}