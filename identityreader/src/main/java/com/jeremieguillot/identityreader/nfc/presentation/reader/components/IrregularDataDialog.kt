package com.jeremieguillot.identityreader.nfc.presentation.reader.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jeremieguillot.identityreader.R
import com.jeremieguillot.identityreader.core.ui.theme.IdentityReaderTheme
import com.jeremieguillot.identityreader.scan.domain.DocumentValidityIssue

@Composable
fun IrregularDataDialog(
    irregularities: List<DocumentValidityIssue>,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        containerColor = Color.White,
        onDismissRequest = { onDismiss() },
        title = { Text(text = stringResource(R.string.missing_information)) },
        text = {
            Column {
                Text(stringResource(R.string.some_fields_could_not_be_read))
                Spacer(modifier = Modifier.height(8.dp))
                irregularities.forEach { issue ->
                    Text(
                        fontWeight = FontWeight.SemiBold,
                        text = "- " + when (issue) {
                            DocumentValidityIssue.INCORRECT_CITY_ZIPCODE -> stringResource(R.string.incorrect_city_zipcode)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.please_fill_document))
            }
        },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("OK")
            }
        }
    )
}

@Preview
@Composable
private fun IrregularDataDialogPrev() {
    IdentityReaderTheme {
        IrregularDataDialog(
            listOf(
                DocumentValidityIssue.INCORRECT_CITY_ZIPCODE
            )
        ) {}
    }
}