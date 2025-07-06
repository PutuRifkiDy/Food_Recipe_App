package com.example.foodrecipeapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.foodrecipeapp.databinding.ActivityEditRecipeBinding

class EditRecipeActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private var recipeId: Int = -1
    private lateinit var binding: ActivityEditRecipeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        recipeId = intent.getIntExtra("recipe_id", -1)

        if (recipeId == -1) {
            Toast.makeText(this, "Invalid recipe", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val recipe = dbHelper.getRecipeById(recipeId)
        if (recipe == null) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // isi input denga data yang lama
        val inputName = binding.inputRecipeName
        val inputDescription = binding.inputRecipeDescription
        val inputIngredients = binding.inputRecipeIngredients
        val inputTools = binding.inputRecipeTools
        val inputStep = binding.inputRecipeSteps
        val inputNutritionInfo = binding.inputRecipeNutritionInfo
        val inputImagePath = binding.imagePreview
        val inputCategory = binding.categorySpinner

        inputName.setText(recipe.name)
        inputDescription.setText(recipe.description)
        inputIngredients.setText(recipe.ingredients)
        inputTools.setText(recipe.tools)
        inputStep.setText(recipe.steps)
        inputNutritionInfo.setText(recipe.nutritionInfo)
        Glide.with(this)
            .load(recipe.imagePath)
            .into(inputImagePath)

        // set spinner pilihan
        val categoryList = dbHelper.getAllCategories() // Ambil list kategori dari DB
        val categoryNames = categoryList.map { it.category_name }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inputCategory.adapter = adapter
        val categoryName = dbHelper.getCategoryNameById(recipe.categoryId)
        val categoryAdapter = inputCategory.adapter
        for (i in 0 until categoryAdapter.count) {
            if (categoryAdapter.getItem(i).toString() == categoryName) {
                inputCategory.setSelection(i)
                break
            }
        }

    }
}