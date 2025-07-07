package com.example.foodrecipeapp

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.foodrecipeapp.databinding.ActivityDetailRecipeBinding
import com.example.foodrecipeapp.databinding.ActivityLandingPageBinding

class detailRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailRecipeBinding
    private  lateinit var dbHelper: DatabaseHelper
    private var recipeId = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        recipeId = intent.getIntExtra("recipe_id", -1)

        // cari category dan recipe berdasarkan id nya
        val recipeDetail = dbHelper.getRecipeById(recipeId)


        if (recipeDetail != null) {
            val categoryName = dbHelper.getCategoryNameById(recipeDetail.categoryId)

            // selanjutnya set semua text view dengan yang ada di database
            val titleName = binding.labelTitleRecipe
            val imageRecipe = binding.imageRecipe
            val labelRecipe = binding.labelRecipe
            val typeFood = binding.labelTypeFood
            val contentDescription = binding.labelContentDescription
            val contentIngredients = binding.labelContentIngredients
            val contentTools = binding.labelContentTools
            val contentSteps = binding.labelContentSteps
            val contentNutrition = binding.labelContentNutrition

            titleName.text = recipeDetail.name ?: "Uknown"
            Glide.with(this)
                .load(recipeDetail.imagePath)
                .into(imageRecipe)
            labelRecipe.text = recipeDetail.name ?: "Uknown"
            typeFood.text = categoryName ?: "Uknown"
            contentDescription.text = recipeDetail.description ?: "Uknown"
            contentIngredients.text = recipeDetail.ingredients ?: "Uknown"
            contentTools.text = recipeDetail.tools ?: "Uknown"
            contentSteps.text = recipeDetail.steps ?: "Uknown"
            contentNutrition.text = recipeDetail.nutritionInfo ?: "Uknown"
        }

        binding.backButtonRecipe.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("navigate_to", "home")
            startActivity(intent)
            finish()
        }
    }
}