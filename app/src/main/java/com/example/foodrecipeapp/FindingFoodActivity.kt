package com.example.foodrecipeapp

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.foodrecipeapp.databinding.ActivityFindingFoodBinding
import com.example.foodrecipeapp.model.Recipe

class FindingFoodActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFindingFoodBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFindingFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dbHelper = DatabaseHelper(this)
        binding.searchRecipeName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak perlu diisi
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val keyword = s.toString()
                val results = dbHelper.searchRecipesByName(keyword)
                displayRecipes(results)
            }

            override fun afterTextChanged(s: Editable?) {
                // Tidak perlu diisi
            }
        })

        binding.backButtonTechniques.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("navigate_to", "home")
            startActivity(intent)
        }
    }

    private fun displayRecipes(recipes: List<Recipe>) {
        binding.containerCardRecipe.removeAllViews()
        val dbHelper = DatabaseHelper(this)
        val container = binding.containerCardRecipe

        for (recipe in recipes) {
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
            layoutParams.topMargin = 16

            // menampilkan cardnya semua
            cardView.layoutParams = layoutParams

            binding.containerCardRecipe.addView(cardView)

            btnSeeDetail.setOnClickListener {
                val intent = Intent(this, detailRecipeActivity::class.java)
                intent.putExtra("recipe_id", recipe.id)
                startActivity(intent)
            }
        }
    }
}