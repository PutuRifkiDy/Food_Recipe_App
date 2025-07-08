package com.example.foodrecipeapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodrecipeapp.databinding.ActivityEditRecipeBinding
import java.io.File
import java.io.FileOutputStream

class EditRecipeActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private var recipeId: Int = -1
    private lateinit var binding: ActivityEditRecipeBinding
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null

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

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                selectedImageUri = data?.data
                binding.imagePreview.setImageURI(selectedImageUri)
                binding.imagePreview.visibility = View.VISIBLE
            }
        }

        // intent ke my recipe lagi
        val btnBack = binding.buttonBack
        btnBack.setOnClickListener{
            val intent = Intent(this, RecipeActivity::class.java)
            startActivity(intent)
        }


        dbHelper = DatabaseHelper(this)
        recipeId = intent.getIntExtra("recipe_id", -1)

        if (recipeId == -1) {
            Toast.makeText(this, "Invalid recipe", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // ambil recipe terdahulu
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
//        val imageFile = File(recipe.imagePath)
//        Glide.with(this)
//            .load(Uri.fromFile(imageFile))
//            .into(inputImagePath)
////        Glide.with(this)
////            .load(recipe.imagePath)
////            .into(inputImagePath)

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


        // start input
        binding.uploadContainer.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }
        fun saveImageToInternalStorage(uri: Uri): String {
            val inputStream = contentResolver.openInputStream(uri)
            val fileName = "IMG_${System.currentTimeMillis()}.jpg"
            val file = File(filesDir, fileName)

            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            return file.absolutePath
        }

        binding.updateRecipeButton.setOnClickListener{
            val name = binding.inputRecipeName.text.toString()
            val description = binding.inputRecipeDescription.text.toString()
            val ingredients = binding.inputRecipeIngredients.text.toString()
            val tools = binding.inputRecipeTools.text.toString()
            val steps = binding.inputRecipeSteps.text.toString()
            val nutritionInfo = binding.inputRecipeNutritionInfo.text.toString()
            val categoryName = binding.categorySpinner.selectedItem.toString()

            if (
                name.isBlank() ||
                description.isBlank() ||
                ingredients.isBlank() ||
                tools.isBlank() ||
                steps.isBlank() ||
                nutritionInfo.isBlank() ||
                categoryName.isBlank()
            ) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val categoryId = dbHelper.getOrInsertCategory(categoryName)
            if (categoryId == -1) {
                Toast.makeText(this, "Invalid category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simpan gambar baru kalau ada
            val imagePath: String = if (selectedImageUri != null) {
                saveImageToInternalStorage(selectedImageUri!!)
            } else {
                recipe.imagePath // pakai gambar lama
            }

            val isUpdated = dbHelper.updateRecipe(
                recipeId,
                name,
                description,
                ingredients,
                tools,
                steps,
                nutritionInfo,
                imagePath,
                categoryId
            )

            if (isUpdated) {
                Toast.makeText(this, "Recipe updated!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, RecipeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Failed to update recipe", Toast.LENGTH_SHORT).show()
            }
        }


    }
}