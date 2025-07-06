package com.example.foodrecipeapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.foodrecipeapp.databinding.ActivityRecipeBinding

class RecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // back to profile
        binding.backButtonMyRecipe.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("navigate_to", "profile")
            startActivity(intent)
            finish()
        }

        val sharePref = getSharedPreferences("UserSession", MODE_PRIVATE)
        val getName = sharePref.getString("user_name", "Guest")
        val userId = sharePref.getInt("user_id", -1)
        val dbHelper = DatabaseHelper(this)

        // set text viewnya
        val nameTextView = binding.tvNameMyRecipe
        nameTextView.text = getName ?: "Guest"

        // count recipe berdasarkan user id
        val jumlahRecipe = dbHelper.countRecipeByUserId(userId)
        val countRecipeTv = binding.tvCountRecipe
        countRecipeTv.text = jumlahRecipe.toString() + " " + "Recipe"

        // ambil semua recipe
        val recipeList = dbHelper.getRecipesByUserId(userId)
        val container = binding.llRecipeContainer

        // tampilkan di dalam container dengan layout inflater
        for (recipe in recipeList) {
            val cardView = layoutInflater.inflate(R.layout.item_recipe_card, null)
            val categoryName = dbHelper.getCategoryNameById(recipe.categoryId)

            val title = cardView.findViewById<TextView>(R.id.tvRecipeTitle)
            val description = cardView.findViewById<TextView>(R.id.tvRecipeDescription)
            val image = cardView.findViewById<ImageView>(R.id.ivRecipeImage)
            val category = cardView.findViewById<TextView>(R.id.tvRecipeCategory)

            title.text = recipe.name
            description.text = recipe.description
            category.text = categoryName


            // tampilin image
            Glide.with(this)
                .load(recipe.imagePath)
                .into(image)

            // kasi gap
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.bottomMargin = 16

            cardView.layoutParams = layoutParams
            container.addView(cardView)

        }
    }
}