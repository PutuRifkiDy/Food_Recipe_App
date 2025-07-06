package com.example.foodrecipeapp.model

data class Recipe(
    val id: Int,
    val name: String,
    val description: String,
    val ingredients: String,
    val tools: String,
    val steps: String,
    val nutritionInfo: String,
    val imagePath: String,
    val categoryId: Int,
    val userId: Int
)