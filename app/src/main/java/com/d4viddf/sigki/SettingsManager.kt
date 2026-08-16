package com.d4viddf.sigki

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("sigki_prefs", Context.MODE_PRIVATE)

    var selectedIndex: Int
        get() = prefs.getInt("selected_index", 0)
        set(value) = prefs.edit { putInt("selected_index", value) }

    var flashlightOn: Boolean
        get() = prefs.getBoolean("flashlight_on", false)
        set(value) = prefs.edit { putBoolean("flashlight_on", value) }

    var cameraOpenOnLock: Boolean
        get() = prefs.getBoolean("camera_open_on_lock", true)
        set(value) = prefs.edit { putBoolean("camera_open_on_lock", value) }

    var selectedAppPackage: String?
        get() = prefs.getString("selected_app_package", null)
        set(value) = prefs.edit { putString("selected_app_package", value) }

    var selectedAppName: String?
        get() = prefs.getString("selected_app_name", null)
        set(value) = prefs.edit { putString("selected_app_name", value) }

    var selectedContactUri: String?
        get() = prefs.getString("selected_contact_uri", null)
        set(value) = prefs.edit { putString("selected_contact_uri", value) }

    var selectedContactName: String?
        get() = prefs.getString("selected_contact_name", null)
        set(value) = prefs.edit { putString("selected_contact_name", value) }

    var selectedContactLookupUri: String?
        get() = prefs.getString("selected_contact_lookup_uri", null)
        set(value) = prefs.edit { putString("selected_contact_lookup_uri", value) }

    var contactAction: String
        get() = prefs.getString("contact_action", "CALL") ?: "CALL"
        set(value) = prefs.edit { putString("contact_action", value) }

    var screenshotFolder: String
        get() = prefs.getString("screenshot_folder", "/Pictures/Screenshots") ?: "/Pictures/Screenshots"
        set(value) = prefs.edit { putString("screenshot_folder", value) }

    var showScreenshotPreview: Boolean
        get() = prefs.getBoolean("show_screenshot_preview", true)
        set(value) = prefs.edit { putBoolean("show_screenshot_preview", value) }

    var targetUrl: String
        get() = prefs.getString("target_url", "https://google.com") ?: "https://google.com"
        set(value) = prefs.edit { putString("target_url", value) }

    var mediaAction: String
        get() = prefs.getString("media_action", "PLAY_PAUSE") ?: "PLAY_PAUSE"
        set(value) = prefs.edit { putString("media_action", value) }

    var mediaAppPackage: String?
        get() = prefs.getString("media_app_package", null)
        set(value) = prefs.edit { putString("media_app_package", value) }

    var mediaAppName: String?
        get() = prefs.getString("media_app_name", null)
        set(value) = prefs.edit { putString("media_app_name", value) }

    var assistantEnabled: Boolean
        get() = prefs.getBoolean("assistant_enabled", true)
        set(value) = prefs.edit { putBoolean("assistant_enabled", value) }

    var permanentOptionsPanel: Boolean
        get() = prefs.getBoolean("permanent_options_panel", false)
        set(value) = prefs.edit { putBoolean("permanent_options_panel", value) }
}
