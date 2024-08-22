package com.jeremieguillot.identityreader.nfc.presentation.reader.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jeremieguillot.identityreader.R
import com.jeremieguillot.identityreader.core.domain.MRZ
import com.jeremieguillot.identityreader.core.extension.addSpaceEveryNChars
import com.jeremieguillot.identityreader.core.extension.fromYYMMDDtoDate
import com.jeremieguillot.identityreader.core.extension.toLocaleDateStringSeparated
import com.jeremieguillot.identityreader.core.ui.theme.IdentityReaderTheme

@Composable
fun MRZCard(mrz: MRZ, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                annotatedString(
                    stringResource(R.string.document_number_dots),
                    mrz.documentNumber.addSpaceEveryNChars(3)
                )
            )
            Text(
                annotatedString(
                    stringResource(R.string.birthdate_dots),
                    mrz.dateOfBirth.fromYYMMDDtoDate()?.toLocaleDateStringSeparated() ?: ""
                )
            )
            Text(
                annotatedString(
                    stringResource(R.string.expiration_date_dots),
                    mrz.dateOfExpiry.fromYYMMDDtoDate()?.toLocaleDateStringSeparated() ?: ""
                )
            )
        }
    }
}

fun annotatedString(label: String, value: String): AnnotatedString {
    return buildAnnotatedString {
        append(label)
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(value)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MRZCardPreview() {
    IdentityReaderTheme {
        MRZCard(
            mrz = MRZ(
                documentNumber = "123456789",
                dateOfBirth = "920819",
                dateOfExpiry = "330606"
            )
        )
    }
}
