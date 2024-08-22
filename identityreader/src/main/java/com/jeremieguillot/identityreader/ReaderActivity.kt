package com.jeremieguillot.identityreader

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jeremieguillot.identityreader.core.domain.DataDocument
import com.jeremieguillot.identityreader.core.presentation.CustomNavType
import com.jeremieguillot.identityreader.core.presentation.Destination
import com.jeremieguillot.identityreader.core.ui.theme.IdentityReaderTheme
import com.jeremieguillot.identityreader.nfc.presentation.reader.NfcReaderScreen
import com.jeremieguillot.identityreader.scan.presentation.ScanScreen
import kotlin.reflect.typeOf

const val ReaderResult = "identity_document"

class ReaderActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this, CAMERAX_PERMISSIONS, 0
            )
        }

        enableEdgeToEdge()
        setContent {
            IdentityReaderTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Destination.ScannerScreen
                ) {
                    composable<Destination.ScannerScreen> {
                        ScanScreen(navController)
                    }
                    composable<Destination.ReaderScreen>(
                        typeMap = mapOf(
                            typeOf<DataDocument>() to CustomNavType(
                                DataDocument::class.java,
                                DataDocument.serializer()
                            )
                        )
                    ) {
                        val args = it.toRoute<Destination.ReaderScreen>()
                        NfcReaderScreen(args.dataDocument)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        enableNfcForegroundDispatch()
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    private fun enableNfcForegroundDispatch() {
        nfcAdapter?.let { adapter ->
            if (adapter.isEnabled) {
                val nfcIntentFilter = arrayOf(
                    IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
                    IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                    IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
                )

                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getActivity(
                        this,
                        0,
                        Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        PendingIntent.FLAG_MUTABLE
                    )
                } else {
                    PendingIntent.getActivity(
                        this,
                        0,
                        Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        PendingIntent.FLAG_MUTABLE
                    )
                }
                val techLists = arrayOf(arrayOf("android.nfc.tech.IsoDep"))
                adapter.enableForegroundDispatch(
                    this, pendingIntent, nfcIntentFilter, techLists
                )
            } else {
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
            }
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext, it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val CAMERAX_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
        )
    }
}