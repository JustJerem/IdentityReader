package com.jeremieguillot.identityreader.scan.presentation

import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jeremieguillot.identityreader.R

@Composable
fun OverlayScreen(
    modifier: Modifier = Modifier,
    controller: LifecycleCameraController,
    overlayColor: Color = Color.Black.copy(alpha = 0.5f),
    transparentRectWidthFraction: Float = 0.3f,
    transparentRectHeightFraction: Float = 0.9f,
    cornerRadius: Float = 32f,
    dashLength: Float = 40f,
    gapLength: Float = 10f,
    content: @Composable () -> Unit,
) {
    var isFlashlightOn by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        content()

        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val rectWidth = canvasWidth * transparentRectWidthFraction
                val rectHeight = canvasHeight * transparentRectHeightFraction
                val rectLeft = 100f
                val rectTop = (canvasHeight - rectHeight) / 2

                // Draw the semi-transparent overlay
                drawRect(
                    color = overlayColor,
                    size = size
                )

                // Draw the transparent rectangle
                clipRect(
                    left = rectLeft,
                    top = rectTop,
                    right = rectLeft + rectWidth,
                    bottom = rectTop + rectHeight
                ) {
                    drawRoundRect(
                        color = Color.Transparent,
                        topLeft = Offset(rectLeft, rectTop),
                        size = Size(rectWidth, rectHeight),
                        cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                        blendMode = BlendMode.Clear
                    )
                }

                // Draw the dashed border
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(rectLeft, rectTop),
                    size = Size(rectWidth, rectHeight),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, gapLength))
                    )
                )
            }
            FlashlightToggle(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(32.dp),
                isFlashlightOn = isFlashlightOn,
            ) {
                isFlashlightOn = !isFlashlightOn
                controller.enableTorch(isFlashlightOn)
            }
            Text(
                text = stringResource(R.string.nfc_position_card),
                textAlign = TextAlign.Center,
                color = overlayColor,
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer {
                        translationX = -280f
                        rotationZ = 90f // Rotate the text vertically
                    }
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun FlashlightToggle(
    modifier: Modifier,
    isFlashlightOn: Boolean,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = {
            onClick()
        },
        modifier = modifier
            .rotate(90f)
            .background(
                Color.White.copy(alpha = 0.5f),
                CircleShape
            )
    ) {
        Icon(
            imageVector = if (isFlashlightOn) Icons.Filled.FlashlightOn else Icons.Filled.FlashlightOff,
            contentDescription = stringResource(R.string.flashlight),
            tint = if (isFlashlightOn) Color.White else Color.Black,
            modifier = Modifier.size(32.dp)
        )
    }

}