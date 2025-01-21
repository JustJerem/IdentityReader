package com.jeremieguillot.identityreader.scan.data

import com.google.common.truth.Truth.assertThat
import com.google.mlkit.vision.text.Text
import com.jeremieguillot.identityreader.core.domain.DocumentType
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test

class MRZRecognitionOCRTest {

    private val ocr = MRZRecognitionOCR()

    @Test
    fun `should correctly process OCR blocks into MRZResult`() {
        // Given
        val textBlock1 = createTextBlock(listOf("P<FRADOE<<JOHN<<<<<<<<<<<<<<<<<<<"))
        val textBlock2 = createTextBlock(listOf("1234567894FRA8701012M3005014<<<<<<<<<<<<<<04"))
        val blocks = listOf(textBlock1, textBlock2)

        // When
        val result = ocr.recognize(blocks)

        // Then
        assertThat(result).isInstanceOf(MRZResult.Success::class.java)
        val success = result as MRZResult.Success
        assertThat(success.data.type).isEqualTo(DocumentType.PASSPORT)
        assertThat(success.data.documentNumber).isEqualTo("123456789")
        assertThat(success.data.firstName).isEqualTo("JOHN")
        assertThat(success.data.lastName).isEqualTo("DOE")
    }

    @Test
    fun `should return Failure for empty OCR blocks`() {
        // Given
        val blocks = emptyList<Text.TextBlock>()

        // When
        val result = ocr.recognize(blocks)

        // Then
        assertThat(result).isEqualTo(MRZResult.Failure)
    }

    @Test
    fun `should clean and process mixed whitespace in OCR blocks`() {
        // Given
        val textBlock1 = createTextBlock(listOf("   P<USA  DOE  <<JOHN   "))
        val textBlock2 = createTextBlock(listOf("   987654321<USA8001012M2703018   "))
        val blocks = listOf(textBlock1, textBlock2)

        // When
        val result = ocr.recognize(blocks)

        // Then
        assertThat(result).isInstanceOf(MRZResult.Success::class.java)
        val success = result as MRZResult.Success
        assertThat(success.data.type).isEqualTo(DocumentType.PASSPORT)
        assertThat(success.data.documentNumber).isEqualTo("987654321")
        assertThat(success.data.firstName).isEqualTo("JOHN")
        assertThat(success.data.lastName).isEqualTo("DOE")
    }

    @Test
    fun `should return Failure for unrecognized data`() {
        // Given
        val textBlock1 = createTextBlock(listOf("RANDOM TEXT THAT DOESN'T MATCH"))
        val blocks = listOf(textBlock1)

        // When
        val result = ocr.recognize(blocks)

        // Then
        assertThat(result).isEqualTo(MRZResult.Failure)
    }

    // Helper to create a mock TextBlock
    private fun createTextBlock(ocrLines: List<String>): Text.TextBlock {
        // Mock lines
        val textLines = ocrLines.map { line ->
            mockk<Text.Line> {
                every { text } returns line
                every { elements } returns emptyList()
                every { boundingBox } returns null
                every { cornerPoints } returns null
            }
        }

        // Mock TextBlock
        return mockk {
            every { text } returns lines.joinToString("\n")
            every { lines } returns textLines
            every { boundingBox } returns null
            every { cornerPoints } returns null
        }
    }
}