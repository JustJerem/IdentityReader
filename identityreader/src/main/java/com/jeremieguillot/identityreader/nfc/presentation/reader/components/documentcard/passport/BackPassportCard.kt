package com.jeremieguillot.identityreader.nfc.presentation.reader.components.documentcard.passport

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeremieguillot.identityreader.R
import com.jeremieguillot.identityreader.core.domain.DocumentType
import com.jeremieguillot.identityreader.core.domain.IdentityDocument

@Composable
fun BackPassportCard(
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .aspectRatio(1.585f),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Ghost Icon
            Icon(
                painter = painterResource(id = R.drawable.ic_chat_info),
                contentDescription = "Ghost Icon",
                modifier = Modifier.padding(bottom = 8.dp),
                tint = Color.Gray
            )

            // Text
            Text(
                text = "Aucune information présente au dos du passeport",
                fontStyle = FontStyle.Italic,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BackPassportCardPreview() {
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
        BackPassportCard()
    }
}

