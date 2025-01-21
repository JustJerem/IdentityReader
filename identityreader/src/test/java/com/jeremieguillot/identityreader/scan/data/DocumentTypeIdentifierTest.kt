package com.jeremieguillot.identityreader.scan.data

import com.google.common.truth.Truth.assertThat
import com.jeremieguillot.identityreader.core.domain.DocumentType
import org.junit.Test

class DocumentTypeIdentifierTest {

    @Test
    fun `should identify new identity card`() {
        // Given
        val sampleData = """
            IDFRAX4RTBPFW46<<<<<<<<<<<<<<<
            9007138F3002119FRA<<<<<<<<<<<6
            MARTIN<<MAELYS<GAELLE<MARIE<<<
        """.trimIndent()

        val identifier = DocumentTypeIdentifier(sampleData)

        // When
        val result = identifier.identify()

        // Then
        assertThat(result).isInstanceOf(MRZResult.Success::class.java)
        val success = result as MRZResult.Success
        assertThat(success.data.type).isEqualTo(DocumentType.ID_CARD)
        assertThat(success.data.documentNumber).isEqualTo("X4RTBPFW4")
        assertThat(success.data.firstName).isEqualTo("MAELYS")
        assertThat(success.data.lastName).isEqualTo("MARTIN")
        assertThat(success.data.nationality).isEqualTo("FRA")
        assertThat(success.data.dateOfBirth).isEqualTo("900713")
        assertThat(success.data.dateOfExpiry).isEqualTo("300211")
        assertThat(success.data.sex).isEqualTo("F")
    }

    @Test
    fun `should identify passport`() {
        // Given
        val sampleData = """
            P<FRAULYSSE<<CHRISTOPHE<<<<<<<<<<<<<<<<<<<<<
            08CD503380FRA6004103M1806058<<<<<<<<<<<<<<06
        """.trimIndent()

        val identifier = DocumentTypeIdentifier(sampleData)

        // When
        val result = identifier.identify()

        // Then
        assertThat(result).isInstanceOf(MRZResult.Success::class.java)
        val success = result as MRZResult.Success
        assertThat(success.data.type).isEqualTo(DocumentType.PASSPORT)
        assertThat(success.data.documentNumber).isEqualTo("08CD50338")
        assertThat(success.data.firstName).isEqualTo("CHRISTOPHE")
        assertThat(success.data.lastName).isEqualTo("ULYSSE")
        assertThat(success.data.nationality).isEqualTo("FRA")
        assertThat(success.data.dateOfBirth).isEqualTo("600410")
        assertThat(success.data.dateOfExpiry).isEqualTo("180605")
        assertThat(success.data.sex).isEqualTo("M")
    }

    @Test
    fun `should identify old identity card`() {
        // Given
        val sampleData = """
            IDFRABERTHIER<<<<<<<<<<<<<<<<<<<<<<<
            8806923102858CORINNE<<<<<<<6512068F6
        """.trimIndent()

        val identifier = DocumentTypeIdentifier(sampleData)

        // When
        val result = identifier.identify()

        // Then
        assertThat(result).isInstanceOf(MRZResult.Success::class.java)
        val success = result as MRZResult.Success
        assertThat(success.data.type).isEqualTo(DocumentType.OLD_ID_CARD)
        assertThat(success.data.documentNumber).isEqualTo("880692310285")
        assertThat(success.data.firstName).isEqualTo("CORINNE")
        assertThat(success.data.lastName).isEqualTo("BERTHIER")
        assertThat(success.data.nationality).isEqualTo("FRA")
        assertThat(success.data.dateOfBirth).isEqualTo("651206")
        assertThat(success.data.sex).isEqualTo("F")
    }

    @Test
    fun `should fail for unknown document`() {
        // Given
        val sampleData = """
            UNKNOWN DATA FORMAT
        """.trimIndent()

        val identifier = DocumentTypeIdentifier(sampleData)

        // When
        val result = identifier.identify()

        // Then
        assertThat(result).isEqualTo(MRZResult.Failure)
    }
}