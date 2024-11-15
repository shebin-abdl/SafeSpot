package com.safespot.safespot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.content.ContextCompat

class EmergencyContactsPage : AppCompatActivity() {

    private lateinit var emergencyNameEditText: EditText
    private lateinit var emergencyPhoneEditText: EditText
    private lateinit var emergencyRelationshipEditText: EditText
    private lateinit var saveButton: Button


    override fun onResume() {
        super.onResume()
        dispaly()
    }

    override fun onRestart() {
        super.onRestart()
        dispaly()
    }

    override fun onPause() {
        super.onPause()
        dispaly()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_contacts_page)


        emergencyNameEditText = findViewById(R.id.emergencyNameEditText)
        emergencyPhoneEditText = findViewById(R.id.emergencyPhoneEditText)
        emergencyRelationshipEditText = findViewById(R.id.emergencyRelationshipEditText)
        saveButton = findViewById(R.id.saveButton)

        dispaly()
        saveButton.setOnClickListener {
            saveEmergencyContact()
            dispaly()
        }
    }

    private fun dispaly(){
        val sharedPreferences = getSharedPreferences("EmergencyContactDetails", Context.MODE_PRIVATE)
        val emergencyName = sharedPreferences.getString("emergencyName", "No Name Provided")
        val emergencyPhone = sharedPreferences.getString("emergencyPhone", "No Phone Number Provided")
        val emergencyRelationship = sharedPreferences.getString("emergencyRelationship", "No Relationship Provided")

        if (sharedPreferences.contains("emergencyPhone")){
            emergencyNameEditText.setText(emergencyName)
            emergencyPhoneEditText.setText(emergencyPhone)
            emergencyRelationshipEditText.setText(emergencyRelationship)
            startSoundDetectionService()

        }
    }
    private fun saveEmergencyContact() {
        val emergencyName = emergencyNameEditText.text.toString()
        val emergencyPhone = emergencyPhoneEditText.text.toString()
        val emergencyRelationship = emergencyRelationshipEditText.text.toString()

        val sharedPreferences = getSharedPreferences("EmergencyContactDetails", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("emergencyName", emergencyName)
        editor.putString("emergencyPhone", emergencyPhone)
        editor.putString("emergencyRelationship", emergencyRelationship)
        editor.apply()

        Toast.makeText(this, "Emergency contact saved!", Toast.LENGTH_SHORT).show()
        dispaly()
    }


    private fun startSoundDetectionService() {
        val serviceIntent = Intent(this, SoundDetectionService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }
}
