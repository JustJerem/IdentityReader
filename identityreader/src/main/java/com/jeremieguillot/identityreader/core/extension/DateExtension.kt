package com.jeremieguillot.identityreader.core.extension

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun Date.toLocaleDateString(): String {
    val formatter = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
    return formatter.format(this)
}

fun Date.toLocaleDateStringSeparated(): String {
    try {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(this)
    } catch (e: Exception) {
        return ""
    }
}

fun Date.toMRZFormat(): String {
    val outputFormat = SimpleDateFormat("yyMMdd", Locale.getDefault())
    return outputFormat.format(this)
}

fun String.fromYYMMDDtoDate(): Date? {
    if (isBlank()) return null
    val sdf = SimpleDateFormat("yyMMdd", Locale.getDefault())
    return sdf.parse(this) ?: Date()
}

fun String.fromDDMMYYYYtoDate(): Date {
    if (isBlank()) return Date()
    val sdf = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
    return sdf.parse(this) ?: Date()
}

fun String.toSlashStringDate(pattern: String = "yyMMdd", forceDateInPast: Boolean = false): String {
    try {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        var parsedDate = LocalDate.parse(this, formatter)

        if (forceDateInPast) {
            parsedDate = parsedDate.minusYears(100)
        }


        val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return parsedDate.format(outputFormatter)
    } catch (e: Exception) {
        return ""
    }
}