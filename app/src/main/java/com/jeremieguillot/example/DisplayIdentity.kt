package com.jeremieguillot.example

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jeremieguillot.identityreader.core.domain.IdentityDocument

@Composable
fun DisplayIdentity(identity: IdentityDocument?) {
    identity?.let {
        Column(modifier = Modifier.padding(16.dp)) {
            DocumentSection(identity)
            Spacer(modifier = Modifier.height(16.dp))
            IdentitySection(identity)
        }
    } ?: Text(
        text = stringResource(R.string.no_identity_available),
        modifier = Modifier.padding(16.dp),
    )
}

@Composable
fun DocumentSection(identity: IdentityDocument) {
    SectionHeader(stringResource(R.string.document_section))

    val documentDetails = listOf(
        stringResource(R.string.type_label) to identity.type.name,
        stringResource(R.string.document_number_label) to identity.documentNumber,
        stringResource(R.string.issuing_country_label) to identity.issuingIsO3Country,
        stringResource(R.string.delivery_date_label) to identity.deliveryDate,
        stringResource(R.string.expiration_date_label) to identity.expirationDate
    )

    DisplayRows(documentDetails)
}

@Composable
fun IdentitySection(identity: IdentityDocument) {
    SectionHeader(stringResource(R.string.identity_section))

    val identityDetails = listOf(
        stringResource(R.string.last_name_label) to identity.lastName,
        stringResource(R.string.first_name_label) to identity.firstName,
        stringResource(R.string.birth_date_label) to identity.birthDate,
        stringResource(R.string.nationality_label) to identity.nationality,
        stringResource(R.string.gender_label) to identity.gender,
        stringResource(R.string.address_label) to identity.address,
        stringResource(R.string.postal_code_label) to identity.zipCode,
        stringResource(R.string.city_label) to identity.city,
        stringResource(R.string.country_label) to identity.country
    )

    DisplayRows(identityDetails)
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun DisplayRows(details: List<Pair<String, String>>) {
    details.forEach { (label, value) ->
        DisplayRow(label = label, value = value)
    }
}

@Composable
fun DisplayRow(label: String, value: String) {
    Row {
        Text(text = "$label: ", fontWeight = FontWeight.Bold)
        Text(text = value)
    }
}
