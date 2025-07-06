package com.example.foodrecipeapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.foodrecipeapp.model.Category
import com.example.foodrecipeapp.model.Recipe
import com.example.foodrecipeapp.model.User

class DatabaseHelper(private val context: Context):
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    // start declare constanta
    companion object {
        private const val DATABASE_NAME = "FoodRecipe.db"
        private const val DATABASE_VERSION = 2

        // table user
        private const val TABLE_USER = "user"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PHONE = "phone_number"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_ABOUT = "about"
        private const val COLUMN_GENDER = "gender"
        private const val COLUMN_BIRTHDAY = "birthday"

        // table category recipe
        private const val TABLE_CATEGORY = "category_recipe"
        private const val COLUMN_CATEGORY_ID = "id"
        private const val COLUMN_CATEGORY_NAME = "category_name"

        // table recipe
        private const val TABLE_RECIPE = "recipe"
        private const val COLUMN_RECIPE_ID = "id"
        private const val COLUMN_RECIPE_NAME = "recipe_name"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_INGREDIENTS = "ingredients"
        private const val COLUMN_TOOLS = "tools"
        private const val COLUMN_STEPS = "steps"
        private const val COLUMN_NUTRITION_INFO = "nutrition_info"
        private const val COLUMN_IMAGE_PATH = "image_path"
        private const val COLUMN_RECIPE_CATEGORY_ID = "category_id"
        private const val COLUMN_RECIPE_USER_ID = "user_id"
    }
    // end declare constanta

    // start create table
    override fun onCreate(db: SQLiteDatabase?) {
        // table user
        val createUserTable = """
            CREATE TABLE $TABLE_USER (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_PHONE TEXT,
                $COLUMN_EMAIL TEXT UNIQUE,
                $COLUMN_PASSWORD TEXT,
                $COLUMN_ABOUT TEXT,
                $COLUMN_GENDER TEXT,
                $COLUMN_BIRTHDAY TEXT
            )
        """.trimIndent()

        // table category
        val createCategoryTable = """
            CREATE TABLE $TABLE_CATEGORY (
                $COLUMN_CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CATEGORY_NAME TEXT
            )
        """.trimIndent()

        // table recipe
        val createRecipeTable = """
            CREATE TABLE $TABLE_RECIPE (
                $COLUMN_RECIPE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_RECIPE_NAME TEXT,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_INGREDIENTS TEXT,
                $COLUMN_TOOLS TEXT,
                $COLUMN_STEPS TEXT,
                $COLUMN_NUTRITION_INFO TEXT,
                $COLUMN_IMAGE_PATH TEXT,
                $COLUMN_RECIPE_CATEGORY_ID INTEGER,
                $COLUMN_RECIPE_USER_ID INTEGER,
                FOREIGN KEY ($COLUMN_RECIPE_CATEGORY_ID) REFERENCES $TABLE_CATEGORY($COLUMN_CATEGORY_ID),
                FOREIGN KEY ($COLUMN_RECIPE_USER_ID) REFERENCES $TABLE_USER($COLUMN_USER_ID)
            )
        """.trimIndent()

        db?.execSQL(createUserTable)
        db?.execSQL(createCategoryTable)
        db?.execSQL(createRecipeTable)
    }
    // end create table

    // start when db upgrade
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableUser = "DROP TABLE IF EXISTS $TABLE_USER"
        val dropTableCategory = "DROP TABLE IF EXISTS $TABLE_CATEGORY"
        val dropTableRecipe = "DROP TABLE IF EXISTS $TABLE_RECIPE"

        db?.execSQL(dropTableUser)
        db?.execSQL(dropTableCategory)
        db?.execSQL(dropTableRecipe)

        onCreate(db)
    }
    // end

    // start insert user for register
    fun insertUser(name: String, phone: String, email: String, password: String): Long {
        val userValues = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_PHONE, phone)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
        }

        val db = writableDatabase
        return db.insert(TABLE_USER, null, userValues)
    }
    // end insert user

    // start read user for login
    fun readUser(email: String, password: String): Boolean {
        val db = readableDatabase
        val selection = "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(email, password)
        val cursor = db.query(TABLE_USER, null, selection, selectionArgs, null, null, null)

        val userExists = cursor.count > 0
        cursor.close()
        return userExists
    }
    // end read user

    // cursor ke row user yang telah login
    fun getUserByEmail(email: String): String? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USER, // nama tabel yang ingin di cari datanya
            arrayOf(COLUMN_NAME), // kolom apa yang ingin diambil
            "$COLUMN_EMAIL = ?", // where emailny apa
            arrayOf(email), // argument untuk ganti tanya tanya di atas
            null,
            null,
            null
        )

        var name: String? = null

        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
        }

        cursor.close()
        return name
    }

    fun getUserDataByEmail(email: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USER,
            null,
            "$COLUMN_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                about = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ABOUT)),
                gender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)),
                birthday = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTHDAY))
            )
        }

        cursor.close()
        return user
    }

    // start insert recipe
    fun insertRecipe(
        name: String,
        description: String,
        ingredients: String,
        tools: String,
        steps: String,
        nutrition: String,
        imagePath: String,
        categoryId: Int,
        userId: Int
    ): Long {
        val values = ContentValues().apply {
            put(COLUMN_RECIPE_NAME, name)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_INGREDIENTS, ingredients)
            put(COLUMN_TOOLS, tools)
            put(COLUMN_STEPS, steps)
            put(COLUMN_NUTRITION_INFO, nutrition)
            put(COLUMN_IMAGE_PATH, imagePath)
            put(COLUMN_RECIPE_CATEGORY_ID, categoryId)
            put(COLUMN_RECIPE_USER_ID, userId)
        }

        val db = writableDatabase
        return db.insert(TABLE_RECIPE, null, values)
    }
    // end insert recipe

    // start getOrInsertCategory
    fun getOrInsertCategory(categoryName: String): Int {
        val db = writableDatabase
        var categoryId: Int? = null

        // apakah kategori sudah ada ?
        val cursor = db.query(
            TABLE_CATEGORY,
            arrayOf(COLUMN_CATEGORY_ID),
            "$COLUMN_CATEGORY_NAME = ?",
            arrayOf(categoryName),
            null, null, null
        )

        if(cursor.moveToFirst()) {
            categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID))
        } else {
            // insert kategori baru kalo kategori tersebut belum ada
            val values = ContentValues().apply {
                put(COLUMN_CATEGORY_NAME, categoryName)
            }

            val newId = db.insert(TABLE_CATEGORY, null, values)
            if (newId != -1L) {
                categoryId = newId.toInt()
            }
        }

        cursor.close()
        return categoryId ?: -1
    }
    // end getOrInsertCategory

    // start get recipes by user id
    fun getRecipesByUserId(userId: Int): List<Recipe> {
        val recipes = mutableListOf<Recipe>()
        val db = readableDatabase

        val cursor = db.query(
            TABLE_RECIPE,
            null,
            "$COLUMN_RECIPE_USER_ID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val recipe = Recipe(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    ingredients = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENTS)),
                    tools = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TOOLS)),
                    steps = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STEPS)),
                    nutritionInfo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NUTRITION_INFO)),
                    imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)),
                    categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_CATEGORY_ID)),
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_USER_ID))
                )
                recipes.add(recipe)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return recipes
    }
    // end recipe by user id

    // start get category name by id categorynya
    fun getCategoryNameById(categoryId: Int): String {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT category_name FROM category_recipe WHERE id = ?", arrayOf(categoryId.toString()))
        var name = "Uknown"

        if (cursor.moveToFirst()) {
            name = cursor.getString(0)
        }
        cursor.close()
        return name
    }
    // end get category by id categorynya

    // start count card berdasarkan id usernya
    fun countRecipeByUserId(userId: Int): Int {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM recipe WHERE user_id = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))
        var count = 0

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        db.close()
        return count
    }
    // end count card berdasarkan id usernya

    // start delete my recipe berdasarkan id recipe
    fun deleteRecipeById(recipeId: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_RECIPE, "id = ?", arrayOf(recipeId.toString()))
        db.close()
        return result > 0
    }
    // end delete my recipe berdasarkan id recipe

    // start getRecipe By id
    fun getRecipeById(id: Int): Recipe? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM recipe WHERE id = ?", arrayOf(id.toString()))

        var recipe: Recipe? = null
        if (cursor.moveToFirst()) {
            recipe = Recipe(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("recipe_name")),
                description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                ingredients = cursor.getString(cursor.getColumnIndexOrThrow("ingredients")),
                tools = cursor.getString(cursor.getColumnIndexOrThrow("tools")),
                steps = cursor.getString(cursor.getColumnIndexOrThrow("steps")),
                nutritionInfo = cursor.getString(cursor.getColumnIndexOrThrow("nutrition_info")),
                imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path")),
                categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("category_id")),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))
            )
        }

        cursor.close()
        db.close()

        return recipe
    }
    // end get recipe by id

    // start get all category
    fun getAllCategories(): List<Category> {
        val categoryList = mutableListOf<Category>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_CATEGORY"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val category = Category(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    category_name = cursor.getString(cursor.getColumnIndexOrThrow("category_name"))
                )
                categoryList.add(category)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return categoryList
    }
    // end get all category

}