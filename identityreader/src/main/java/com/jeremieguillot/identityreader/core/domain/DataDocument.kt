package com.jeremieguillot.identityreader.core.domain

import android.os.Parcelable
import com.jeremieguillot.identityreader.core.extension.fromYYMMDDtoDate
import com.jeremieguillot.identityreader.core.extension.toLocaleDateStringSeparated
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
@Parcelize
data class DataDocument(
    val type: DocumentType,
    val issuingCountry: String,
    val documentNumber: String,
    val lastName: String,
    val nationality: String,
    val dateOfBirth: String,
    val dateOfExpiry: String,
    val sex: String,
    val firstName: String = "",
    val deliveryDate: String = "",
) : Parcelable

fun DataDocument.toIdentityDocument(): IdentityDocument {
    return IdentityDocument(
        type = this.type,
        documentNumber = this.documentNumber,
        issuingIsO3Country = Locale("", this.issuingCountry).isO3Country,
        lastName = this.lastName,
        firstName = this.firstName,
        nationality = Locale("", this.nationality).country,
        gender = this.sex,
        placeOfBirth = "", // Missing data
        birthDate = this.dateOfBirth.fromYYMMDDtoDate()
            ?.toLocaleDateStringSeparated().orEmpty(),
        expirationDate = this.dateOfExpiry.fromYYMMDDtoDate()
            ?.toLocaleDateStringSeparated().orEmpty(),
        deliveryDate = this.deliveryDate.fromYYMMDDtoDate()
            ?.toLocaleDateStringSeparated().orEmpty(),
        address = "", // Missing data
        zipCode = "", // Missing data
        city = "", // Missing data
        country = "" // Missing data
    )
}