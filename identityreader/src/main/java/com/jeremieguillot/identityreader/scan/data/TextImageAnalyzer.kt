package com.jeremieguillot.identityreader.scan.data

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class TextImageAnalyzer(val onSuccess: (List<Text.TextBlock>) -> Unit) : ImageAnalysis.Analyzer {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val resultText = visionText.text
                    Log.i("resultText", resultText)
                    imageProxy.close()
                    onSuccess(visionText.textBlocks)
                }
                .addOnFailureListener { e ->
                    Log.e("error", e.message ?: "null")
                    imageProxy.close()
                }
        }
    }
}