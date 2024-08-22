package com.jeremieguillot.example

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jeremieguillot.identityreader.core.domain.IdentityDocument

@Composable
fun DisplayIdentity(identity: IdentityDocument?) {
    if (identity != null) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Document", fontWeight = FontWeight.Bold)

            DisplayRow(label = "Type", value = identity.type.name)
            DisplayRow(label = "Document Number", value = identity.documentNumber)
            DisplayRow(label = "Issuing Country", value = identity.issuingIsO3Country)
            DisplayRow(label = "Delivery Date", value = identity.deliveryDate)
            DisplayRow(label = "Expiration Date", value = identity.expirationDate)

            Text(text = " ")
            Text(text = " ")
            Text(text = "Identité", fontWeight = FontWeight.Bold)

            DisplayRow(label = "Last Name", value = identity.lastName)
            DisplayRow(label = "First Name", value = identity.firstName)
            DisplayRow(label = "Birth Date", value = identity.birthDate)
            DisplayRow(label = "Nationality", value = identity.nationality)
            DisplayRow(label = "Gender", value = identity.gender)
            // DisplayRow(label = "Address Number", value = identity.addressNumber)
            DisplayRow(label = "Address", value = identity.address)
            DisplayRow(label = "Postal Code", value = identity.postalCode)
            DisplayRow(label = "City", value = identity.city)
            DisplayRow(label = "Country", value = identity.country)
        }
    }
}

@Composable
fun DisplayRow(label: String, value: String) {
    Row {
        Text(text = "$label: ", fontWeight = FontWeight.Bold)
        Text(text = value)
    }
}
