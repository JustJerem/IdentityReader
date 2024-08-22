package com.jeremieguillot.identityreader.scan.domain

import java.util.regex.Matcher

enum class IdentifierType {
    OLD_PASSPORT,
    PASSPORT,
    ID_CARD,
    UNKNOWN
}

data class IdentifierContent(val type: IdentifierType, val matcher: Matcher?)