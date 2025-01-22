package com.jeremieguillot.identityreader.nfc.data

import android.nfc.Tag
import android.nfc.tech.IsoDep
import com.google.common.truth.Truth.assertThat
import com.jeremieguillot.identityreader.core.domain.DataDocument
import com.jeremieguillot.identityreader.core.domain.DocumentType
import com.jeremieguillot.identityreader.core.domain.IdentityDocument
import com.jeremieguillot.identityreader.core.domain.MRZ
import com.jeremieguillot.identityreader.core.domain.util.DataError
import com.jeremieguillot.identityreader.core.domain.util.Result
import com.jeremieguillot.identityreader.nfc.domain.NfcReaderStatus
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class NFCReaderTest {

    private lateinit var dataDocument: DataDocument
    private lateinit var nfcReader: NFCReader
    private lateinit var nfcDocument: NFCDocument
    private val mockTag: Tag = mockk()
    private val mockIsoDep: IsoDep = mockk()

    @Before
    fun setup() {
        // Updated DataDocument with new fields
        dataDocument = DataDocument(
            type = DocumentType.PASSPORT,
            issuingCountry = "FRA",
            documentNumber = "123456789",
            lastName = "DOE",
            firstName = "JOHN",
            nationality = "FRA",
            dateOfBirth = "990101",
            dateOfExpiry = "230101",
            deliveryDate = "01012023",
            sex = "M"
        )
        nfcReader = NFCReader(dataDocument)
        nfcDocument = mockk()

        mockkStatic(IsoDep::class)
        every { IsoDep.get(any()) } returns mockk()
    }

    @Test
    fun onTagDiscovered_emitsConnectingAndThenConnected_onSuccess() = runBlocking {
        // Given
        val mockIdentityDocument = mockk<IdentityDocument>()
        coEvery {
            nfcDocument.startReadTask(
                mockIsoDep,
                any<MRZ>(),
                DocumentType.PASSPORT
            )
        } returns Result.Success(mockIdentityDocument)

        mockkConstructor(NFCDocument::class)
        every {
            anyConstructed<NFCDocument>().startReadTask(
                mockIsoDep,
                any(),
                any()
            )
        } returns Result.Success(mockIdentityDocument)

        // When
        val result = nfcReader.onTagDiscovered(mockTag, DocumentType.PASSPORT)

        // Then
        assertThat(nfcReader.status.first()).isEqualTo(NfcReaderStatus.CONNECTED)
        assertThat(result).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun onTagDiscovered_emitsConnectingAndThenError_onFailure() = runBlocking {
        // Given
        coEvery {
            nfcDocument.startReadTask(
                mockIsoDep,
                any<MRZ>(),
                DocumentType.PASSPORT
            )
        } returns Result.Error(DataError.Local.INVALID_DATA)

        mockkConstructor(NFCDocument::class)
        every {
            anyConstructed<NFCDocument>().startReadTask(
                mockIsoDep,
                any(),
                any()
            )
        } returns Result.Error(DataError.Local.INVALID_DATA)

        // When
        val result = nfcReader.onTagDiscovered(mockTag, DocumentType.PASSPORT)

        // Then
        assertThat(nfcReader.status.first()).isEqualTo(NfcReaderStatus.ERROR)
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun onTagDiscovered_handlesNullTagGracefully() = runBlocking {
        // When
        val result = nfcReader.onTagDiscovered(null, DocumentType.PASSPORT)

        // Then
        assertThat(nfcReader.status.first()).isEqualTo(NfcReaderStatus.ERROR) // Expect ERROR status
        assertThat(result).isInstanceOf(Result.Error::class.java) // Expect Result.Error
    }
}