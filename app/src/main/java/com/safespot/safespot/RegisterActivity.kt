package com.safespot.safespot

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var addressEditText: EditText
    private lateinit var nextButton: Button

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        nameEditText = findViewById(R.id.nameEditText)
        ageEditText = findViewById(R.id.ageEditText)
        genderRadioGroup = findViewById(R.id.RG)
        addressEditText = findViewById(R.id.addressEditText)
        nextButton = findViewById(R.id.nextButton)

        sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE)



        nextButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val age = ageEditText.text.toString().trim()
            val address = addressEditText.text.toString().trim()
            val selectedGenderId = genderRadioGroup.checkedRadioButtonId
            val gender = findViewById<RadioButton>(selectedGenderId)?.text.toString()

            if (name.isEmpty() || age.isEmpty() || address.isEmpty() || selectedGenderId == -1) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveUserData(name, age, gender, address)

            val intent = Intent(this, EmergencyContactsPage::class.java)
            startActivity(intent)
        }
    }
    private fun saveUserData(name: String, age: String, gender: String, address: String) {
        val editor = sharedPreferences.edit()
        editor.putString("name", name)
        editor.putString("age", age)
        editor.putString("gender", gender)
        editor.putString("address", address)
        editor.apply()

    }

    private fun display(){


        val sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE)

        val name = sharedPreferences.getString("name", "Default Name")
        val age = sharedPreferences.getString("age", "0")
        val gender = sharedPreferences.getString("gender", "Not Specified")
        val address = sharedPreferences.getString("address", "Not Specified")

        Log.d("UserData", "Name: $name, Age: $age, Gender: $gender, Address: $address")

    }
}
