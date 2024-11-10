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

    // Variables to track the permission statuses
    private var audioPermissionGranted = false
    private var locationPermissionGranted = false
    private var notificationPermissionGranted = false
    private var callPermissionGranted = false
    private var smsPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize CheckBoxes
        val audioPermissionCheckBox: CheckBox = binding.audioPermissionCheckBox
        val locationPermissionCheckBox: CheckBox = binding.locationPermissionCheckBox
        val smsPermissionCheckBox: CheckBox = binding.smsPermissionCheckBox
        val notificationPermissionCheckBox: CheckBox = binding.notificationPermissionCheckBox

        // Initially disable the continue button
        binding.continueButton.isEnabled = false

        // Check the current status of permissions when the activity starts
        checkPermissions()

        // Handle CheckBox click events if needed
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

        // Continue button click listener to navigate to WelcomePage
        binding.continueButton.setOnClickListener {
            navigateToWelcomePage()
            finish()
        }
    }

// In your PermissionsActivity.kt file

// Handle CheckBox click event for SMS permission

    // Check permissions when the activity starts
    private fun checkPermissions() {
        // Check if the necessary permissions are granted
        audioPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        notificationPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        callPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
        locationPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        smsPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED

        // Update the CheckBoxes based on the current permission status
        binding.audioPermissionCheckBox.isChecked = audioPermissionGranted
        binding.locationPermissionCheckBox.isChecked = locationPermissionGranted
        binding.smsPermissionCheckBox.isChecked = smsPermissionGranted
        binding.notificationPermissionCheckBox.isChecked = notificationPermissionGranted

        // Enable Continue button only if all permissions are granted
        if (audioPermissionGranted && notificationPermissionGranted && callPermissionGranted && smsPermissionGranted) {
            binding.continueButton.isEnabled = true
            navigateToWelcomePage()
            finish()
        } else {
            binding.continueButton.isEnabled = false
        }

        // Request the permissions if they are not granted
        if (!audioPermissionGranted) permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        if (!notificationPermissionGranted) permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        if (!callPermissionGranted) permissionLauncher.launch(Manifest.permission.CALL_PHONE)
        if (!locationPermissionGranted) permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        if (!smsPermissionGranted) permissionLauncher.launch(Manifest.permission.SEND_SMS)
    }


    // Register for the permission result, which handles the permission request results
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        // Update the permissions status dynamically based on the result
        if (isGranted) {
            checkPermissions()  // Recheck permissions after they have been granted
        } else {
            // Handle the case where permission is denied (e.g., show a message to the user)
            // You can show a message explaining why the permission is necessary
        }
    }

    // Navigate to WelcomePage once all permissions are granted
    private fun navigateToWelcomePage() {
        val intent = Intent(this, WelcomePage::class.java)
        startActivity(intent)
        finish()
    }
}

