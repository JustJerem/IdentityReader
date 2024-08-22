package com.jeremieguillot.identityreader.nfc.presentation.reader.components.documentcard.identitycard

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeremieguillot.identityreader.R
import com.jeremieguillot.identityreader.core.domain.DocumentType
import com.jeremieguillot.identityreader.core.domain.IdentityDocument

@Composable
fun FrontIdentityCard(
    modifier: Modifier = Modifier,
    identityDocument: IdentityDocument,
) {
    IdentityCardHolder(modifier) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                Text(
                    text = "Pièce d'identité".uppercase(),
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    color = Color(0xFF003399),
                )
                Box(
                    Modifier.align(
                        Alignment.TopEnd
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.europe_flag),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .height(23.dp)
                    )
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = identityDocument.issuingIsO3Country.uppercase().take(2),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
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
                        "NOM",
                        identityDocument.lastName.uppercase(),
                        fontSize = IdentityFieldFontSize.LARGE
                    )
                    IdentityField(
                        "Prénoms",
                        identityDocument.firstName,
                        fontSize = IdentityFieldFontSize.LARGE
                    )


                    // Nationality, Gender, Birthdate Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IdentityField("SEXE", identityDocument.gender)
                        IdentityField("NATIONALITÉ", identityDocument.nationality)
                        IdentityField(
                            "DATE DE NAISS.",
                            identityDocument.birthDate
                        )
                    }
                    IdentityField("LIEU DE NAISSANCE", identityDocument.placeOfBirth)


                    // Document Number and Origin
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DocumentField("N° DU DOCUMENT", identityDocument.documentNumber)
                        IdentityField(
                            "DATE D'EXPIR",
                            identityDocument.expirationDate
                        )
                    }
                }
            }
        }
    }
}

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition()
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000)
        ), label = "startOffsetX"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFE2E1E1),
                Color(0xFFB8B5B5),
                Color(0xFFE2E1E1),
//                    Color(0xFFB8B5B5),
//                Color(0xFF8F8B8B),
//                Color(0xFFB8B5B5),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size = it.size
        }
}

@Composable
fun IdentityField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    fontSize: IdentityFieldFontSize = IdentityFieldFontSize.SMALL
) {
    Column(modifier.padding(top = 2.dp)) {
        IdentityFieldLabel(label)
        if (value.isEmpty()) {
            // Shimmer effect for loading state
            Text(
                text = "Loading...",
                color = Color.Transparent,
                modifier = Modifier
                    .height(fontSize.size.dp)
                    .shimmerEffect()
            )
        } else {
            val textUnit = fontSize.size.sp
            Text(
                lineHeight = textUnit,
                text = value,
                fontWeight = FontWeight.W500,
                color = Color.Black,
                fontSize = textUnit
            )
        }
    }
}

enum class IdentityFieldFontSize(val size: Int) {
    LARGE(16),
    SMALL(13),
    EXTRA_SMALL(12)
}

@Composable
fun DocumentField(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        IdentityFieldLabel(label)
        val textUnit = 18.sp
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            fontSize = textUnit,
            lineHeight = textUnit,
        )
    }
}

@Composable
fun IdentityFieldLabel(label: String) {
    val textUnit = 9.sp
    Text(
        text = label,
        fontSize = textUnit,
        lineHeight = textUnit,
        fontStyle = FontStyle.Italic,
        color = Color.Gray
    )
}

@Preview(showBackground = true)
@Composable
fun IdentityCardPreview() {
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
        postalCode = "75001",
        city = "Paris",
        country = "France"
    )

    Column(modifier = Modifier.padding(16.dp)) {
        FrontIdentityCard(identityDocument = document)
    }
}

@Preview(showBackground = true)
@Composable
fun IdentityCardPreviewWithMissingData() {
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
        FrontIdentityCard(identityDocument = document)
    }
}
