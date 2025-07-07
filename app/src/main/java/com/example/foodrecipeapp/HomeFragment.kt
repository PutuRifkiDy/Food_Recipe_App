package com.example.foodrecipeapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView

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
        val moreButton = view.findViewById<Button>(R.id.moreTechniques)
        val moreButton1 = view.findViewById<Button>(R.id.moreTechniques1)
        val cardRecipes = view.findViewById<CardView>(R.id.card_nasi_goreng)

        // Intent ke CookingTechniquesActivity
        moreButton.setOnClickListener {
            val intent = Intent(requireContext(), CookingTechniquesActivity::class.java)
            startActivity(intent)
        }

        // intent ke cooking techniques
        moreButton1.setOnClickListener {
            val intent = Intent(requireContext(), CookingTechniquesActivity::class.java)
            startActivity(intent)
        }

        // intent ke detail recipes
        cardRecipes.setOnClickListener{
            val intent = Intent(requireContext(), detailRecipeActivity::class.java)
            startActivity(intent)
        }

        // akses text view, mengambil user login dari share prefrences
        val sharedPref = requireContext().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val userName = sharedPref.getString("user_name", "Guest")

        // set text viewnya
        val greetingTextView = view.findViewById<TextView>(R.id.greetingName)
        greetingTextView.text = "Welcome, $userName!"

        // ambil remua recipe
        val dbHelper = DatabaseHelper(requireContext())
        val recipeList = dbHelper.getAllRecipe()
        val containerComponent = view.findViewById<LinearLayout>(R.id.container_recipe)

        for(recipe in recipeList){

            val cardView = layoutInflater.inflate(R.layout.home_recipe_card,null)
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