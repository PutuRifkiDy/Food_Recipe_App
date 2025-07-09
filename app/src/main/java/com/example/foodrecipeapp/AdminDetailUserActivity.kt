package com.example.foodrecipeapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodrecipeapp.databinding.ActivityAdminDetailUserBinding
import com.example.foodrecipeapp.databinding.ActivityDetailRecipeBinding

class AdminDetailUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminDetailUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAdminDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener{
            val intent = Intent(this, AdminUserManagemenActivity::class.java)
            startActivity(intent)
        }

        val dbHelper = DatabaseHelper(this)
        val userId = intent.getIntExtra("user_id", -1)

        val detailUser = dbHelper.getUserByUserId(userId)

        binding.tvAboutDesc.text = detailUser?.about
        binding.tvBirthDesc.text = detailUser?.birthday
        binding.tvGenderDesc.text = detailUser?.gender
        binding.tvPhoneNumberDesc.text = detailUser?.phone
        binding.tvEmailDesc.text = detailUser?.email
    }
}