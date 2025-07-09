package com.example.foodrecipeapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodrecipeapp.databinding.ActivityAdminDashboardBinding

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // intent ke activity masing masing
        binding.btnManageCategory.setOnClickListener{
            val intent = Intent(this, AdminCategoryActivity::class.java)
            startActivity(intent)
        }

        binding.btnManageUsers.setOnClickListener{
            val intent = Intent(this, AdminUserManagemenActivity::class.java)
            startActivity(intent)
        }

        binding.btnManageTechniques.setOnClickListener{
            val intent = Intent(this, AdminCookingTechniquesActivity::class.java)
            startActivity(intent)
        }
    }
}