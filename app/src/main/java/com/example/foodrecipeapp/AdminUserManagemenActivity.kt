package com.example.foodrecipeapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodrecipeapp.databinding.ActivityAdminUserManagemenBinding

class AdminUserManagemenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminUserManagemenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminUserManagemenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backButtonManageUser.setOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent)
        }

        val dbHelper = DatabaseHelper(this)
        val userList = dbHelper.getAllCookingTechnique()
        val container = binding.containerManageUser
    }
}