@file:OptIn(ExperimentalStdlibApi::class)

package com.jeremieguillot.identityreader.nfc.data

import android.content.ContentValues
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.nfc.tech.NfcA
import android.util.Log
import com.jeremieguillot.identityreader.core.domain.DataDocument
import com.jeremieguillot.identityreader.core.domain.IdentityDocument
import com.jeremieguillot.identityreader.core.domain.MRZ
import com.jeremieguillot.identityreader.core.domain.util.Error
import com.jeremieguillot.identityreader.core.domain.util.Result
import com.jeremieguillot.identityreader.nfc.domain.NfcReaderStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.experimental.and


class NFCReader(dataDocument: DataDocument) {

    private var mrz: MRZ = MRZ(
        dataDocument.documentNumber,
        dataDocument.dateOfBirth,
        dataDocument.dateOfExpiry
    )
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _status: MutableStateFlow<NfcReaderStatus> = MutableStateFlow(NfcReaderStatus.IDLE)
    val status: MutableStateFlow<NfcReaderStatus> = _status

    fun update(newMRZ: MRZ) {
        mrz = newMRZ
        reset()
    }

    fun reset() {
        scope.launch {
            _status.emit(NfcReaderStatus.IDLE)
        }
    }

    fun onTagDiscovered(tag: Tag?): Result<IdentityDocument, Error> {
        scope.launch {
            _status.emit(NfcReaderStatus.CONNECTING)
//            readTag(tag)
//            readTag2(tag)
        }
        val result = NFCDocument().startReadTask(IsoDep.get(tag), mrz)
        scope.launch {
            when (result) {
                is Result.Error -> _status.emit(NfcReaderStatus.ERROR)
                is Result.Success -> _status.emit(NfcReaderStatus.CONNECTED)
            }
        }
        return result

//        return Result.Error(DataError.Local.INVALID_DATA)
    }

    private fun readTag2(tag: Tag?) {
        tag?.let {
            // Handle IsoDep
            handleIsoDep(it)

            // Handle NfcA
            handleNfcA(it)

        }
    }
}

fun ByteArray.toHexString(): String =
    joinToString(separator = " ") { eachByte -> "%02x".format(eachByte) }


private fun handleIsoDep(tag: Tag) {
    val isoDepTag = IsoDep.get(tag)
    try {
        isoDepTag.connect()
        val apduCommand: ByteArray = byteArrayOf(
            0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte(),
            0x07.toByte(), 0xD2.toByte(), 0x76.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x85.toByte(), 0x01.toByte(), 0x01.toByte(),
            0x00.toByte()
        )
        val response: ByteArray = isoDepTag.transceive(apduCommand)
        Log.d(TAG, "NfcISO Data: ${response.toHexString()}")
    } catch (e: IOException) {
        Log.d(TAG, "EXCEPTION: ${e.message}")
    } finally {
        try {
            isoDepTag.close()
        } catch (e: IOException) {
            Log.d(TAG, "EXCEPTION: ${e.message}")
        }
    }
}

private fun handleNfcA(tag: Tag) {
    val nfcATag = NfcA.get(tag)
    try {
        nfcATag.connect()
        val atqa = nfcATag.atqa
        val sak = nfcATag.sak
        val data = nfcATag.transceive(byteArrayOf(0x30.toByte(), 0x04.toByte())) // Example command
        Log.d(TAG, "NfcA Data: ${data.toHexString()}")
        Log.d(TAG, "NfcA atqa: ${atqa}")
        Log.d(TAG, "NfcA sak: ${sak}")
    } catch (e: IOException) {
        Log.d(TAG, "EXCEPTION: ${e.message}")
    } finally {
        try {
            nfcATag.close()
        } catch (e: IOException) {
            Log.d(TAG, "EXCEPTION: ${e.message}")
        }
    }
}

val TAG = "NFC READER"
private val prefix = "android.nfc.tech."
private fun readTag(tag: Tag?) {

    Log.d(TAG, "readTag(${tag} ${tag?.techList})")

    val stringBuilder: StringBuilder = StringBuilder()
    val id: ByteArray? = tag?.id
    stringBuilder.append("Tag ID (hex): ${getHex(id!!)} \n")
    stringBuilder.append("Tag ID (dec): ${getDec(id)} \n")
    stringBuilder.append("Tag ID (reversed): ${getReversed(id)} \n")
    stringBuilder.append("Technologies: ")
    tag.techList.forEach { tech ->
        stringBuilder.append(tech.substring(prefix.length))
        stringBuilder.append(", ")
    }
    stringBuilder.delete(stringBuilder.length - 2, stringBuilder.length)
    tag.techList.forEach { tech ->
        if (tech.equals(MifareClassic::class.java.getName())) {
            stringBuilder.append('\n')
            val mifareTag: MifareClassic = MifareClassic.get(tag)
            val type = when (mifareTag.type) {
                MifareClassic.TYPE_CLASSIC -> "Classic"
                MifareClassic.TYPE_PLUS -> "Plus"
                MifareClassic.TYPE_PRO -> "Pro"
                else -> "Unknown"
            }
            stringBuilder.append("Mifare Classic type: $type \n")
            stringBuilder.append("Mifare size: ${mifareTag.size} bytes \n")
            stringBuilder.append("Mifare sectors: ${mifareTag.sectorCount} \n")
            stringBuilder.append("Mifare blocks: ${mifareTag.blockCount}")
        }
        if (tech.equals(MifareUltralight::class.java.getName())) {
            stringBuilder.append('\n')
            val mifareUlTag: MifareUltralight = MifareUltralight.get(tag)
            val type: String = when (mifareUlTag.type) {
                MifareUltralight.TYPE_ULTRALIGHT -> "Ultralight"
                MifareUltralight.TYPE_ULTRALIGHT_C -> "Ultralight C"
                else -> "Unkown"
            }
            stringBuilder.append("Mifare Ultralight type: ")
            stringBuilder.append(type)
        }
    }
    Log.d(TAG, "Datum: $stringBuilder")
    Log.d(ContentValues.TAG, "dumpTagData Return \n $stringBuilder")
}


//region Tags Information Methods

private fun getHex(bytes: ByteArray): String {
    val sb = StringBuilder()
    for (i in bytes.indices.reversed()) {
        val b: Int = bytes[i].and(0xff.toByte()).toInt()
        if (b < 0x10) sb.append('0')
        sb.append(Integer.toHexString(b))
        if (i > 0)
            sb.append(" ")
    }
    return sb.toString()
}

private fun getDec(bytes: ByteArray): Long {
    Log.d(TAG, "getDec()")
    var result: Long = 0
    var factor: Long = 1
    for (i in bytes.indices) {
        val value: Long = bytes[i].and(0xffL.toByte()).toLong()
        result += value * factor
        factor *= 256L
    }
    return result
}

private fun getReversed(bytes: ByteArray): Long {
    Log.d(TAG, "getReversed()")
    var result: Long = 0
    var factor: Long = 1
    for (i in bytes.indices.reversed()) {
        val value = bytes[i].and(0xffL.toByte()).toLong()
        result += value * factor
        factor *= 256L
    }
    return result
    }
