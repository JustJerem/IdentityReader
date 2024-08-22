package com.jeremieguillot.identityreader.nfc.presentation.reader.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jeremieguillot.identityreader.R

@Composable
fun ExpirationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit = onDismiss,  // Optional parameter
    title: String = stringResource(R.string.expired_document_title),
    text: String = stringResource(R.string.expired_document_desc),
    confirmButtonText: String = stringResource(R.string.yes),
    dismissButtonText: String = stringResource(R.string.no)
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(dismissButtonText)
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(confirmButtonText)
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
