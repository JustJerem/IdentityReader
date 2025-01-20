package com.jeremieguillot.identityreader.scan.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.jeremieguillot.identityreader.ReaderActivity
import com.jeremieguillot.identityreader.ReaderResult
import com.jeremieguillot.identityreader.core.domain.DataDocument
import com.jeremieguillot.identityreader.core.domain.IdentityDocument
import com.jeremieguillot.identityreader.core.domain.IdentityDocument.Companion.toIdentityDocument
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.ErrorDialog
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.ExpirationDialog
import com.jeremieguillot.identityreader.scan.data.MRZRecognitionOCR
import com.jeremieguillot.identityreader.scan.data.MRZResult.Failure
import com.jeremieguillot.identityreader.scan.data.MRZResult.MRZError
import com.jeremieguillot.identityreader.scan.data.MRZResult.Success
import com.jeremieguillot.identityreader.scan.data.TextImageAnalyzer
import com.jeremieguillot.identityreader.scan.domain.DocumentValidityAnalyzer
import com.jeremieguillot.identityreader.scan.domain.DocumentValidityIssue

@Composable
fun ScanScreen(
    navigateToNfcReader: (DataDocument) -> Unit
) {

    val recognizer = remember { MRZRecognitionOCR() }
    val context = LocalContext.current
    var identity by remember { mutableStateOf<IdentityDocument?>(null) }
    var errorCount by remember { mutableIntStateOf(0) }
    var showExpirationDialog by remember { mutableStateOf(false) }

    ErrorDialog(
        showDialog = errorCount > 5,
        onDismiss = {
            errorCount = 0
            (context as ReaderActivity).finish()
        }
    )

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

    val analyzer = remember {
        TextImageAnalyzer(onSuccess = {
            when (val result = recognizer.recognize(it)) {
                Failure -> {/*will retry automatically*/
                }

                MRZError -> {
                    errorCount++
                }

                is Success -> {
                    val type = result.data.type
                    when {
                        type.hasNfcChip() -> navigateToNfcReader(result.data)
                        else -> {
                            identity = toIdentityDocument(result.data)
                            processDocument(
                                context = context,
                                identity = identity,
                                showDialog = { showExpirationDialog = true },
                            )
                        }
                    }
                }


            }
        }
        )
    }

    val controller = remember {
        LifecycleCameraController(context).apply {
            isPinchToZoomEnabled = false
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
            setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context), analyzer
            )
        }
    }

    Scaffold { padding ->
        OverlayScreen(
            Modifier
                .fillMaxSize()
                .padding(padding),
            controller = controller
        ) {
            CameraPreview(
                controller = controller, modifier = Modifier.fillMaxSize()
            )
        }
    }
}

fun processNFCDocument(
    context: Context,
    identity: IdentityDocument?,
    showDialog: () -> Unit,
    showIrregularDataDialog: (List<DocumentValidityIssue>) -> Unit,
) {
    identity?.let { doc ->
        val analyzer = DocumentValidityAnalyzer(doc)
        val irregularities = analyzer.processIrregularities()

        when {
            analyzer.expirationDateIsInThePast() -> showDialog()
            irregularities.isNotEmpty() && doc.type.hasNfcChip() -> showIrregularDataDialog(
                irregularities
            )

            else -> returnIdentityDocumentResult(context, doc)
        }
    }
}

fun processDocument(
    context: Context,
    identity: IdentityDocument?,
    showDialog: () -> Unit,
) = identity?.let { doc ->
    if (DocumentValidityAnalyzer(doc).expirationDateIsInThePast()) {
        showDialog()
    } else {
        returnIdentityDocumentResult(context, doc)
    }
}

fun returnIdentityDocumentResult(context: Context, doc: IdentityDocument) {
    (context as ReaderActivity).apply {
        setResult(Activity.RESULT_OK, Intent().putExtra(ReaderResult, doc))
        finish()
    }
}