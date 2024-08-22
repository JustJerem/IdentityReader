package com.jeremieguillot.identityreader.nfc.presentation.reader.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import com.jeremieguillot.identityreader.nfc.domain.NfcReaderStatus

@Composable
fun ReaderAnimation(status: NfcReaderStatus, modifier: Modifier = Modifier) {
    // Define the transition state
    val transition = rememberInfiniteTransition(label = "transition")

    // Define the animation values
    val durationMs = 1500
    val radius by transition.animateFloat(
        initialValue = 20f,
        targetValue = 250f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMs),
            repeatMode = RepeatMode.Restart
        ), label = "radius"
    )

    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = durationMs
                0.0f at 1000 // Fade out halfway through the animation
            },
            repeatMode = RepeatMode.Restart
        ), label = "alpha"
    )

    Canvas(
        modifier = modifier//.size(200.dp)
    ) {
        drawCircle(
            color = status.color,
            radius = radius,
            alpha = alpha,
            style = Fill
        )
    }
}

@Preview
@Composable
fun LoadingAnimationPreview() {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var status by remember {
            mutableStateOf(NfcReaderStatus.IDLE)
        }
        ReaderAnimation(status = status)
        Button(onClick = { status = NfcReaderStatus.CONNECTED }) {
            Text(text = "Click Me")
        }
    }
}
