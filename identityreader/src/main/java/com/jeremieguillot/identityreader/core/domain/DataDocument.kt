package com.jeremieguillot.identityreader.core.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

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