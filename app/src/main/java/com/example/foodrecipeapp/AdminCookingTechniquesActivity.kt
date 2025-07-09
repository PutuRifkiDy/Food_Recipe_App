package com.example.foodrecipeapp

import android.content.Intent
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
import com.example.foodrecipeapp.databinding.ActivityAdminCookingTechniquesBinding
import com.example.foodrecipeapp.model.Category
import com.example.foodrecipeapp.model.CookingTechnique
import java.io.File
import java.io.FileOutputStream

class AdminCookingTechniquesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminCookingTechniquesBinding
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

        binding = ActivityAdminCookingTechniquesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.createNewCookingTechnique.setOnClickListener {
            showCookingTechniqueDialog(null)
        }

        binding.backButtonCookingTechnique.setOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent)
        }

        // ambil semua category
        val dbHelper = DatabaseHelper(this)
        val cookingTechniqueList = dbHelper.getAllCookingTechnique()
        val container = binding.containerCookingTechnique

        for(cookingTechnique in cookingTechniqueList) {
            val cardView = layoutInflater.inflate(R.layout.item_cooking_technique_card, null)

            val tvTitleCookingTechnique = cardView.findViewById<TextView>(R.id.tvCookingTechniqueTitle)
            val tvMethodCookingTechnique = cardView.findViewById<TextView>(R.id.tvCookingTechniqueMethod)
            val tvDescriptionCookingTechnique = cardView.findViewById<TextView>(R.id.tvCookingTechniqueDescription)
            val image = cardView.findViewById<ImageView>(R.id.ivCookingTechnique)

            tvTitleCookingTechnique.text = cookingTechnique.title
//            tvMethodCookingTechnique.text = cookingTechnique.method
//            tvDescriptionCookingTechnique.text = cookingTechnique.description
            Glide.with(this)
                .load(cookingTechnique.imagePath)
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
            val deleteBtn  = cardView.findViewById<Button>(R.id.btnDeleteCookingTechnique)
            deleteBtn.setOnClickListener{
                AlertDialog.Builder(this)
                    .setTitle("Delete Cooking Technique")
                    .setMessage("Are you sure you want to delete this cooking technique?")
                    .setPositiveButton("Yes") { _, _ ->
                        val success = dbHelper.deleteCookingTechnique(cookingTechnique.id)
                        if (success) {
                            Toast.makeText(this, "Cooking Technique deleted", Toast.LENGTH_SHORT).show()
                            recreate()
                        } else {
                            Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
            }

            val editBtn = cardView.findViewById<Button>(R.id.btnEditCookingTechnique)
            editBtn.setOnClickListener {
                showCookingTechniqueDialog(cookingTechnique)
            }
        }
    }

    private fun showCookingTechniqueDialog(cookingTechnique: CookingTechnique?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_cooking_technique_admin, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.inputCookingTechniqueTitle)
        val etDescription = dialogView.findViewById<EditText>(R.id.inputCookingTechniqueDescription)
        val etMethod = dialogView.findViewById<EditText>(R.id.inputCookingTechniqueMethod)
        val uploadContainer = dialogView.findViewById<LinearLayout>(R.id.uploadContainer)
        val dbHelper = DatabaseHelper(this)
        imagePreview = dialogView.findViewById(R.id.imagePreview)

        // Reset image dan URI
        selectedImageUri = null
        imagePreview?.setImageDrawable(null)
        imagePreview?.visibility = View.GONE

        // Jika edit, isi data lama
        if (cookingTechnique != null) {
            etTitle.setText(cookingTechnique.title)
            etDescription.setText(cookingTechnique.description)
            etMethod.setText(cookingTechnique.method)
            if (!cookingTechnique.imagePath.isNullOrEmpty()) {
                imagePreview?.visibility = View.VISIBLE
                Glide.with(this).load(cookingTechnique.imagePath).into(imagePreview!!)
            }
        }

        uploadContainer.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        val isEditing = cookingTechnique != null

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle(if (isEditing) "Edit Cooking Techniques" else "Add New Cooking Technique")
            .setPositiveButton("Save") { _, _ ->
                val title = etTitle.text.toString().trim()
                val description = etDescription.text.toString().trim()
                val method = etMethod.text.toString().trim()
                val imagePath = when {
                    selectedImageUri != null -> saveImageToInternalStorage(selectedImageUri!!)
                    else -> cookingTechnique?.imagePath ?: ""
                }

                if (title.isEmpty() ||
                    (cookingTechnique == null && selectedImageUri == null) ||
                    description.isEmpty() ||
                    method.isEmpty()
                    ) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (isEditing) {
                    val updatedCookingTechnique = CookingTechnique(cookingTechnique!!.id, title, imagePath, description, method)
                    val success = dbHelper.updateCookingTechnique(updatedCookingTechnique)
                    if (success) {
                        Toast.makeText(this, "Cooking Technique updated!", Toast.LENGTH_SHORT).show()
                        recreate()
                    } else {
                        Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val result = dbHelper.insertCookingTechnique(title, imagePath,  description, method)
                    if (result != -1L) {
                        Toast.makeText(this, "Cooking Technique added!", Toast.LENGTH_SHORT).show()
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