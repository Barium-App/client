package com.example.barium

import android.app.IntentService
import android.content.Intent
import android.os.Build
import android.telephony.SmsManager
import android.util.Log

class SMSService : IntentService("SMSService") {
    override fun onHandleIntent(intent: Intent?) {
        val message = intent?.getStringExtra("message")
        val recipient = intent?.getStringExtra("recipient")

        if (!message.isNullOrEmpty() && !recipient.isNullOrEmpty()) {
            sendSMS(recipient, message)
        }
    }

    private fun sendSMS(recipient: String, message: String) {
        try {
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getSystemService(SmsManager::class.java)
            } else {
                SmsManager.getDefault()
            }
            smsManager.sendTextMessage(recipient, null, message, null, null)
            Log.d("SMSService", "Message sent to $recipient: $message")
        } catch (e: Exception) {
            Log.e("SMSService", "Failed to send SMS", e)
        }
    }
}