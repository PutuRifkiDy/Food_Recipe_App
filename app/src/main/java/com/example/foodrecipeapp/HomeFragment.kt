package com.example.foodrecipeapp

import android.content.Intent
import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import org.w3c.dom.Text

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Akses tombol More
//        val moreButton = view.findViewById<Button>(R.id.moreTechniques)
//        val moreButton1 = view.findViewById<Button>(R.id.moreTechniques1)
//        val cardRecipes = view.findViewById<LinearLayout>(R.id.card_nasi_goreng)

        // Intent ke CookingTechniquesActivity
//        moreButton.setOnClickListener {
//            val intent = Intent(requireContext(), CookingTechniquesActivity::class.java)
//            startActivity(intent)
//        }
//
//        // intent ke cooking techniques
//        moreButton1.setOnClickListener {
//            val intent = Intent(requireContext(), CookingTechniquesActivity::class.java)
//            startActivity(intent)
//        }

        // akses text view, mengambil user login dari share prefrences
        val sharedPref = requireContext().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val userName = sharedPref.getString("user_name", "Guest")

        // set text viewnya
        val greetingTextView = view.findViewById<TextView>(R.id.greetingName)
        greetingTextView.text = "Welcome, $userName!"

        // ambil semua category
        val dbHelper = DatabaseHelper(requireContext())
        val categoryList = dbHelper.getAllCategories()
        val containerCategory = view.findViewById<LinearLayout>(R.id.container_category)

        for(category in categoryList) {
            val cardViewCategory = layoutInflater.inflate(R.layout.item_category_home, null)
            val tvCategoryName = cardViewCategory.findViewById<TextView>(R.id.labelIconCategory)
            val ivCategory = cardViewCategory.findViewById<ImageView>(R.id.icon_path_category)

            tvCategoryName.text = category.category_name
            Glide.with(this)
                .load(category.iconPath)
                .into(ivCategory)

            containerCategory.addView(cardViewCategory)

            cardViewCategory.setOnClickListener{
                val intent = Intent(requireContext(), RecipeCategoryFilterActivity::class.java)
                intent.putExtra("category_id", category.id)
                startActivity(intent)
            }
        }

        // ambil remua recipe
        val recipeList = dbHelper.getAllRecipe()
        val containerComponent = view.findViewById<LinearLayout>(R.id.container_recipe)
        containerComponent.removeAllViews()

        for(recipe in recipeList){
            val cardView = layoutInflater.inflate(R.layout.home_recipe_card, containerComponent, false)
            val userTitle = dbHelper.getUserByRecipeUserId(recipe.userId)
            val categoryName = dbHelper.getCategoryNameById(recipe.categoryId)

            val tvUser = cardView.findViewById<TextView>(R.id.title_user)
            val ivRecipe = cardView.findViewById<ImageView>(R.id.iv_recipe_image)
            val category = cardView.findViewById<TextView>(R.id.title_category)
            val titleRecipe = cardView.findViewById<TextView>(R.id.recipe_name)

            tvUser.text = userTitle
            category.text = categoryName
            titleRecipe.text = recipe.name
            // tampilin image
            Glide.with(this)
                .load(recipe.imagePath)
                .into(ivRecipe)

            containerComponent.addView(cardView)

            cardView.setOnClickListener{
                val intent = Intent(requireContext(), detailRecipeActivity::class.java)
                intent.putExtra("recipe_id", recipe.id)
                startActivity(intent)
            }

        }

        // ambil semua cooking technique
        val cookingTechniqueList = dbHelper.getAllCookingTechnique()
        val containerCookingTechnique = view.findViewById<LinearLayout>(R.id.containerCookingTechnique)
        containerComponent.removeAllViews()

        for(cookingTechnique in cookingTechniqueList) {
            val cardView = layoutInflater.inflate(R.layout.item_cooking_technique_home, null)

            val tvTitleCookingTechnique =
                cardView.findViewById<TextView>(R.id.methodCookingTechnique)
            val tvDescriptionCookingTechnique =
                cardView.findViewById<TextView>(R.id.descriptionCookingTechnique)
            val image = cardView.findViewById<ImageView>(R.id.imageCookingTechnique)
            val btnMoreDetail = cardView.findViewById<Button>(R.id.btnMoreCookingTechnique)

            tvTitleCookingTechnique.text = cookingTechnique.title
            tvDescriptionCookingTechnique.text = cookingTechnique.description
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
            containerCookingTechnique.addView(cardView)

            btnMoreDetail.setOnClickListener {
                val intent = Intent(requireContext(), CookingTechniquesActivity::class.java)
                intent.putExtra("cooking_technique_id", cookingTechnique.id)
                startActivity(intent)
            }
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}