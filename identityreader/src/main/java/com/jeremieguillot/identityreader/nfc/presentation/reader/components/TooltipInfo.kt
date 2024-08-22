package com.jeremieguillot.identityreader.nfc.presentation.reader.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeremieguillot.identityreader.R

@Composable
fun TooltipInfo(titleId: Int, descriptionId: Int) {
    Card(
        modifier = Modifier
            .padding(bottom = 2.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chat_info),
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = stringResource(id = titleId),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        fontSize = 16.sp
                    )
                }
            }
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(id = descriptionId),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Black
                )
            )
        }
    }
}

