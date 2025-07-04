package com.example.foodrecipeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodrecipeapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    // untuk akses component ui yang ada di xml layoutnya
    private lateinit var binding: ActivityLoginBinding
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // akses file xml ke kotlin supaya ga pake findViewById Lagi
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // declare database helper
        databaseHelper = DatabaseHelper(this)

        binding.loginButton.setOnClickListener{
            val loginEmail = binding.loginEmail.text.toString().trim()
            val loginPassword = binding.LoginPassword.text.toString().trim()

            var isEmptyFields = false

            if (loginEmail.isEmpty()) {
                isEmptyFields = true
                binding.loginEmail.error = "This field must be filled"
            }

            if (loginPassword.isEmpty()) {
                isEmptyFields = true
                binding.LoginPassword.error = "This field must be filled"
            }

            if (!isEmptyFields) {
                loginDatabase(loginEmail, loginPassword)
            }

        }

        binding.signupRedirect.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loginDatabase(email: String, password: String) {
        val userExists = databaseHelper.readUser(email, password)
        if (userExists) {
            val name = databaseHelper.getUserByEmail(email)

            // simpan ke share preference biar tau siapa yang login
            val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("user_name", name)
            editor.putString("user_email", email)
            editor.apply()


            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
        }
    }
}