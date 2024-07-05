package com.example.barium

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

class SMSReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (message in messages) {
                val sender = message.originatingAddress
                val messageBody = message.messageBody
                Log.d("SMSReceiver", "SMS received from: $sender, Message: $messageBody")

            }
        }
    }

    private fun processReceivedMessage(context: Context, sender: String?, messageBody: String) {
        Log.d("SMSReceiver", "Received message from $sender: $messageBody")
        // Implement logic to process the received message
    }
}