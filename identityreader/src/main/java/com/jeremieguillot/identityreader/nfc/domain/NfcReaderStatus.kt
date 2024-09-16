package com.jeremieguillot.identityreader.nfc.domain

import androidx.compose.ui.graphics.Color


enum class NfcReaderStatus(val color: Color) {
    IDLE(Color.DarkGray),
    CONNECTING(Color(0xFF1976D2)),
    CONNECTED(Color(0xFF388E3C)),
    DISABLED(Color(0xFFD32F2F)),
    ERROR(Color(0xFFD32F2F))
}


