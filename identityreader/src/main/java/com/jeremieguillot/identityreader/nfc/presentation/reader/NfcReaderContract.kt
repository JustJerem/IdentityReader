package com.jeremieguillot.identityreader.nfc.presentation.reader

import android.nfc.Tag
import com.jeremieguillot.identityreader.core.domain.IdentityDocument
import com.jeremieguillot.identityreader.nfc.domain.NfcReaderStatus
import com.jeremieguillot.identityreader.scan.domain.DocumentValidityIssue

class NfcReaderContract {

    sealed class NfcReaderEvent {
        class NavigateToIdentityDisplay(val doc: IdentityDocument) : NfcReaderEvent()
    }

    sealed class NfcReaderIntent {
        data class ProcessTag(val tag: Tag) : NfcReaderIntent()
        data object DismissDialogs : NfcReaderIntent()
    }

    data class NfcReaderState(
        val status: NfcReaderStatus = NfcReaderStatus.IDLE,
        val identityDocument: IdentityDocument? = null,
        val irregularities: List<DocumentValidityIssue> = emptyList(),
        val showExpirationDialog: Boolean = false,
        val isNFCTimedOut: Boolean = false
    )
}