package com.example.barium

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.CellInfoLte
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat

class SignalStrengthListener(private val context: Context) : PhoneStateListener() {
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
        super.onSignalStrengthsChanged(signalStrength)

        val dBm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            signalStrength.cellSignalStrengths.firstOrNull()?.dbm ?: -1
        } else {
            val strength = signalStrength.gsmSignalStrength
            2 * strength - 113 // Convert to dBm
        }

        Log.d("SignalStrength", "Signal Strength: $dBm dBm")

        if (dBm < -110) {
            val location = (context as MainActivity).getLastKnownLocation()
            val cellInfo = getCellServingInfo()
            val message = "Signal strength: $dBm dBm\nLocation: $location\nCell Info: $cellInfo"
            (context as MainActivity).sendSMSToServer(message)
        }
    }

    private fun getCellServingInfo(): String {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "Permission not granted"
        }

        val cellInfo = telephonyManager.allCellInfo.firstOrNull { it.isRegistered }
        return when (cellInfo) {
            is CellInfoLte -> {
                val cellIdentity = cellInfo.cellIdentity
                "LTE - MCC: ${cellIdentity.mcc}, MNC: ${cellIdentity.mnc}, CI: ${cellIdentity.ci}, TAC: ${cellIdentity.tac}"
            }
            else -> "Cell info not available"
        }
    }

    fun startListening() {
        telephonyManager.listen(this, LISTEN_SIGNAL_STRENGTHS)
    }

    fun stopListening() {
        telephonyManager.listen(this, LISTEN_NONE)
    }
}