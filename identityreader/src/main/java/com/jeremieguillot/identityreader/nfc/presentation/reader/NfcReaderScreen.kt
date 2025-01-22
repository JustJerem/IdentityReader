package com.jeremieguillot.identityreader.nfc.presentation.reader

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.util.Consumer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeremieguillot.identityreader.R
import com.jeremieguillot.identityreader.ReaderActivity
import com.jeremieguillot.identityreader.core.domain.IdentityDocument
import com.jeremieguillot.identityreader.nfc.presentation.reader.NfcReaderContract.NfcReaderEvent
import com.jeremieguillot.identityreader.nfc.presentation.reader.NfcReaderContract.NfcReaderIntent
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.ExpirationDialog
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.IrregularDataDialog
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.RippleEffect
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.documentcard.FlippableCard
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.getDescription
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.getTitle
import com.jeremieguillot.identityreader.scan.domain.DocumentValidityAnalyzer
import com.jeremieguillot.identityreader.scan.domain.DocumentValidityIssue
import timber.log.Timber


@Composable
fun NfcReaderScreen(
    navigateToIdentityDisplay: (IdentityDocument) -> Unit,
    viewModel: NfcReaderViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current as ReaderActivity
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is NfcReaderEvent.NavigateToIdentityDisplay -> {
                    navigateToIdentityDisplay(event.doc)
                }
            }
        }
    }


    DisposableEffect(Unit) {
        val listener = Consumer<Intent> { intent ->
            val tag: Tag? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            }

            if (tag != null && tag.techList.contains("android.nfc.tech.IsoDep")) {
                viewModel.handleIntent(NfcReaderIntent.ProcessTag(tag))
            } else {
                Timber.tag("ERROR").e("Error: Unsupported tag detected")
            }
        }
        context.addOnNewIntentListener(listener)
        onDispose {
            context.removeOnNewIntentListener(listener)
        }
    }

    if (state.showExpirationDialog) {
        ExpirationDialog(
            onDismiss = { viewModel.handleIntent(NfcReaderIntent.DismissDialogs) },
            onConfirm = { state.identityDocument?.let(navigateToIdentityDisplay) }
        )
    }

    if (state.irregularities.isNotEmpty()) {
        IrregularDataDialog(
            irregularities = state.irregularities,
            onDismiss = {
                viewModel.handleIntent(NfcReaderIntent.DismissDialogs)
                state.identityDocument?.let(navigateToIdentityDisplay)
            }
        )
    }

    Scaffold(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {

            state.identityDocument?.let { identityDocument ->
                FlippableCard(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    cardModifier = Modifier.padding(6.dp),
                    identityDocument = identityDocument
                )
            }

            RippleEffect(
                state.status.color,
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
                    state.status.getTitle(), fontSize = 14.sp, fontWeight = FontWeight.SemiBold
                )
                Text(state.status.getDescription(), textAlign = TextAlign.Center, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                AnimatedVisibility(visible = state.isNFCTimedOut) {
                    Button(onClick = {
                        state.identityDocument?.let { identityDocument ->
                            navigateToIdentityDisplay(
                                identityDocument
                            )
                        }
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