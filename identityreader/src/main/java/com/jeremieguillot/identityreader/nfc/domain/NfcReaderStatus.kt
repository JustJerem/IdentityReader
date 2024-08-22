package com.jeremieguillot.identityreader.nfc.domain

import androidx.compose.ui.graphics.Color

enum class NfcReaderStatus(val color: Color) {
    IDLE(Color.Gray),
    CONNECTING(Color(0xFF64B5F6)), // Blue 200
    CONNECTED(Color(0xFF4CAF50)), // Green 400
    DISABLED(Color(0xFFEF5350)), // Red 300
    ERROR(Color(0xFFEF5350)) // Red 300
}

