package com.example.foodrecipeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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

        // fungsi logout
        fun logoutUser() {
            val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.clear()
            editor.remove("is_guest")
            editor.apply()

            Toast.makeText(this, "Logout Successfull", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LandingPageActivity::class.java)
            startActivity(intent)
            finish()
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

        binding.btnLogoutAdmin.setOnClickListener {
            logoutUser()
        }

        val dbHelper = DatabaseHelper(this)
        val countCategory = dbHelper.countCategory()
        val countUser = dbHelper.countUser()
        val countTechnique = dbHelper.countCookingTechnique()

        binding.tvTotalCategories.text = "Total Categories : $countCategory"
        binding.tvTotalTechniques.text = "Total Cooking Technique : $countTechnique"
        binding.tvTotalUser.text = "Total User : $countUser"
    }
}