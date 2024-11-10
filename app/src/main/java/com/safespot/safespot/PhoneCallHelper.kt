package com.safespot.safespot

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class PhoneCallHelper {

    companion object {
        private const val REQUEST_CODE_CALL_PHONE = 101
        private const val REQUEST_CODE_SEND_SMS = 102

        // Function to make a phone call
        fun callPhoneNumber(context: Context, phoneNumber: String,sharedPreferences:SharedPreferences) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    REQUEST_CODE_CALL_PHONE
                )
                return
            }

            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$phoneNumber")
            callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            if (callIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(callIntent)

                val name = sharedPreferences.getString("name", "Default Name")
                var currLocation = "Location not found"

                getCurrentLocationOffline(context) { location ->
                    currLocation = location
                    // Use the location coordinates here
                    sendSms(context, phoneNumber,"$name is in danger, this is an SOS message from $currLocation")

                }

            } else {
                Toast.makeText(context, "No app found to make a call", Toast.LENGTH_SHORT).show()
            }
        }

        fun sendSms(context: Context, phoneNumber: String, message: String) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.SEND_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.SEND_SMS),
                    REQUEST_CODE_SEND_SMS
                )
                return
            }

            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$phoneNumber")
            callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(context, "SMS Sent", Toast.LENGTH_SHORT).show()
        }

        fun getCurrentLocationOffline(context: Context, callback: (String) -> Unit) {
            val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                callback("Location permission not granted")
                return
            }

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val locationText = "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
                        callback(locationText)
                    } else {
                        callback("Unable to retrieve location")
                    }
                }
                .addOnFailureListener {
                    callback("Failed to retrieve location")
                }
        }


        fun handlePermissionResult(requestCode: Int, grantResults: IntArray) {
            if (requestCode == REQUEST_CODE_CALL_PHONE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
            } else if (requestCode == REQUEST_CODE_SEND_SMS) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
            }
        }
    }
}
