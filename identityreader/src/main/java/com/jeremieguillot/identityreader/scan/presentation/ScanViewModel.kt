package com.jeremieguillot.identityreader.scan.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeremieguillot.identityreader.core.domain.DataDocument
import com.jeremieguillot.identityreader.core.domain.IdentityDocument.Companion.toIdentityDocument
import com.jeremieguillot.identityreader.scan.data.MRZResult
import com.jeremieguillot.identityreader.scan.data.MRZResult.Failure
import com.jeremieguillot.identityreader.scan.data.MRZResult.MRZError
import com.jeremieguillot.identityreader.scan.data.MRZResult.Success
import com.jeremieguillot.identityreader.scan.domain.DocumentValidityAnalyzer
import com.jeremieguillot.identityreader.scan.presentation.ScanContract.ScanEvent
import com.jeremieguillot.identityreader.scan.presentation.ScanContract.ScanIntent
import com.jeremieguillot.identityreader.scan.presentation.ScanContract.ScanUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScanViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<ScanEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var errorCount = 0

    fun processIntent(intent: ScanIntent) {
        when (intent) {
            is ScanIntent.ProcessMRZResult -> processMRZResult(intent.result)
            ScanIntent.ResetErrorCount -> resetErrorCount()
            ScanIntent.DismissExpirationDialog -> dismissExpirationDialog()
            ScanIntent.ConfirmIdentity -> confirmIdentity()
        }
    }

    private fun processMRZResult(result: MRZResult) {
        when (result) {
            is Failure -> Unit // Retry automatically
            is MRZError -> handleMRZError()
            is Success -> handleSuccess(result.data)
        }
    }

    private fun handleSuccess(data: DataDocument) {
        if (data.type.hasNfcChip()) {
            viewModelScope.launch {
                _eventChannel.send(ScanEvent.NavigateToNfcReader(data))
            }
        } else {
            val identity = toIdentityDocument(data)
            _uiState.update {
                it.copy(
                    identity = identity,
                    showExpirationDialog = DocumentValidityAnalyzer(identity).expirationDateIsInThePast()
                )
            }
        }
    }

    private fun handleMRZError() {
        errorCount++
        if (errorCount > 5) {
            _uiState.update { it.copy(showErrorDialog = true) }
        }
    }

    private fun resetErrorCount() {
        errorCount = 0
        _uiState.update { it.copy(showErrorDialog = false) }
    }

    private fun dismissExpirationDialog() {
        _uiState.update { it.copy(showExpirationDialog = false) }
    }

    private fun confirmIdentity() {
        _uiState.value.identity?.let {
            viewModelScope.launch {
                _eventChannel.send(ScanEvent.ReturnIdentityDocumentResult(it))
            }
        }
    }
}