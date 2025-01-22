package com.jeremieguillot.identityreader.nfc.presentation.reader

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.util.Consumer
import com.jeremieguillot.identityreader.R
import com.jeremieguillot.identityreader.ReaderActivity
import com.jeremieguillot.identityreader.core.domain.DataDocument
import com.jeremieguillot.identityreader.core.domain.IdentityDocument
import com.jeremieguillot.identityreader.core.domain.IdentityDocument.Companion.toIdentityDocument
import com.jeremieguillot.identityreader.core.domain.util.Result
import com.jeremieguillot.identityreader.nfc.data.NFCReader
import com.jeremieguillot.identityreader.nfc.domain.NfcReaderStatus
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.ExpirationDialog
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.IrregularDataDialog
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.RippleEffect
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.documentcard.FlippableCard
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.getDescription
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.getTitle
import com.jeremieguillot.identityreader.scan.domain.DocumentValidityAnalyzer
import com.jeremieguillot.identityreader.scan.domain.DocumentValidityIssue
import com.jeremieguillot.identityreader.scan.presentation.processDocument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber


@Composable
fun NfcReaderScreen(
    dataDocument: DataDocument,
    returnIdentityDocumentResult: (IdentityDocument) -> Unit,
) {

    val context = LocalContext.current
    val scope = CoroutineScope(Dispatchers.Default)
    val reader = remember { NFCReader(dataDocument) }
    val identityDocument =
        remember { mutableStateOf(toIdentityDocument(dataDocument)) }
    val status by reader.status.collectAsState(NfcReaderStatus.IDLE)
    var identity by remember { mutableStateOf<IdentityDocument?>(null) }

    var showExpirationDialog by remember { mutableStateOf(false) }
    var isNFCTimedOut by remember { mutableStateOf(false) }

    val irregularities = remember { mutableStateListOf<DocumentValidityIssue>() }

    LaunchedEffect(Unit) {
        delay(8000L) // 8 seconds delay
        isNFCTimedOut = true
    }

    ExpirationDialog(
        showDialog = showExpirationDialog,
        onDismiss = {
            showExpirationDialog = false
            (context as ReaderActivity).finish()
        }, onConfirm = {
            returnIdentityDocumentResult(identity!!)
        }
    )

    if (irregularities.isNotEmpty()) {
        IrregularDataDialog(
            irregularities = irregularities,
            onDismiss = {
                irregularities.clear()
                returnIdentityDocumentResult(identity!!)
            }
        )
    }

    DisposableEffect(Unit) {
        val listener = Consumer<Intent> { intent ->
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            if (tag!!.techList.contains("android.nfc.tech.IsoDep")) {
                scope.launch {
                    when (val result = reader.onTagDiscovered(tag, dataDocument.type)) {
                        is Result.Error -> {
                            //what to do ?
                        }

                        is Result.Success -> {
                            identity = result.data
                            processNFCDocument(
                                identity = identity,
                                showDialog = { showExpirationDialog = true },
                                showIrregularDataDialog = { irregularities.addAll(it) },
                                returnIdentityDocumentResult = returnIdentityDocumentResult
                            )
                        }
                    }
                }
            } else {
                Timber.tag("ERROR").e("Error isoDep")
            }
        }
        (context as ReaderActivity).addOnNewIntentListener(listener)
        onDispose { context.removeOnNewIntentListener(listener) }
    }


    Scaffold(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {

            FlippableCard(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                cardModifier = Modifier.padding(6.dp),
                identityDocument = identityDocument.value
            )

            RippleEffect(
                status.color,
                Modifier
                    .align(Alignment.Center)
                    .padding(top = 32.dp)
            )

            Column(
                modifier = Modifier
                    .animateContentSize()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    status.getTitle(), fontSize = 14.sp, fontWeight = FontWeight.SemiBold
                )
                Text(status.getDescription(), textAlign = TextAlign.Center, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                AnimatedVisibility(visible = isNFCTimedOut) {
                    Button(onClick = {
                        identity = toIdentityDocument(dataDocument)
                        processDocument(
                            identity = identity,
                            showDialog = { showExpirationDialog = true },
                            returnIdentityDocumentResult = returnIdentityDocumentResult
                        )
                    }) {
                        Text(
                            text = stringResource(R.string.scan_without_nfc),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

fun processNFCDocument(
    identity: IdentityDocument?,
    showDialog: () -> Unit,
    showIrregularDataDialog: (List<DocumentValidityIssue>) -> Unit,
    returnIdentityDocumentResult: (IdentityDocument) -> Unit,
) {
    identity?.let { doc ->
        val analyzer = DocumentValidityAnalyzer(doc)
        val irregularities = analyzer.processIrregularities()

        when {
            analyzer.expirationDateIsInThePast() -> showDialog()
            irregularities.isNotEmpty() && doc.type.hasNfcChip() -> showIrregularDataDialog(
                irregularities
            )

            else -> returnIdentityDocumentResult(doc)
        }
    }
}