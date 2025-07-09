package com.example.foodrecipeapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.foodrecipeapp.databinding.ActivityCookingTechniquesActivityBinding

class CookingTechniquesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCookingTechniquesActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCookingTechniquesActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backButtonTechniques.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("navigate_to", "home")
            startActivity(intent)
        }

        val cookingTechniqueId = intent.getIntExtra("cooking_technique_id", -1)
        val dbHelper = DatabaseHelper(this)
        val detailCookingTechnique = dbHelper.getCookingTechniqueById(cookingTechniqueId)

        val tvTitleTechnique = binding.labelTitleTechniques
        val tvBtnBackTechnique = binding.backButtonTechniques
        val imageTechnique = binding.imageTechniques
        val tvDescription = binding.contentDescription
        val tvMethodDescription = binding.methodDescriptionTechniques

        tvTitleTechnique.text = detailCookingTechnique?.title
        Glide.with(this)
            .load(detailCookingTechnique?.imagePath)
            .into(imageTechnique)
        tvDescription.text = detailCookingTechnique?.description
        tvMethodDescription.text = detailCookingTechnique?.method

        tvBtnBackTechnique.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("navigate_to", "home")
            startActivity(intent)
        }

    }
}