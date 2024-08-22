package com.jeremieguillot.identityreader.scan.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp

@Composable
fun OverlayScreen(
    modifier: Modifier = Modifier,
    overlayColor: Color = Color.Black.copy(alpha = 0.5f),
    transparentRectWidthFraction: Float = 0.3f,
    transparentRectHeightFraction: Float = 0.9f,
    cornerRadius: Float = 32f,
    dashLength: Float = 40f,
    gapLength: Float = 10f,
    content: @Composable () -> Unit,
) {

    Box(modifier = modifier) {
        content()

        // Overlay
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
    }
}