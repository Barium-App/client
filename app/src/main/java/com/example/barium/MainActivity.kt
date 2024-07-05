package com.example.barium

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var signalStrengthListener: SignalStrengthListener
    private lateinit var locationHelper: LocationHelper
    private var isMonitoring = false

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
        private const val SERVER_PHONE_NUMBER = "+989361720429" // Replace with actual server number
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)

        signalStrengthListener = SignalStrengthListener(this)
        locationHelper = LocationHelper(this)

        setupButtonListeners()
        checkAndRequestPermissions()
    }

    private fun setupButtonListeners() {
        startButton.setOnClickListener {
            if (!isMonitoring) {
                startMonitoring()
            }
        }

        stopButton.setOnClickListener {
            if (isMonitoring) {
                stopMonitoring()
            }
        }
    }

    private fun startMonitoring() {
        signalStrengthListener.startListening()
        locationHelper.startListening()
        isMonitoring = true
        Toast.makeText(this, "Monitoring started", Toast.LENGTH_SHORT).show()
    }

    private fun stopMonitoring() {
        signalStrengthListener.stopListening()
        locationHelper.stopListening()
        isMonitoring = false
        Toast.makeText(this, "Monitoring stopped", Toast.LENGTH_SHORT).show()
    }

    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Some permissions were denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sendSMSToServer(message: String) {
        try {
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                this.getSystemService(SmsManager::class.java)
            } else {
                SmsManager.getDefault()
            }
            smsManager.sendTextMessage(SERVER_PHONE_NUMBER, null, message, null, null)
            Log.d("SMS", "Message sent to server: $message")
        } catch (e: Exception) {
            Log.e("SMS", "Failed to send SMS", e)
            Toast.makeText(this, "Failed to send SMS", Toast.LENGTH_SHORT).show()
        }
    }

    fun getLastKnownLocation(): String {
        return locationHelper.getLastKnownLocation()
    }
}
