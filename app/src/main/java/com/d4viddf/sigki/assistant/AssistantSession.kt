package com.d4viddf.sigki.assistant

import android.content.Context
import android.os.Bundle
import android.graphics.Bitmap
import android.service.voice.VoiceInteractionSession
import com.d4viddf.sigki.ActionExecutor
import com.d4viddf.sigki.SettingsManager

class AssistantSession(context: Context) : VoiceInteractionSession(context) {

    override fun onShow(args: Bundle?, showFlags: Int) {
        super.onShow(args, showFlags)
        
        // Explicitly request screenshot data
        // SHOW_WITH_SCREENSHOT = 1
        // But better to use the system flow.
    }

    override fun onHandleScreenshot(screenshot: Bitmap?) {
        super.onHandleScreenshot(screenshot)
        
        val settings = SettingsManager(context)
        if (settings.assistantEnabled) {
            val executor = ActionExecutor(context, settings)
            executor.executeSelectedAction(screenshot)
        }
        
        finish()
    }
}
