package com.d4viddf.sigki.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.d4viddf.sigki.ActionExecutor
import com.d4viddf.sigki.SettingsManager

class AssistantActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // This activity handles the ACTION_ASSIST intent.
        // We can either show our own UI here or trigger the service.
        // For now, let's just trigger the executor and finish.
        
        val settings = SettingsManager(this)
        if (settings.assistantEnabled) {
            val executor = ActionExecutor(this, settings)
            executor.executeSelectedAction()
        }
        
        finish()
    }
}
