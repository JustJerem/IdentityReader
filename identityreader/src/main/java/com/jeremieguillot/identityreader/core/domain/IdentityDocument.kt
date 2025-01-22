package com.jeremieguillot.identityreader.core.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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
) : Parcelable