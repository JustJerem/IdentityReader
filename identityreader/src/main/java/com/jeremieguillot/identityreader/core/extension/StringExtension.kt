package com.jeremieguillot.identityreader.core.extension

fun String.addSpaceEveryNChars(n: Int): String =
    chunked(n).joinToString(" ") { it }