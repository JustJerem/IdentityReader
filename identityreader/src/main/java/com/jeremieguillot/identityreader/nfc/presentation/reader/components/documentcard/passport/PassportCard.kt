package com.jeremieguillot.identityreader.nfc.presentation.reader.components.documentcard.passport

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.documentcard.identitycard.AddressField
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.documentcard.identitycard.IdentityField
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.documentcard.identitycard.IdentityFieldFontSize

@Composable
fun PassportCard(
    modifier: Modifier = Modifier,
    identityDocument: IdentityDocument,
) {
    ElevatedCard(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
    ) {

        Column(modifier = Modifier.padding(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.weight(3f),
                    text = stringResource(R.string.passport).uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    color = Color.Red,
                )
                Row(
                    modifier = Modifier
                        .weight(7f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IdentityField(stringResource(R.string.type), "P")
                    IdentityField(
                        stringResource(R.string.country_code),
                        identityDocument.issuingIsO3Country
                    )
                    IdentityField(
                        stringResource(R.string.document_number),
                        identityDocument.documentNumber
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
                    ) {
                        IdentityField(
                            stringResource(R.string.nationality),
                            identityDocument.nationality
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        IdentityField(stringResource(R.string.gender), identityDocument.gender)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IdentityField(
                            stringResource(R.string.birth_date),
                            identityDocument.birthDate
                        )
                        IdentityField(
                            stringResource(R.string.place_of_birth),
                            identityDocument.placeOfBirth
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            IdentityField(
                                stringResource(R.string.delivery_date),
                                identityDocument.deliveryDate
                            )
                            IdentityField(
                                stringResource(R.string.expiration_date),
                                identityDocument.expirationDate
                            )
                        }

                        AddressField(
                            stringResource(R.string.address),
                            listOf(
                                identityDocument.address,
                                "${identityDocument.zipCode} ${identityDocument.city}",
                                identityDocument.country
                            ),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun IdentityCardPreview() {
    val document = IdentityDocument(
        type = DocumentType.PASSPORT,
        documentNumber = "13A000026",
        issuingIsO3Country = "FRA",
        lastName = "Doe",
        firstName = "John, Peter, Maxwell",
        nationality = "French",
        gender = "M",
        birthDate = "03/04/1982",
        expirationDate = "23/12/2045",
        placeOfBirth = "Strasbourg",
        address = "Boulevard du General de Gaule",
        zipCode = "75001",
        city = "Saint Romain en Provence",
        country = "France"
    )

    Column(modifier = Modifier.padding(16.dp)) {
        PassportCard(identityDocument = document)
    }
}

@Preview(showBackground = true)
@Composable
fun IdentityCardPreviewWithMissingData() {
    val document = IdentityDocument(
        type = DocumentType.PASSPORT,
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
        PassportCard(identityDocument = document)
    }
}
