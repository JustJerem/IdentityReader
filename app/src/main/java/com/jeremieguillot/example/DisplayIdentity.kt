package com.jeremieguillot.example

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jeremieguillot.identityreader.core.domain.IdentityDocument

@Composable
fun DisplayIdentity(identity: IdentityDocument?) {
    if (identity != null) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = stringResource(R.string.document_section), fontWeight = FontWeight.Bold)

            DisplayRow(label = stringResource(R.string.type_label), value = identity.type.name)
            DisplayRow(
                label = stringResource(R.string.document_number_label),
                value = identity.documentNumber
            )
            DisplayRow(
                label = stringResource(R.string.issuing_country_label),
                value = identity.issuingIsO3Country
            )
            DisplayRow(
                label = stringResource(R.string.delivery_date_label),
                value = identity.deliveryDate
            )
            DisplayRow(
                label = stringResource(R.string.expiration_date_label),
                value = identity.expirationDate
            )

            Text(
                text = stringResource(R.string.identity_section), fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )

            DisplayRow(label = stringResource(R.string.last_name_label), value = identity.lastName)
            DisplayRow(
                label = stringResource(R.string.first_name_label),
                value = identity.firstName
            )
            DisplayRow(
                label = stringResource(R.string.birth_date_label),
                value = identity.birthDate
            )
            DisplayRow(
                label = stringResource(R.string.nationality_label),
                value = identity.nationality
            )
            DisplayRow(label = stringResource(R.string.gender_label), value = identity.gender)
            DisplayRow(label = stringResource(R.string.address_label), value = identity.address)
            DisplayRow(label = stringResource(R.string.postal_code_label), value = identity.zipCode)
            DisplayRow(label = stringResource(R.string.city_label), value = identity.city)
            DisplayRow(label = stringResource(R.string.country_label), value = identity.country)
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
