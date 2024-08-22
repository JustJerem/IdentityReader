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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.jeremieguillot.identityreader.ReaderActivity
import com.jeremieguillot.identityreader.ReaderResult
import com.jeremieguillot.identityreader.core.domain.DocumentType
import com.jeremieguillot.identityreader.core.domain.IdentityDocument
import com.jeremieguillot.identityreader.core.domain.IdentityDocument.Companion.toIdentityDocument
import com.jeremieguillot.identityreader.core.presentation.Destination
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.ExpirationDialog
import com.jeremieguillot.identityreader.scan.data.MRZRecognitionOCR
import com.jeremieguillot.identityreader.scan.data.MRZResult.Failure
import com.jeremieguillot.identityreader.scan.data.MRZResult.Success
import com.jeremieguillot.identityreader.scan.data.TextImageAnalyzer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun ScanScreen(navController: NavHostController) {

    val recognizer = remember { MRZRecognitionOCR() }
    val context = LocalContext.current
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

    val analyzer = remember {
        TextImageAnalyzer(onSuccess = {
            when (val result = recognizer.recognize(it)) {
                Failure -> {/*will retry automatically*/
                }

                is Success -> {
                    val type = result.data.type
                    when (type) {
                        DocumentType.PASSPORT -> navController.navigate( ///todo add DocumentType.ID_CARD when France Connect reply, NFC new card working
                            Destination.ReaderScreen(result.data)
                        )

                        else -> {
                            identity = toIdentityDocument(result.data)
                            processDocument(
                                context = context,
                                identity = identity,
                                showDialog = { showExpirationDialog = true }
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
                .padding(padding)
        ) {
            CameraPreview(
                controller = controller, modifier = Modifier.fillMaxSize()
            )
        }
    }
}

fun processDocument(
    context: Context,
    identity: IdentityDocument?,
    showDialog: () -> Unit
) {
    identity?.let { document ->
        if (expirationDateIsInThePast(document.expirationDate)) {
            showDialog()
        } else {
            returnIdentityDocumentResult(context, document)
        }
    }
}


fun expirationDateIsInThePast(expirationDate: String): Boolean {
    if (expirationDate.isBlank()) return true
    return try {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val expiration = LocalDate.parse(expirationDate, formatter)
        expiration.isBefore(LocalDate.now())
    } catch (e: DateTimeParseException) {
        throw IllegalArgumentException("Invalid date format. Please use 'dd/MM/yyyy'.")
    }
}


fun returnIdentityDocumentResult(context: Context, doc: IdentityDocument) {
    val resultIntent = Intent().apply {
        putExtra(ReaderResult, doc)
    }
    (context as ReaderActivity).setResult(Activity.RESULT_OK, resultIntent)
    context.finish()
}
