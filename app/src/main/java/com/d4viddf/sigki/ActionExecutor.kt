package com.d4viddf.sigki

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.SystemClock
import android.provider.Settings
import android.view.KeyEvent
import android.widget.Toast
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActionExecutor(private val context: Context, private val settings: SettingsManager) {

    fun executeSelectedAction(screenshot: Bitmap? = null) {
        val items = listOf(
            "Flashlight", "Camera", "Do not disturb", "Media", "App",
            "Contact", "Screenshot", "Open URL", "TalkBack"
        )
        val selectedAction = items.getOrNull(settings.selectedIndex) ?: return

        when (selectedAction) {
            "Flashlight" -> toggleFlashlight()
            "Camera" -> openCamera()
            "Do not disturb" -> toggleDND()
            "Media" -> handleMedia()
            "App" -> launchApp()
            "Contact" -> handleContact()
            "Screenshot" -> takeScreenshot(screenshot)
            "Open URL" -> openUrl()
            "TalkBack" -> toggleTalkBack()
        }
    }

    private fun handleMedia() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val action = settings.mediaAction
        
        // If nothing is playing and we have a fallback app, launch it first
        if (!audioManager.isMusicActive && action == "PLAY_PAUSE") {
            settings.mediaAppPackage?.let { packageName ->
                val intent = context.packageManager.getLaunchIntentForPackage(packageName)
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    // Give it a tiny bit of time to initialize if needed, 
                    // though dispatching keys immediately usually works for most players
                }
            }
        }

        val keyCode = when(action) {
            "STOP" -> KeyEvent.KEYCODE_MEDIA_STOP
            else -> KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
        }

        val eventTime = SystemClock.uptimeMillis()
        audioManager.dispatchMediaKeyEvent(KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0))
        audioManager.dispatchMediaKeyEvent(KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keyCode, 0))
    }

    private fun toggleFlashlight() {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = cameraManager.cameraIdList[0]
            val newState = !settings.flashlightOn
            cameraManager.setTorchMode(cameraId, newState)
            settings.flashlightOn = newState
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.toast_flashlight_error, e.message), Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCamera() {
        val intent = Intent(android.provider.MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun toggleDND() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.isNotificationPolicyAccessGranted) {
            val filter = if (notificationManager.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_ALL) {
                NotificationManager.INTERRUPTION_FILTER_NONE
            } else {
                NotificationManager.INTERRUPTION_FILTER_ALL
            }
            notificationManager.setInterruptionFilter(filter)
        } else {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    private fun launchApp() {
        val packageName = settings.selectedAppPackage
        if (packageName != null) {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
    }

    private fun handleContact() {
        val uriString = settings.selectedContactUri ?: return
        val action = settings.contactAction
        val intent = when (action) {
            "CALL" -> Intent(Intent.ACTION_CALL, uriString.toUri())
            "MESSAGE" -> Intent(Intent.ACTION_SENDTO,
                "smsto:${uriString.toUri().schemeSpecificPart}".toUri())
            else -> {
                // VIEW action: Try to use lookup URI for profile, fallback to tel: URI
                val lookupUri = settings.selectedContactLookupUri
                if (lookupUri != null) {
                    Intent(Intent.ACTION_VIEW, lookupUri.toUri())
                } else {
                    Intent(Intent.ACTION_VIEW, uriString.toUri())
                }
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.toast_contact_error, e.message), Toast.LENGTH_SHORT).show()
        }
    }

    private fun takeScreenshot(bitmap: Bitmap?) {
        if (bitmap == null) {
            Toast.makeText(context, context.getString(R.string.toast_no_screenshot), Toast.LENGTH_SHORT).show()
            return
        }

        if (settings.showScreenshotPreview) {
            // Signal MainActivity to show preview
            val intent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("action", "SHOW_SCREENSHOT")
            }
            MainActivity.latestScreenshot = bitmap
            context.startActivity(intent)
        } else {
            saveBitmapToSelectedFolder(bitmap)
        }
    }

    private fun saveBitmapToSelectedFolder(bitmap: Bitmap) {
        try {
            val folderUriString = settings.screenshotFolder
            val filename = "Screenshot_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.png"
            
            val contentResolver = context.contentResolver
            val folderUri = folderUriString.toUri()
            
            if (folderUri.scheme == "content") {
                val pickedDir = DocumentFile.fromTreeUri(context, folderUri)
                val file = pickedDir?.createFile("image/png", filename)
                if (file != null) {
                    val out: OutputStream? = contentResolver.openOutputStream(file.uri)
                    out?.use {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                    }
                    Toast.makeText(context, context.getString(R.string.toast_screenshot_saved), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, context.getString(R.string.toast_invalid_location), Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.toast_save_failed, e.message), Toast.LENGTH_SHORT).show()
        }
    }

    private fun openUrl() {
        val url = settings.targetUrl
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun toggleTalkBack() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        Toast.makeText(context, context.getString(R.string.toast_opening_accessibility), Toast.LENGTH_SHORT).show()
    }
}
