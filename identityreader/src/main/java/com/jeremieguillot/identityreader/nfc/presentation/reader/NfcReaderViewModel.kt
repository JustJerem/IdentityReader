package com.jeremieguillot.identityreader.nfc.presentation.reader

import android.nfc.Tag
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeremieguillot.identityreader.core.domain.DataDocument
import com.jeremieguillot.identityreader.core.domain.toIdentityDocument
import com.jeremieguillot.identityreader.core.domain.util.Result
import com.jeremieguillot.identityreader.nfc.data.NFCReader
import com.jeremieguillot.identityreader.nfc.domain.NfcReaderStatus
import com.jeremieguillot.identityreader.nfc.presentation.reader.NfcReaderContract.NfcReaderEvent
import com.jeremieguillot.identityreader.nfc.presentation.reader.NfcReaderContract.NfcReaderIntent
import com.jeremieguillot.identityreader.nfc.presentation.reader.NfcReaderContract.NfcReaderState
import com.jeremieguillot.identityreader.scan.domain.DocumentValidityAnalyzer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class NfcReaderViewModel(private val dataDocument: DataDocument) : ViewModel() {

    private val nfcReader = NFCReader(dataDocument)

    private val _state = MutableStateFlow(NfcReaderState())
    val state: StateFlow<NfcReaderState> = _state

    private val _eventChannel = Channel<NfcReaderEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        _state.update { it.copy(identityDocument = dataDocument.toIdentityDocument()) }
        handleTimeout()
    }

    fun handleIntent(intent: NfcReaderIntent) {
        when (intent) {
            is NfcReaderIntent.ProcessTag -> processTag(intent.tag)
            NfcReaderIntent.DismissDialogs -> dismissDialogs()
        }
    }

    private fun handleTimeout() {
        viewModelScope.launch {
            delay(8000L)
            _state.update { it.copy(isNFCTimedOut = true) }
        }
    }

    private fun processTag(tag: Tag) {
        viewModelScope.launch {
            _state.update { it.copy(status = NfcReaderStatus.CONNECTING) }
            when (val result = nfcReader.onTagDiscovered(tag, dataDocument.type)) {
                is Result.Success -> {
                    val doc = result.data
                    val documentValidityAnalyzer = DocumentValidityAnalyzer(doc)
                    val irregularities = documentValidityAnalyzer.processIrregularities()

                    when {
                        documentValidityAnalyzer.expirationDateIsInThePast() -> _state.update {
                            it.copy(
                                identityDocument = doc,
                                showExpirationDialog = true
                            )
                        }

                        irregularities.isNotEmpty() -> _state.update {
                            it.copy(
                                identityDocument = doc,
                                irregularities = irregularities
                            )
                        }

                        else -> {
                            _state.update {
                                it.copy(
                                    identityDocument = doc,
                                    status = NfcReaderStatus.CONNECTED,
                                )
                            }
                            _eventChannel.send(NfcReaderEvent.NavigateToIdentityDisplay(doc))
                        }
                    }
                }

                is Result.Error -> _state.update { it.copy(status = NfcReaderStatus.ERROR) }
            }
        }
    }

    private fun dismissDialogs() {
        _state.update { it.copy(showExpirationDialog = false, irregularities = emptyList()) }
    }
}