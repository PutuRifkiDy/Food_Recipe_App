package com.example.foodrecipeapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.Profile
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddRecipeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddRecipeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var databaseHelper: DatabaseHelper
    private var selectedImageUri: Uri? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // Inisialisasi launcher untuk pilih gambar
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    val data: Intent? = result.data
                    selectedImageUri = data?.data
                    val imagePreview = view?.findViewById<ImageView>(R.id.imagePreview)
                    imagePreview?.setImageURI(selectedImageUri)
                    imagePreview?.visibility = View.VISIBLE
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_recipe, container, false)

        // untuk cek apakah orang ini guest
        val sharedPref = requireContext().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val isGuest = sharedPref.getBoolean("is_guest", false)
        if (isGuest) {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
            return null
        }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil referensi Spinner
        val spinner = view.findViewById<Spinner>(R.id.categorySpinner)
        // Data kategori
        val categories = listOf("Choose Category", "Vegetable", "Meat", "Drink", "Seafood", "Snack")


        // Adapter untuk mengisi data ke spinner
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        // Set adapter ke spinner
        spinner.adapter = adapter

        // Optional: aksi saat item dipilih
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                val selected = categories[position]
                // Lakukan sesuatu jika kategori dipilih
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Jika tidak ada yang dipilih
            }
        }

        // Upload Gambar
        val uploadContainer = view.findViewById<LinearLayout>(R.id.uploadContainer)
        uploadContainer.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        // proses insert recipe
        val addButton = view.findViewById<Button>(R.id.addRecipeButton)
        databaseHelper = DatabaseHelper(requireContext())
        addButton.setOnClickListener{
            val name = view.findViewById<EditText>(R.id.inputRecipeName).text.toString()
            val description = view.findViewById<EditText>(R.id.inputRecipeDescription).text.toString()
            val ingredients = view.findViewById<EditText>(R.id.inputRecipeIngredients).text.toString()
            val tools = view.findViewById<EditText>(R.id.inputRecipeTools).text.toString()
            val steps = view.findViewById<EditText>(R.id.inputRecipeSteps).text.toString()
            val nutritionInfo = view.findViewById<EditText>(R.id.inputRecipeNutritionInfo).text.toString()
            val reference = view.findViewById<EditText>(R.id.inputRecipeReferences).text.toString()
            val categoryName = spinner.selectedItem.toString()

            // validasi input kosong
            if (
                name.isBlank() ||
                description.isBlank() ||
                ingredients.isBlank() ||
                tools.isBlank() ||
                steps.isBlank() ||
                nutritionInfo.isBlank() ||
                reference.isBlank() ||
                categoryName == "Choose Category" ||
                selectedImageUri == null
            ) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ambil user id dari share preferences
            val sharePref = requireContext().getSharedPreferences("UserSession", AppCompatActivity.MODE_PRIVATE)
            val userId = sharePref.getInt("user_id", -1)

            if (userId == -1) {
                Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ambil ID Kategori dari nama kategori
            val categoryId = databaseHelper.getOrInsertCategory(categoryName)

            if (categoryId == -1) {
                Toast.makeText(context, "Invalid category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // simpan gambar ke internal
            fun saveImageToInternalStorage(uri: Uri): String {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val fileName = "IMG_${System.currentTimeMillis()}.jpg"
                val file = File(requireContext().filesDir, fileName)

                inputStream?.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }

                return file.absolutePath
            }
            val imagePath = saveImageToInternalStorage(selectedImageUri!!) // simpan pathnya aja

            // simpan recipe ke database
            val result = databaseHelper.insertRecipe(name, description, ingredients, tools, steps, nutritionInfo, reference, imagePath, categoryId, userId)

            if (result != -1L) {
                Toast.makeText(requireContext(), "Recipe added!", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), RecipeActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "Failed to add recipe", Toast.LENGTH_SHORT).show()
            }
        }
        // end proses insert recipe

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddRecipeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddRecipeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}