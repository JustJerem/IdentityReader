package com.jeremieguillot.identityreader.nfc.presentation.reader.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jeremieguillot.identityreader.core.ui.theme.IdentityReaderTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RippleEffect(backgroundColor: Color = Color.Gray, modifier: Modifier = Modifier) {
    // Number of circles in the ripple effect
    val circleCount = 5

    // Initialize a list of Animatable scales for each circle
    val scales = remember { List(circleCount) { Animatable(1f) } }

    // Launch animations with staggered delays
    LaunchedEffect(Unit) {
        scales.forEachIndexed { index, animatable ->
            launch {
                // Delay each animation by 0.1 seconds multiplied by the index
                delay(100L * index)
                animatable.animateTo(
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
        }
    }

    // Layout to overlay circles on top of each other
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Draw circles from largest to smallest
        for (i in (circleCount - 1) downTo 0) {
            val scale = scales[i].value
            val size = 50.dp * (i + 1)
            val opacity = 1f - 0.2f * i

            Box(
                modifier = modifier
                    .size(size)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        alpha = opacity
                        shadowElevation = 10.dp.toPx()
                        shape = CircleShape
                        clip = true
                    }
                    .background(
                        color = backgroundColor,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Preview
@Composable
private fun RippleEffectPrev() {
    IdentityReaderTheme {
        RippleEffect(Color.Red)
    }
}
