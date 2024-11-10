package com.safespot.safespot

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.safespot.safespot.databinding.ActivityWelcomePageBinding
import android.Manifest
import android.content.Context
import android.util.Log


class WelcomePage : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE)

        val name = sharedPreferences.getString("name", "Default Name")


        android.os.Handler().postDelayed({

            if (sharedPreferences.contains("name")) {
                val name = sharedPreferences.getString("name", "Default Name")
                val intent = Intent(this, EmergencyContactsPage::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }


        }, 1000)

        Log.d("Welcome page", "check audio")
        checkAudioPermission()
        checkNotificationPermission()

    }

    private fun checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED)
        {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("Welcome Page", "permission Granted")

        } else {
        }
    }



}