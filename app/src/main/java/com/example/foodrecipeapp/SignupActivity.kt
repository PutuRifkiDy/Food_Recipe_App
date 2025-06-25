package com.example.foodrecipeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodrecipeapp.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    // untuk akses component ui yang ada di xml layoutnya
    private lateinit var binding: ActivitySignupBinding
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // akses file xml ke kotlin supaya ga pake findViewById Lagi
        binding = ActivitySignupBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // declare database helper
        databaseHelper = DatabaseHelper(this)

        binding.signupButton.setOnClickListener{
            val signupName = binding.signupName.text.toString().trim()
            val signupPhone = binding.signupPhoneNumber.text.toString().trim()
            val signupEmail = binding.signupEmail.text.toString().trim()
            val signupPassword = binding.signupPassword.text.toString().trim()

            var isEmptyFields = false

            if (signupName.isEmpty()) {
                isEmptyFields = true
                binding.signupName.error = "This field must be filled"
            }

            if (signupPhone.isEmpty()) {
                isEmptyFields = true
                binding.signupPhoneNumber.error = "This field must be filled"
            }

            if (signupEmail.isEmpty()) {
                isEmptyFields = true
                binding.signupEmail.error = "This field must be filled"
            }

            if (signupPassword.isEmpty()) {
                isEmptyFields = true
                binding.signupPassword.error = "This field must be filled"
            }

            if (!isEmptyFields) {
                // store ke database di fungsi yang udah dibuat di bawah
                signupDatabase(signupName, signupPhone, signupEmail, signupPassword)
            }

        }

        // redirect ke login page use intent
        binding.loginRedirect.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun signupDatabase(name: String, phone: String, email: String, password: String) {
        val insertedRowId = databaseHelper.insertUser(name, phone, email, password)
        if (insertedRowId != -1L) {
            Toast.makeText(this, "Signup Successfull", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Signup Failed", Toast.LENGTH_SHORT).show()
        }
    }
}