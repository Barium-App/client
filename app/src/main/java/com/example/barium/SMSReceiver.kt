package com.example.barium

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log

class SMSReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bundle: Bundle? = intent.extras
        try {
            if (bundle != null) {
                val pds = bundle["pds"] as Array<*>
                for (pdu in pds) {
                    val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
                    val serviceIntent = Intent(context, SMSService::class.java)
                    serviceIntent.putExtra("messageBody", smsMessage.messageBody)
                    serviceIntent.putExtra("originatingAddress", smsMessage.originatingAddress)
                    context.startService(serviceIntent)
                }
            }
        } catch (e: Exception) {
            Log.e("SMSReceiver", "Exception: $e")
        }
    }
}
