package com.jeremieguillot.identityreader.nfc.presentation.reader.components.documentcard

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.jeremieguillot.identityreader.core.domain.DocumentType
import com.jeremieguillot.identityreader.core.domain.IdentityDocument
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.documentcard.identitycard.BackIdentityCard
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.documentcard.identitycard.FrontIdentityCard
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.documentcard.passport.BackPassportCard
import com.jeremieguillot.identityreader.nfc.presentation.reader.components.documentcard.passport.PassportCard

enum class CardFace(val angle: Float) {
    Front(0f) {
        override val next: CardFace
            get() = Back
    },
    Back(180f) {
        override val next: CardFace
            get() = Front
    };

    abstract val next: CardFace
}

@Composable
fun FlipCard(
    cardFace: CardFace,
    onClick: (CardFace) -> Unit,
    modifier: Modifier = Modifier,
    back: @Composable () -> Unit = {},
    front: @Composable () -> Unit = {},
) {

    val rotation by animateFloatAsState(
        targetValue = cardFace.angle,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing,
        ), label = "rotation"
    )
    Card(
        onClick = { onClick(cardFace) },
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
    ) {
        if (rotation <= 90f) {
            front()
        } else {
            Box(
                Modifier
                    .graphicsLayer {
                        rotationY = 180f
                    },
            ) {
                back()
            }
        }
    }
}

@Composable
fun FlippableCard(
    modifier: Modifier = Modifier,
    cardModifier: Modifier = Modifier,
    identityDocument: IdentityDocument
) {
    var cardFace by remember {
        mutableStateOf(CardFace.Front)
    }

    FlipCard(
        modifier = modifier,
        cardFace = cardFace,
        onClick = {
//            cardFace = cardFace.next
        },
        front = {
            when (identityDocument.type) {
                DocumentType.PASSPORT -> PassportCard(cardModifier, identityDocument)
                DocumentType.ID_CARD -> FrontIdentityCard(cardModifier, identityDocument)
                else -> {}
            }
        },
        back = {
            when (identityDocument.type) {
                DocumentType.PASSPORT -> BackPassportCard(cardModifier)
                DocumentType.ID_CARD -> BackIdentityCard(cardModifier, identityDocument)
                else -> {}
            }
        },
    )
}