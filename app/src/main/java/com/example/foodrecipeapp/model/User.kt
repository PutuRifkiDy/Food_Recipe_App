package com.example.foodrecipeapp.model

data class User(
    val id: Int,
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    val about: String?,
    val gender: String?,
    val birthday: String?
)