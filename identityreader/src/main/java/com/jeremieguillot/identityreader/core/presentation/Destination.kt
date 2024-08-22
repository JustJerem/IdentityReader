package com.jeremieguillot.identityreader.core.presentation

import com.jeremieguillot.identityreader.core.domain.DataDocument
import kotlinx.serialization.Serializable

@Serializable
sealed class Destination {

    @Serializable
    data object ScannerScreen : Destination()

    @Serializable
    data class ReaderScreen(val dataDocument: DataDocument) : Destination()
}