package com.example.foodrecipeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.foodrecipeapp.databinding.ActivityRecipeCategoryFilterBinding

class RecipeCategoryFilterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeCategoryFilterBinding
    private lateinit var dbHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRecipeCategoryFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        dbHelper = DatabaseHelper(this)
        val categoryId = intent.getIntExtra("category_id", -1)
        val container = binding.containerFilterRecipeByCategory

        val categoryName = dbHelper.getCategoryNameById(categoryId)

        binding.backButtonRecipeFilterCategory.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("navigate_to", "home")
            startActivity(intent)
            finish()
        }

        binding.labelTitleRecipeFilterCategory.text = categoryName

        // get semua category berdasarkan categoryId
        val recipeList = dbHelper.getRecipeByCategoryId(categoryId)
        for(recipe in recipeList){
            val cardView = layoutInflater.inflate(R.layout.item_recipe_category_filter, null)
            val userTitle = dbHelper.getUserByRecipeUserId(recipe.userId)
            val categoryName = dbHelper.getCategoryNameById(recipe.categoryId)

            val tvUserName = cardView.findViewById<TextView>(R.id.tvUsernameFilterCategory)
            val ivImage = cardView.findViewById<ImageView>(R.id.ivRecipeImageFilterCategory)
            val tvRecipeName = cardView.findViewById<TextView>(R.id.tvRecipeNameFilterCategory)
            val tvCategoryName = cardView.findViewById<TextView>(R.id.tvCategoryNameFilterCategory)
            val tvDescription = cardView.findViewById<TextView>(R.id.tvRecipeDescriptionFilterCategory)
            val btnSeeDetail = cardView.findViewById<Button>(R.id.btnSeeDetailFilterCategory)

            // set ke componen masing masing
            tvUserName.text = userTitle
            tvRecipeName.text = recipe.name
            tvCategoryName.text = categoryName
            tvDescription.text = recipe.description
            Glide.with(this)
                .load(recipe.imagePath)
                .into(ivImage)

            // kasi gap
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.bottomMargin = 16

            // menampilkan cardnya semua
            cardView.layoutParams = layoutParams
            container.addView(cardView)

            btnSeeDetail.setOnClickListener {
                val intent = Intent(this, detailRecipeActivity::class.java)
                intent.putExtra("recipe_id", recipe.id)
                startActivity(intent)
            }

        }
    }
}