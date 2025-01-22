package com.jeremieguillot.identityreader.nfc.presentation.reader.components.documentcard.identitycard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeremieguillot.identityreader.R
import com.jeremieguillot.identityreader.core.domain.DocumentType
import com.jeremieguillot.identityreader.core.domain.IdentityDocument

@Composable
fun FrontStayCard(
    modifier: Modifier = Modifier,
    identityDocument: IdentityDocument,
) {
    IdentityCardHolder(modifier) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                Text(
                    text = stringResource(R.string.identity_card_title).uppercase(),
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp,
                )
                Box(
                    Modifier.align(
                        Alignment.TopStart
                    )
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = identityDocument.issuingIsO3Country.uppercase(),
                        color = Color(0xFF003399),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                }
            }
            Row {
                Icon(
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxHeight(),
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.LightGray
                )
                Column(
                    modifier = Modifier
                        .weight(7f)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Name Row
                    IdentityField(
                        stringResource(R.string.last_name),
                        identityDocument.lastName.uppercase(),
                        fontSize = IdentityFieldFontSize.LARGE
                    )
                    IdentityField(
                        stringResource(R.string.first_names),
                        identityDocument.firstName,
                        fontSize = IdentityFieldFontSize.LARGE
                    )

                    // Nationality, Gender, Birthdate Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IdentityField(stringResource(R.string.gender), identityDocument.gender)
                        IdentityField(
                            stringResource(R.string.nationality),
                            identityDocument.nationality
                        )
                        IdentityField(
                            stringResource(R.string.birth_date),
                            identityDocument.birthDate
                        )
                    }

                    // Document Number and Origin
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IdentityField(
                            stringResource(R.string.permit_category),
                            stringResource(R.string.permit_title)
                        )
                        IdentityField(
                            stringResource(R.string.expiration_date_short),
                            identityDocument.expirationDate
                        )
                    }

                    DocumentField(
                        stringResource(R.string.personal_number),
                        identityDocument.documentNumber
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FrontStayCardPreview() {
    val document = IdentityDocument(
        type = DocumentType.ID_CARD,
        documentNumber = "13A000026",
        issuingIsO3Country = "FRA",
        lastName = "Doe",
        firstName = "John, Peter, Maxwell",
        nationality = "Francaise",
        gender = "M",
        birthDate = "03/04/1982",
        expirationDate = "23/12/2045",
        placeOfBirth = "Strasbourg",
        address = "Main Street",
        zipCode = "75001",
        city = "Paris",
        country = "France"
    )

    Column(modifier = Modifier.padding(16.dp)) {
        FrontStayCard(identityDocument = document)
    }
}

@Preview(showBackground = true)
@Composable
fun FrontStayCardPreviewWithMissingData() {
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
        zipCode = "",  // Missing data
        city = "Paris",
        country = ""  // Missing data
    )
    Column(modifier = Modifier.padding(16.dp)) {
        FrontStayCard(identityDocument = document)
    }
}
