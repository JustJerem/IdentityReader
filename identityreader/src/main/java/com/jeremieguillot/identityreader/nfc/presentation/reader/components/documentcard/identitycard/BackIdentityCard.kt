package com.jeremieguillot.identityreader.nfc.presentation.reader.components.documentcard.identitycard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeremieguillot.identityreader.core.domain.DocumentType
import com.jeremieguillot.identityreader.core.domain.IdentityDocument

@Composable
fun BackIdentityCard(
    modifier: Modifier = Modifier,
    identityDocument: IdentityDocument,
) {
    IdentityCardHolder(modifier) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IdentityField("DATE DE DELIVRANCE", identityDocument.deliveryDate)
            }
            AddressField(
                "Adresse",
                listOf(
                    identityDocument.address,
                    "${identityDocument.postalCode} ${identityDocument.city}",
                    identityDocument.country
                )
            )
        }
    }
}

@Composable
fun AddressField(label: String, values: List<String>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        IdentityFieldLabel(label)
        if (values.any { it.isEmpty() }) {
            // Shimmer effect for loading state
            ShimmerBox(120)
            ShimmerBox(80)
            ShimmerBox(50)
        } else {
            values.forEachIndexed { index, s ->
                val size =
                    if (index + 1 == values.size) IdentityFieldFontSize.EXTRA_SMALL.size else IdentityFieldFontSize.SMALL.size
                Text(
                    lineHeight = size.sp,
                    text = s,
                    fontWeight = FontWeight.W500,
                    color = Color.Black,
                    fontSize = size.sp
                )
            }
        }
    }
}

@Composable
private fun ShimmerBox(width: Int) {
    Box(
        modifier = Modifier
            .height(16.dp)
            .width(width.dp)
            .shimmerEffect()
    )
}

@Preview(showBackground = true)
@Composable
fun BackCardPreview() {
    val document = IdentityDocument(
        type = DocumentType.ID_CARD,
        documentNumber = "13A000026",
        issuingIsO3Country = "FRA",
        lastName = "Doe",
        firstName = "John, Peter, Maxwell",
        nationality = "French",
        gender = "M",
        birthDate = "03/04/1982",
        expirationDate = "23/12/2045",
        placeOfBirth = "Strasbourg",
        address = "Main Street",
        postalCode = "75001",
        city = "Paris",
        country = "France"
    )

    Column(modifier = Modifier.padding(16.dp)) {
        BackIdentityCard(identityDocument = document)
    }
}

@Preview(showBackground = true)
@Composable
fun BackCardPreviewWithMissingData() {
    val document = IdentityDocument(
        type = DocumentType.ID_CARD,
        documentNumber = "13A000026",
        issuingIsO3Country = "FRA",
        lastName = "Doe",
        firstName = "John, Peter, Maxwell, Alexander",
        nationality = "",  // Missing data
        gender = "M",
        placeOfBirth = "",
        birthDate = "03/04/1982",
        expirationDate = "23/12/2045",
        address = "",  // Missing data
        postalCode = "",  // Missing data
        city = "Paris",
        country = ""  // Missing data
    )
    Column(modifier = Modifier.padding(16.dp)) {
        BackIdentityCard(identityDocument = document)
    }
}
