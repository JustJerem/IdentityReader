package com.jeremieguillot.identityreader.core.domain

import android.os.Parcelable
import com.jeremieguillot.identityreader.core.extension.fromYYMMDDtoDate
import com.jeremieguillot.identityreader.core.extension.toLocaleDateStringSeparated
import kotlinx.parcelize.Parcelize
import java.util.Locale

@Parcelize
data class IdentityDocument(
    val type: DocumentType,
    val documentNumber: String = "",
    val expirationDate: String = "",
    val deliveryDate: String = "",
    val issuingIsO3Country: String = "",
    val size: Int = 0,
    val lastName: String = "",
    val firstName: String = "",
    val nationality: String = "",
    val birthDate: String = "",
    val placeOfBirth: String = "",
    val gender: String = "",
    val address: String = "",
    val postalCode: String = "",
    val city: String = "",
    val country: String = "",
) : Parcelable {
    companion object {
        fun toIdentityDocument(dataDocument: DataDocument): IdentityDocument {
            return IdentityDocument(
                type = dataDocument.type,
                documentNumber = dataDocument.documentNumber,
                issuingIsO3Country = Locale("", dataDocument.issuingCountry).isO3Country,
                lastName = dataDocument.lastName,
                firstName = dataDocument.firstName,// Missing data
                nationality = Locale("", dataDocument.nationality).country,
                gender = dataDocument.sex,
                placeOfBirth = "",
                birthDate = dataDocument.dateOfBirth.fromYYMMDDtoDate()
                    ?.toLocaleDateStringSeparated() ?: "",
                expirationDate = dataDocument.dateOfExpiry.fromYYMMDDtoDate()
                    ?.toLocaleDateStringSeparated() ?: "",
                deliveryDate = dataDocument.deliveryDate.fromYYMMDDtoDate()
                    ?.toLocaleDateStringSeparated() ?: "",
                address = "",  // Missing data
                postalCode = "",  // Missing data
                city = "",
                country = ""  // Missing data
            )
        }
    }
}

fun Int.toHumanReadableHeight(): String {
    if (this == 0) return ""
    val meters = this / 100
    val centimeters = this % 100
    return "${meters},${centimeters}m"
}
