package com.example.foodrecipeapp

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodrecipeapp.databinding.ActivityEditprofileBinding

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditprofileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditprofileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backButtonEditProfile.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("navigate_to", "profile")
            startActivity(intent)
            finish()
        }

        val sharePrev = getSharedPreferences("UserSession", MODE_PRIVATE)
        val email = sharePrev.getString("user_email", null)

        if (email != null) {
            val dbHelper = DatabaseHelper(this)
            val user = dbHelper.getUserDataByEmail(email)

            if (user != null) {
                binding.name.setText(user.name)
                binding.about.setText(user.about)
                binding.dob.setText(user.birthday)
                binding.number.setText(user.phone)
                binding.email.setText(user.email)
                when (user.gender) {
                    "Female" -> binding.genderGroup.check(R.id.female)
                    "Male" -> binding.genderGroup.check(R.id.male)
                }
            }
        }

        binding.saveButton.setOnClickListener{
            val newName = binding.name.text.toString()
            val newAbout = binding.about.text.toString()
            val newBirthday = binding.dob.text.toString()
            val newPhone = binding.number.text.toString()
            val newEmail = binding.email.text.toString()

            val selectedGenderId = binding.genderGroup.checkedRadioButtonId
            val selectedGender = when (selectedGenderId) {
                R.id.female -> "Female"
                R.id.male -> "Male"
                else -> ""
            }

            if (email != null) {
                val dbHelper = DatabaseHelper(this)
                val values = ContentValues().apply {
                    put("name", newName)
                    put("about", newAbout)
                    put("birthday", newBirthday)
                    put("phone_number", newPhone)
                    put("email", newEmail)
                    put("gender", selectedGender)
                }

                val db = dbHelper.writableDatabase
                db.update("user", values, "email = ?", arrayOf(email))

                // update di sharedPreferencesnya juga
                val editor = sharePrev.edit()
                editor.putString("user_name", newName)
                editor.putString("user_about", newAbout)
                editor.putString("user_birthday", newBirthday)
                editor.putString("phone_number", newPhone)
                editor.putString("user_email", newEmail)
                editor.putString("user_gender", selectedGender)
                editor.apply()

                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("navigate_to", "profile")
                startActivity(intent)
                finish()
            }
        }

    }
}