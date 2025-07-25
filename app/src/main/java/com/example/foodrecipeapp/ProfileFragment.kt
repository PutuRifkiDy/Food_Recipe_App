package com.example.foodrecipeapp

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // fungsi logout
        fun logoutUser() {
            val sharedPref = requireContext().getSharedPreferences("UserSession", MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.clear()
            editor.remove("is_guest")
            editor.apply()

            Toast.makeText(requireContext(), "Logout Successfull", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), LandingPageActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        // akses semua button
        val editProfile = view.findViewById<LinearLayout>(R.id.btnEditProfile)
        val logOut = view.findViewById<LinearLayout>(R.id.btnLogout)
        val myRecipe = view.findViewById<LinearLayout>(R.id.btnMyRecipe)
        val btnBack = view.findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener{
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        logOut.setOnClickListener{
            logoutUser()
        }

        myRecipe.setOnClickListener{
            val intent = Intent(requireContext(), RecipeActivity::class.java)
            startActivity(intent)
        }

        editProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        // ambil preferences user yang login
        // akses text view, mengambil user login dari share prefrences
        val sharedPref = requireContext().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val userName = sharedPref.getString("user_name", "Guest")
        val userPhoneNumber = sharedPref.getString("user_phone", "-")
        val userGender = sharedPref.getString("user_gender", "-")
        val about = sharedPref.getString("user_about", "-")
        val userEmail = sharedPref.getString("user_email", "-")
        val userBirthday = sharedPref.getString("user_birthday", "-")

        // untuk cek apakah orang ini guest
        val isGuest = sharedPref.getBoolean("is_guest", false)
        if (isGuest) {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
            return null
        }

        // set text viewnya
        val nameTextView = view.findViewById<TextView>(R.id.tvUsername)
        val aboutTextView = view.findViewById<TextView>(R.id.tvAboutDesc)
        val phoneTextView = view.findViewById<TextView>(R.id.tvPhoneNumberDesc)
        val genderTextView = view.findViewById<TextView>(R.id.tvGenderDesc)
        val emailTextView = view.findViewById<TextView>(R.id.tvEmailDesc)
        val birthTextView = view.findViewById<TextView>(R.id.tvBirthDesc)
        nameTextView.text = userName ?: "Guest"
        aboutTextView.text = about ?: "-"
        phoneTextView.text = userPhoneNumber ?: "-"
        genderTextView.text = userGender ?: "-"
        emailTextView.text = userEmail ?: "-"
        birthTextView.text = userBirthday ?: "-"

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}