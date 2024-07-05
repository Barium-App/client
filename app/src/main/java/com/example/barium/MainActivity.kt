package com.example.barium

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var passwordEditText: EditText
    private lateinit var connectButton: Button
    private lateinit var monitoringLayout: LinearLayout
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var dataGrid: RecyclerView
    private lateinit var signalStrengthListener: SignalStrengthListener
    private lateinit var locationHelper: LocationHelper


    companion object {
        private const val SERVER_PHONE_NUMBER = "+989029518712" // Replace with actual server number
        private const val PERMISSION_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connectButton = findViewById(R.id.connectButton)
        passwordEditText = findViewById(R.id.passwordEditText)

        monitoringLayout = findViewById(R.id.monitoringLayout)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        dataGrid = findViewById(R.id.dataGrid)

        signalStrengthListener = SignalStrengthListener(this)
        locationHelper = LocationHelper(this)


        connectButton.setOnClickListener {
            val password = passwordEditText.text.toString().trim()
            if (password.isNotEmpty()) {
                sendSMSToServer(password)
                Toast.makeText(this, "Sending SMS with password...", Toast.LENGTH_SHORT).show()
                monitoringLayout.visibility = View.VISIBLE
                connectButton.visibility = View.GONE
            } else {
                Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show()
            }
        }



        startButton.setOnClickListener {
            startMonitoring()
        }

        stopButton.setOnClickListener {
            stopMonitoring()
        }

        checkAndRequestPermissions()
    }

    private fun startMonitoring() {
        signalStrengthListener.startListening()
        locationHelper.startListening()
    }

    private fun stopMonitoring() {
        signalStrengthListener.stopListening()
        locationHelper.stopListening()
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
                getSystemService(SmsManager::class.java)
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