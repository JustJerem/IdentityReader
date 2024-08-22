package com.jeremieguillot.identityreader.nfc.presentation.reader.components.documentcard.identitycard

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun IdentityCardHolder(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    ElevatedCard(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .aspectRatio(1.585f),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
    ) {
        content()
    }
}