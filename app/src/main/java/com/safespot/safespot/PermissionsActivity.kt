package com.safespot.safespot

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.CheckBox
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.safespot.safespot.databinding.ActivityPermissionsBinding


class PermissionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionsBinding

    private var audioPermissionGranted = false
    private var locationPermissionGranted = false
    private var notificationPermissionGranted = false
    private var callPermissionGranted = false
    private var smsPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val audioPermissionCheckBox: CheckBox = binding.audioPermissionCheckBox
        val locationPermissionCheckBox: CheckBox = binding.locationPermissionCheckBox
        val smsPermissionCheckBox: CheckBox = binding.smsPermissionCheckBox
        val notificationPermissionCheckBox: CheckBox = binding.notificationPermissionCheckBox

        binding.continueButton.isEnabled = false

        checkPermissions()

        audioPermissionCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }

        locationPermissionCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        smsPermissionCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                permissionLauncher.launch(Manifest.permission.SEND_SMS)
            }
        }

        notificationPermissionCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        binding.continueButton.setOnClickListener {
            navigateToWelcomePage()
            finish()
        }
    }


    private fun checkPermissions() {
        audioPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        notificationPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        callPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
        locationPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        smsPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED

        binding.audioPermissionCheckBox.isChecked = audioPermissionGranted
        binding.locationPermissionCheckBox.isChecked = locationPermissionGranted
        binding.smsPermissionCheckBox.isChecked = smsPermissionGranted
        binding.notificationPermissionCheckBox.isChecked = notificationPermissionGranted

        if (audioPermissionGranted && notificationPermissionGranted && callPermissionGranted && smsPermissionGranted) {
            binding.continueButton.isEnabled = true
            navigateToWelcomePage()
            finish()
        } else {
            binding.continueButton.isEnabled = false
        }

        if (!audioPermissionGranted) permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        if (!notificationPermissionGranted) permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        if (!callPermissionGranted) permissionLauncher.launch(Manifest.permission.CALL_PHONE)
        if (!locationPermissionGranted) permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        if (!smsPermissionGranted) permissionLauncher.launch(Manifest.permission.SEND_SMS)
    }


    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            checkPermissions()
        } else {
        }
    }

    private fun navigateToWelcomePage() {
        val intent = Intent(this, WelcomePage::class.java)
        startActivity(intent)
        finish()
    }
}

