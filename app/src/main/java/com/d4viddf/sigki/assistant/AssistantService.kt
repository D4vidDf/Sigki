package com.d4viddf.sigki.assistant

import android.service.voice.VoiceInteractionService

class AssistantService : VoiceInteractionService() {
    override fun onReady() {
        super.onReady()
        // Signal that the service is ready to be bound
    }
}
