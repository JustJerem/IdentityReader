package com.jeremieguillot.identityreader.core.domain

import android.os.Parcelable
import com.sncf.android.internal.identityreader.core.extension.fromYYMMDDtoDate
import com.sncf.android.internal.identityreader.core.extension.toLocaleDateStringSeparated
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
    var zipCode: String = "",
    var city: String = "",
    val country: String = "",
) : Parcelable {
    companion object {
        fun toIdentityDocument(dataDocument: DataDocument): IdentityDocument {
            return IdentityDocument(
                type = dataDocument.type,
                documentNumber = dataDocument.documentNumber,
                issuingIsO3Country = Locale("", dataDocument.issuingCountry).isO3Country,
                lastName = dataDocument.lastName,
                firstName = dataDocument.firstName,
                nationality = Locale("", dataDocument.nationality).country,
                gender = dataDocument.sex,
                placeOfBirth = "",
                birthDate = dataDocument.dateOfBirth.fromYYMMDDtoDate()
                    ?.toLocaleDateStringSeparated().orEmpty(),
                expirationDate = dataDocument.dateOfExpiry.fromYYMMDDtoDate()
                    ?.toLocaleDateStringSeparated().orEmpty(),
                deliveryDate = dataDocument.deliveryDate.fromYYMMDDtoDate()
                    ?.toLocaleDateStringSeparated().orEmpty(),
                address = "",  // Missing data
                zipCode = "",  // Missing data
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
