package com.example.zcode

import android.app.Application
import android.util.Log
import com.example.zcode.linux.UbuntuEnvironment
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * ZcodeApplication - Main application class for Zcode Terminal Emulator
 *
 * Initializes Hilt dependency injection and auto-sets up Ubuntu environment
 * on first launch (happens in background - no user interaction needed!)
 */
@HiltAndroidApp
class ZcodeApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "ZcodeApp"
        const val PREFS_NAME = "zcode_prefs"
        const val KEY_UBUNTU_SETUP_COMPLETE = "ubuntu_setup_complete"
    }

    override fun onCreate() {
        super.onCreate()

        // Auto-setup Ubuntu environment in background on first launch
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val setupComplete = prefs.getBoolean(KEY_UBUNTU_SETUP_COMPLETE, false)

        if (!setupComplete) {
            Log.i(TAG, "First launch - setting up Ubuntu environment in background...")

            applicationScope.launch {
                try {
                    val ubuntu = UbuntuEnvironment(this@ZcodeApplication)

                    ubuntu.setupEnvironment { progress ->
                        Log.i(TAG, "Ubuntu setup: ${progress.message} (${progress.progress}%)")
                    }.onSuccess {
                        prefs.edit().putBoolean(KEY_UBUNTU_SETUP_COMPLETE, true).apply()
                        Log.i(TAG, "✅ Ubuntu environment ready!")
                    }.onFailure { error ->
                        Log.e(TAG, "❌ Ubuntu setup failed", error)
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Exception during Ubuntu setup", e)
                }
            }
        } else {
            Log.i(TAG, "Ubuntu environment already configured")
        }
    }
}

