package com.jeremieguillot.identityreader.nfc.presentation.reader.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.jeremieguillot.identityreader.R

@Composable
fun ErrorDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onDismissRequest: () -> Unit = onDismiss,  // Optional parameter
    title: String = stringResource(R.string.technical_error_title),
    text: String = stringResource(R.string.technical_error_description),
    dismissButtonText: String = stringResource(R.string.to_continue),
) {
    if (showDialog) {
        AlertDialog(
            containerColor = Color.White,
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(dismissButtonText)
                }
            },
            title = {
                Text(title)
            },
            text = {
                Text(text)
            }
        )
    }
}
