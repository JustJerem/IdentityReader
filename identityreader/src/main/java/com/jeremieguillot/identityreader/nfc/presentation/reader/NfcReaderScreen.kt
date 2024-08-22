package com.jeremieguillot.identityreader.nfc.presentation.reader

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.util.Consumer
import com.jeremieguillot.identityreader.ReaderActivity
import com.jeremieguillot.identityreader.core.domain.DataDocument
import com.jeremieguillot.identityreader.core.domain.IdentityDocument
import com.jeremieguillot.identityreader.core.domain.util.Result
import com.jeremieguillot.identityreader.nfc.data.NFCReader
import com.jeremieguillot.identityreader.nfc.domain.NfcReaderStatus
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.ExpirationDialog
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.ReaderAnimation
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.documentcard.FlippableCard
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.getDescription
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.getTitle
import com.jeremieguillot.identityreader.scan.presentation.processDocument
import com.jeremieguillot.identityreader.scan.presentation.returnIdentityDocumentResult


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NfcReaderScreen(dataDocument: DataDocument) {

    val context = LocalContext.current
    val reader = remember { NFCReader(dataDocument) }
    val identityDocument =
        remember { mutableStateOf(IdentityDocument.toIdentityDocument(dataDocument)) }
    val status by reader.status.collectAsState(NfcReaderStatus.IDLE)
    var identity by remember { mutableStateOf<IdentityDocument?>(null) }

    var showExpirationDialog by remember { mutableStateOf(false) }

    ExpirationDialog(
        showDialog = showExpirationDialog,
        onDismiss = {
            showExpirationDialog = false
            (context as ReaderActivity).finish()
        },
        onConfirm = {
            returnIdentityDocumentResult(context, identity!!)
        }
    )

    DisposableEffect(Unit) {
        val listener = Consumer<Intent> { intent ->
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            if (tag!!.techList.contains("android.nfc.tech.IsoDep")) {
                when (val result = reader.onTagDiscovered(tag)) {
                    is Result.Error -> {
                        //what to do ?
                    }

                    is Result.Success -> {
                        identity = result.data
                        processDocument(
                            context = context,
                            identity = identity,
                            showDialog = { showExpirationDialog = true }
                        )
                    }
                }
            } else {
                Log.e("ERROR", "Error isoDep")
            }

        }
        (context as ReaderActivity).addOnNewIntentListener(
            listener
        )
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

            ReaderAnimation(status, Modifier.align(Alignment.Center))

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    status.getTitle(), style = MaterialTheme.typography.titleLarge
                )
                Text(status.getDescription(), textAlign = TextAlign.Center)
            }
        }
    }
}
