package com.safespot.safespot

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.math.truncate

class SoundDetectionService : Service() {

    private lateinit var audioRecord: AudioRecord
    private var isDetecting = false
    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
    private val page = "Detection"
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())

        Log.d(page,"detection class started")
        createNotificationChannel()
        startAudioDetection()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun createNotification(): Notification {
        val channelId = "SoundDetectionServiceChannel"
        val channelName = "Sound Detection Service"

        // For Android O and above, create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
        Log.d(page,"Notification Build")
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Safe Spot activated")
            .setContentText("Your safety is being monitored")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .build()
    }

    private fun startAudioDetection() {
        // Check for microphone permission
        Log.d(page,"Sound detection started")
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        isDetecting = true
        audioRecord.startRecording()

        Thread {
            Log.d(page,"threadss")
            val audioBuffer = ShortArray(bufferSize)
            while (isDetecting) {
                val readCount = audioRecord.read(audioBuffer, 0, bufferSize)
                if (readCount > 0) {
                    if (detectScream(audioBuffer)) {
                        triggerEmergencyResponse()
                        break // Stop after detecting a scream (optional)
                    }
                }
            }
        }.start()
    }

    private fun detectScream(audioData: ShortArray): Boolean {
        Log.d(page,"Scream detected")

        return audioData.maxOrNull()?.let { it > 30000 } ?: false
    }

    private fun triggerEmergencyResponse() {
        val sharedPreferencesC = getSharedPreferences("EmergencyContactDetails", Context.MODE_PRIVATE)
        val emergencyPhone = sharedPreferencesC.getString("emergencyPhone", "No Phone Number Provided")

        val sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE)

        val name = sharedPreferences.getString("name", "Default Name")

        val location = "sd"

        Log.d("SoundDetectionService", "Emergency sound detected. Triggering response.")
        sendNotification("Emergency Alert", "Scream detected. Emergency response triggered.")
        PhoneCallHelper.callPhoneNumber(applicationContext,emergencyPhone!!,sharedPreferences)

    }

    private fun sendNotification(title: String, message: String) {
        val notificationId = 2
        val channelId = "SoundDetectionAlertChannel"
        val notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Sound Detection Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(this).notify(notificationId, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "SoundDetectionServiceChannel"
            val channelName = "Sound Detection Service"
            val channelDescription = "Monitors audio for safety detection."

            val notificationManager = getSystemService(NotificationManager::class.java)
            val existingChannel = notificationManager?.getNotificationChannel(channelId)

            if (existingChannel == null) {
                val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = channelDescription
                    enableLights(true)
                    enableVibration(true)
                }

                notificationManager?.createNotificationChannel(channel)
            }
        }
    }


    private fun getCurrentLocation(callback: (Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callback(null)
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                callback(location)
            }
    }



    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        isDetecting = false  // Stops the detection thread
        audioRecord.stop()
        audioRecord.release()
    }
}
