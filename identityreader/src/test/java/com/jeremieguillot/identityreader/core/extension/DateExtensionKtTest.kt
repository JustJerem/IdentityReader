package com.jeremieguillot.identityreader.core.extension

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateExtensionTest {

    @Test
    fun `should format Date to ddMMyyyy`() {
        // Given
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("2023-01-21")!!

        // When
        val formattedDate = date.toLocaleDateString()

        // Then
        assertThat(formattedDate).isEqualTo("21012023")
    }

    @Test
    fun `should format Date to dd slash MM slash yyyy`() {
        // Given
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("2023-01-21")!!

        // When
        val formattedDate = date.toLocaleDateStringSeparated()

        // Then
        assertThat(formattedDate).isEqualTo("21/01/2023")
    }

    @Test
    fun `should format Date to yyMMdd`() {
        // Given
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("2023-01-21")!!

        // When
        val formattedDate = date.toMRZFormat()

        // Then
        assertThat(formattedDate).isEqualTo("230121")
    }

    @Test
    fun `should parse String from yyMMdd to Date`() {
        // Given
        val input = "230121"

        // When
        val date = input.fromYYMMDDtoDate()

        // Then
        val expectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("2023-01-21")
        assertThat(date).isEqualTo(expectedDate)
    }

    @Test
    fun `should return null for blank String fromYYMMDDtoDate`() {
        // Given
        val input = ""

        // When
        val date = input.fromYYMMDDtoDate()

        // Then
        assertThat(date).isNull()
    }

    @Test
    fun `should parse String from ddMMyyyy to Date`() {
        // Given
        val input = "21012023"

        // When
        val date = input.fromDDMMYYYYtoDate()

        // Then
        val expectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("2023-01-21")
        assertThat(date).isEqualTo(expectedDate)
    }

    @Test
    fun `should return current date for blank String fromDDMMYYYYtoDate`() {
        // Given
        val input = ""

        // When
        val date = input.fromDDMMYYYYtoDate()

        // Then
        val expectedDate = Date() // Current date
        assertThat(date).isNotNull()
    }

    @Test
    fun `should convert String to dd slash MM slash yyyy format`() {
        // Given
        val input = "210123"

        // When
        val formattedDate = input.toSlashStringDate()

        // Then
        assertThat(formattedDate).isEqualTo("23/01/2021")
    }

    @Test
    fun `should convert String to past date if forceDateInPast is true`() {
        // Given
        val input = "990101"

        // When
        val formattedDate = input.toSlashStringDate(forceDateInPast = true)

        // Then
        assertThat(formattedDate).isEqualTo("01/01/1999")
    }

    @Test
    fun `should handle invalid input in toSlashStringDate gracefully`() {
        // Given
        val input = "invalid_date"

        // When
        val formattedDate = input.toSlashStringDate()

        // Then
        assertThat(formattedDate).isEmpty()
    }
}