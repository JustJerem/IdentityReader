package com.jeremieguillot.identityreader.scan.presentation

import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.jeremieguillot.identityreader.core.domain.DataDocument
import com.jeremieguillot.identityreader.core.domain.IdentityDocument
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.ErrorDialog
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.ExpirationDialog
import com.jeremieguillot.identityreader.scan.data.MRZRecognitionOCR
import com.jeremieguillot.identityreader.scan.data.TextImageAnalyzer
import com.jeremieguillot.identityreader.scan.domain.DocumentValidityAnalyzer
import com.jeremieguillot.identityreader.scan.presentation.ScanContract.ScanIntent
import com.jeremieguillot.identityreader.scan.presentation.camera.CameraPreview
import com.jeremieguillot.identityreader.scan.presentation.camera.OverlayScreen

@Composable
fun ScanScreen(
    navigateToNfcReader: (DataDocument) -> Unit,
    returnIdentityDocumentResult: (IdentityDocument) -> Unit,
    viewModel: ScanViewModel = remember { ScanViewModel() }
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Observe navigation events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ScanContract.ScanEvent.NavigateToNfcReader -> navigateToNfcReader(event.dataDocument)
                is ScanContract.ScanEvent.ReturnIdentityDocumentResult -> returnIdentityDocumentResult(
                    event.identityDocument
                )
            }
        }
    }

    // Handle dialogs
    if (state.showErrorDialog) {
        ErrorDialog(
            showDialog = true,
            onDismiss = { viewModel.processIntent(ScanIntent.ResetErrorCount) }
        )
    }

    if (state.showExpirationDialog) {
        ExpirationDialog(
            showDialog = true,
            onDismiss = { viewModel.processIntent(ScanIntent.DismissExpirationDialog) },
            onConfirm = { viewModel.processIntent(ScanIntent.ConfirmIdentity) }
        )
    }

    // Create and remember the analyzer
    val recognizer = remember { MRZRecognitionOCR() }
    val analyzer = remember {
        TextImageAnalyzer { text ->
            val result = recognizer.recognize(text)
            viewModel.processIntent(ScanIntent.ProcessMRZResult(result))
        }
    }

    // Set up the camera controller
    val controller = remember {
        LifecycleCameraController(context).apply {
            isPinchToZoomEnabled = false
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
            setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context),
                analyzer
            )
        }
    }

    Scaffold { padding ->
        OverlayScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            controller = controller
        ) {
            CameraPreview(
                controller = controller,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

fun processDocument(
    identity: IdentityDocument?,
    showDialog: () -> Unit,
    returnIdentityDocumentResult: (IdentityDocument) -> Unit,
) = identity?.let { doc ->
    if (DocumentValidityAnalyzer(doc).expirationDateIsInThePast()) {
        showDialog()
    } else {
        returnIdentityDocumentResult(doc)
    }
}