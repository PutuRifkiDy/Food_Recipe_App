package com.example.foodrecipeapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.foodrecipeapp.databinding.ActivityAdminUserManagemenBinding
import com.example.foodrecipeapp.model.CookingTechnique
import com.example.foodrecipeapp.model.User

class AdminUserManagemenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminUserManagemenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminUserManagemenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backButtonManageUser.setOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent)
        }

        binding.createNewManageUser.setOnClickListener {
            showUserDialog(null)
        }

        val dbHelper = DatabaseHelper(this)
        val userList = dbHelper.getAllUser()
        val container = binding.containerManageUser

        for (user in userList) {
            val cardView = layoutInflater.inflate(R.layout.item_user_card, null)
            val tvUserName = cardView.findViewById<TextView>(R.id.tvNameUser)
            val tvAbout = cardView.findViewById<TextView>(R.id.tvDescriptionUser)

            tvUserName.text = user.name
            tvAbout.text = user.about

            // kasi gap
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.bottomMargin = 16

            // menampilkan cardnya semua
            cardView.layoutParams = layoutParams
            container.addView(cardView)



            val deleteBtn  = cardView.findViewById<Button>(R.id.btnDeleteUser)

            if (user.isAdmin == true) {
                deleteBtn.visibility = View.GONE
            } else {
                deleteBtn.setOnClickListener{
                    AlertDialog.Builder(this)
                        .setTitle("Delete User")
                        .setMessage("Are you sure you want to delete this user?")
                        .setPositiveButton("Yes") { _, _ ->
                            val success = dbHelper.deleteUserById(user.id)
                            if (success) {
                                Toast.makeText(this, "User Deleted", Toast.LENGTH_SHORT).show()
                                recreate()
                            } else {
                                Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .setNegativeButton("No", null)
                        .show()
                }
            }
        }
    }

    private fun showUserDialog(user: User?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_user_admin, null)
        val inputUserName = dialogView.findViewById<EditText>(R.id.inputUserName)
        val inputPhoneNumber = dialogView.findViewById<EditText>(R.id.inputPhoneNumber)
        val inputEmail = dialogView.findViewById<EditText>(R.id.inputEmail)
        val inputPassword = dialogView.findViewById<EditText>(R.id.inputPassword)
        val inputAbout = dialogView.findViewById<EditText>(R.id.inputAbout)
        val inputGender = dialogView.findViewById<EditText>(R.id.inputGender)
        val inputBirthday = dialogView.findViewById<EditText>(R.id.inputBirthday)
        val dbHelper = DatabaseHelper(this)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Add New User")
            .setPositiveButton("Save") { _, _ ->
                val name = inputUserName.text.toString().trim()
                val phone = inputPhoneNumber.text.toString().trim()
                val email = inputEmail.text.toString().trim()
                val password = inputPassword.text.toString().trim()
                val about = inputAbout.text.toString().trim()
                val gender = inputGender.text.toString().trim()
                val birthday = inputBirthday.text.toString().trim()


                if (name.isEmpty() ||
                    phone.isEmpty() ||
                    email.isEmpty() ||
                    about.isEmpty() ||
                    gender.isEmpty() ||
                    birthday.isEmpty()
                ) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val result = dbHelper.insertUserInAdmin(name, phone,  email, password, about, gender, birthday)
                if (result != -1L) {
                    Toast.makeText(this, "User added!", Toast.LENGTH_SHORT).show()
                    recreate()
                } else {
                    Toast.makeText(this, "Failed to add user", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}