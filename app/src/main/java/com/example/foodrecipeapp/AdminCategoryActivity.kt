package com.example.foodrecipeapp

import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.foodrecipeapp.databinding.ActivityAdminCategoryBinding
import com.example.foodrecipeapp.model.Category
import java.io.File
import java.io.FileOutputStream

class AdminCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminCategoryBinding
    private lateinit var databaseHelper: DatabaseHelper
    private var selectedImageUri: Uri? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var imagePreview: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Registrasi launcher HARUS dilakukan sebelum activity RESUMED
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                selectedImageUri = result.data?.data
                imagePreview?.setImageURI(selectedImageUri)
                imagePreview?.visibility = View.VISIBLE
            }
        }

        binding = ActivityAdminCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createNewCategory.setOnClickListener {
            showCategoryDialog(null)
        }

        binding.backButtonManageCategory.setOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent)
        }

        // ambil semua category
        val dbHelper = DatabaseHelper(this)
        val categoryList = dbHelper.getAllCategories()
        val container = binding.containerCategories

        for(category in categoryList) {
            val cardView = layoutInflater.inflate(R.layout.item_category_card, null)

            val tvTitle = cardView.findViewById<TextView>(R.id.tvCategoryTitle)
            val image = cardView.findViewById<ImageView>(R.id.ivcategoryIcon)

            tvTitle.text = category.category_name
            Glide.with(this)
                .load(category.iconPath)
                .into(image)

            // kasi gap
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.bottomMargin = 16

            // menampilkan cardnya semua
            cardView.layoutParams = layoutParams
            container.addView(cardView)

            // delete
            // delete recipe
            val deleteBtn  = cardView.findViewById<Button>(R.id.btnDeleteCategory)
            deleteBtn.setOnClickListener{
                AlertDialog.Builder(this)
                    .setTitle("Delete Category")
                    .setMessage("Are you sure you want to delete this category?")
                    .setPositiveButton("Yes") { _, _ ->
                        val success = dbHelper.deleteCategoryById(category.id)
                        if (success) {
                            Toast.makeText(this, "Category deleted", Toast.LENGTH_SHORT).show()
                            recreate()
                        } else {
                            Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
            }

            val editBtn = cardView.findViewById<Button>(R.id.btnEditCategory)
            editBtn.setOnClickListener {
                showCategoryDialog(category)
            }

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showCategoryDialog(category: Category?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_category_admin_input, null)
        val etName = dialogView.findViewById<EditText>(R.id.inputCategory)
        val uploadContainer = dialogView.findViewById<LinearLayout>(R.id.uploadContainer)
        val dbHelper = DatabaseHelper(this)
        imagePreview = dialogView.findViewById(R.id.imagePreview)

        // Reset image dan URI
        selectedImageUri = null
        imagePreview?.setImageDrawable(null)
        imagePreview?.visibility = View.GONE

        // Jika edit, isi data lama
        if (category != null) {
            etName.setText(category.category_name)
            if (!category.iconPath.isNullOrEmpty()) {
                imagePreview?.visibility = View.VISIBLE
                Glide.with(this).load(category.iconPath).into(imagePreview!!)
            }
        }

        uploadContainer.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        val isEditing = category != null

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle(if (isEditing) "Edit Category" else "Add New Category")
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString().trim()

                if (name.isEmpty() || (category == null && selectedImageUri == null)) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val imagePath = when {
                    selectedImageUri != null -> saveImageToInternalStorage(selectedImageUri!!)
                    else -> category?.iconPath ?: ""
                }

                if (isEditing) {
                    val updatedCategory = Category(category!!.id, name, imagePath)
                    val success = dbHelper.updateCategory(updatedCategory)
                    if (success) {
                        Toast.makeText(this, "Category updated!", Toast.LENGTH_SHORT).show()
                        recreate()
                    } else {
                        Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val result = dbHelper.insertCategory(name, imagePath)
                    if (result != -1L) {
                        Toast.makeText(this, "Category added!", Toast.LENGTH_SHORT).show()
                        recreate()
                    } else {
                        Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveImageToInternalStorage(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val fileName = "CAT_${System.currentTimeMillis()}.jpg"
        val file = File(filesDir, fileName)

        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        return file.absolutePath
    }
}