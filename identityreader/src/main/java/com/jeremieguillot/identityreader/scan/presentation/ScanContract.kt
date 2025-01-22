package com.jeremieguillot.identityreader.scan.presentation

import com.jeremieguillot.identityreader.core.domain.DataDocument
import com.jeremieguillot.identityreader.core.domain.IdentityDocument
import com.jeremieguillot.identityreader.scan.data.MRZResult

class ScanContract {

    sealed class ScanEvent {
        data class NavigateToNfcReader(val dataDocument: DataDocument) : ScanEvent()
        data class ReturnIdentityDocumentResult(val identityDocument: IdentityDocument) :
            ScanEvent()
    }

    sealed class ScanIntent {
        data class ProcessMRZResult(val result: MRZResult) : ScanIntent()
        data object ResetErrorCount : ScanIntent()
        data object DismissExpirationDialog : ScanIntent()
        data object ConfirmIdentity : ScanIntent()
    }

    data class ScanState(
        val identity: IdentityDocument? = null,
        val showExpirationDialog: Boolean = false,
        val showErrorDialog: Boolean = false
    )
}