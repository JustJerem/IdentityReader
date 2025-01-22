package com.jeremieguillot.identityreader.core.domain

enum class DocumentType {
    PASSPORT,
    DRIVING_LICENCE,
    RESIDENT_PERMIT,
    ID_CARD,
    OLD_ID_CARD;

    /*
     * Check if the document type has an associated NFC chip
     */
    fun hasNfcChip(): Boolean {
        return when (this) {
            PASSPORT, RESIDENT_PERMIT, ID_CARD -> true
            else -> false
        }
    }
}