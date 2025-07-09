package com.example.foodrecipeapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import com.example.foodrecipeapp.model.Category
import com.example.foodrecipeapp.model.CookingTechnique
import com.example.foodrecipeapp.model.Recipe
import com.example.foodrecipeapp.model.User
import java.lang.ref.Reference

class DatabaseHelper(private val context: Context):
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    // start declare constanta
    companion object {
        private const val DATABASE_NAME = "FoodRecipe.db"
        private const val DATABASE_VERSION = 9

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
        private const val IS_ADMIN = "is_admin"

        // table category recipe
        private const val TABLE_CATEGORY = "category_recipe"
        private const val COLUMN_CATEGORY_ID = "id"
        private const val COLUMN_CATEGORY_NAME = "category_name"
        private const val COLUMN_ICON_PATH = "icon_path"

        // table cooking techniques
        private const val TABLE_COOKING_TECHNIQUE = "cooking_technique"
        private const val COLUMN_COOKING_TECHNIQUE_ID = "id"
        private const val COLUMN_COOKING_TECHNIQUE_TITLE = "title"
        private const val COLUMN_COOKING_TECHNIQUE_IMAGE_PATH = "image_path"
        private const val COLUMN_COOKING_TECHNIQUE_DESCRIPTION = "description"
        private const val COLUMN_COOKING_TECHNIQUE_METHOD = "method"

        // table recipe
        private const val TABLE_RECIPE = "recipe"
        private const val COLUMN_RECIPE_ID = "id"
        private const val COLUMN_RECIPE_NAME = "recipe_name"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_INGREDIENTS = "ingredients"
        private const val COLUMN_TOOLS = "tools"
        private const val COLUMN_STEPS = "steps"
        private const val COLUMN_NUTRITION_INFO = "nutrition_info"
        private const val COLUMN_REFERENCES = "reference"
        private const val COLUMN_IMAGE_PATH = "image_path"
        private const val COLUMN_RECIPE_CATEGORY_ID = "category_id"
        private const val COLUMN_RECIPE_USER_ID = "user_id"
    }
    // end declare constanta

    override fun onConfigure(db: SQLiteDatabase) {
        // Aktifkan foreign key constraint di SQLite Android
        db.setForeignKeyConstraintsEnabled(true)
    }

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
                $COLUMN_BIRTHDAY TEXT,
                $IS_ADMIN INTEGER DEFAULT 0
            )
        """.trimIndent()

        // table category
        val createCategoryTable = """
            CREATE TABLE $TABLE_CATEGORY (
                $COLUMN_CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CATEGORY_NAME TEXT,
                $COLUMN_ICON_PATH TEXT
            )
        """.trimIndent()

        // table cooking technique
        val createCookingTechnique = """
            CREATE TABLE $TABLE_COOKING_TECHNIQUE (
                $COLUMN_COOKING_TECHNIQUE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_COOKING_TECHNIQUE_TITLE TEXT,
                $COLUMN_COOKING_TECHNIQUE_IMAGE_PATH TEXT,
                $COLUMN_COOKING_TECHNIQUE_DESCRIPTION TEXT,
                $COLUMN_COOKING_TECHNIQUE_METHOD TEXT
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
                $COLUMN_REFERENCES TEXT,
                $COLUMN_IMAGE_PATH TEXT,
                $COLUMN_RECIPE_CATEGORY_ID INTEGER,
                $COLUMN_RECIPE_USER_ID INTEGER,
                FOREIGN KEY ($COLUMN_RECIPE_CATEGORY_ID) REFERENCES $TABLE_CATEGORY($COLUMN_CATEGORY_ID) ON DELETE CASCADE,
                FOREIGN KEY ($COLUMN_RECIPE_USER_ID) REFERENCES $TABLE_USER($COLUMN_USER_ID) ON DELETE CASCADE
            )
        """.trimIndent()

        val insertAdmin = """
            INSERT INTO $TABLE_USER (
                $COLUMN_NAME, $COLUMN_PHONE, $COLUMN_EMAIL, $COLUMN_PASSWORD,
                $COLUMN_ABOUT, $COLUMN_GENDER, $COLUMN_BIRTHDAY, $IS_ADMIN
            ) VALUES (
                'Admin', '081234567890', 'admin@gmail.com', 'admin123',
                'Default admin account', 'Other', '11-07-2005', 1
            )
        """.trimIndent()

        db?.execSQL(createUserTable)
        db?.execSQL(createCategoryTable)
        db?.execSQL(createCookingTechnique)
        db?.execSQL(createRecipeTable)
        db?.execSQL(insertAdmin)
    }
    // end create table

    // start when db upgrade
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableUser = "DROP TABLE IF EXISTS $TABLE_USER"
        val dropTableCategory = "DROP TABLE IF EXISTS $TABLE_CATEGORY"
        val dropTableRecipe = "DROP TABLE IF EXISTS $TABLE_RECIPE"

        db?.execSQL(dropTableRecipe)
        db?.execSQL(dropTableCategory)
        db?.execSQL(dropTableUser)

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
                birthday = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTHDAY)),
                isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow("is_admin")) == 1
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
        reference: String,
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
            put(COLUMN_REFERENCES, reference)
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
                    reference = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REFERENCES)),
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
                reference = cursor.getString(cursor.getColumnIndexOrThrow("reference")),
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
                    category_name = cursor.getString(cursor.getColumnIndexOrThrow("category_name")),
                    iconPath = cursor.getString(cursor.getColumnIndexOrThrow("icon_path"))
                )
                categoryList.add(category)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return categoryList
    }
    // end get all category

    // start get all recipe
    fun getAllRecipe(): List<Recipe> {
        val recipeList = mutableListOf<Recipe>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_RECIPE"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val recipe = Recipe(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("recipe_name")),
                    description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    ingredients = cursor.getString(cursor.getColumnIndexOrThrow("ingredients")),
                    tools = cursor.getString(cursor.getColumnIndexOrThrow("tools")),
                    steps = cursor.getString(cursor.getColumnIndexOrThrow("steps")),
                    nutritionInfo = cursor.getString(cursor.getColumnIndexOrThrow("nutrition_info")),
                    reference = cursor.getString(cursor.getColumnIndexOrThrow("reference")),
                    imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path")),
                    categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("category_id")),
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))
                )
                recipeList.add(recipe)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return recipeList
    }
    // end get all recipe

    // get user name by id
    fun getUserByRecipeUserId(recipeUserId: Int): String {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_NAME FROM $TABLE_USER WHERE id = ?", arrayOf(recipeUserId.toString()))
        var name = "Uknown"

        if (cursor.moveToFirst()) {
            name = cursor.getString(0)
        }
        cursor.close()
        return name
    }
    // end get user name by id

    // start update recipe
    fun updateRecipe(
        id: Int,
        name: String,
        description: String,
        ingredients: String,
        tools: String,
        steps: String,
        nutritionInfo: String,
        reference: String,
        imagePath: String,
        categoryId: Int
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("$COLUMN_RECIPE_NAME", name)
            put("$COLUMN_DESCRIPTION", description)
            put("$COLUMN_INGREDIENTS", ingredients)
            put("$COLUMN_TOOLS", tools)
            put("$COLUMN_STEPS", steps)
            put("$COLUMN_NUTRITION_INFO", nutritionInfo)
            put("$COLUMN_REFERENCES", reference)
            put("$COLUMN_IMAGE_PATH", imagePath)
            put("$COLUMN_RECIPE_CATEGORY_ID", categoryId)
        }

        val result = db.update("$TABLE_RECIPE", values, "id = ?", arrayOf(id.toString()))
        return result > 0
    }
    // end update recipe

    // start insert category
    fun insertCategory(name: String, iconPath: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CATEGORY_NAME, name)
            put(COLUMN_ICON_PATH, iconPath)
        }

        val result = db.insert(TABLE_CATEGORY, null, values)
        return result
    }
    // end insert category

    // start delete category berdasarkan id category
    fun deleteCategoryById(categoryId: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_CATEGORY, "id = ?", arrayOf(categoryId.toString()))
        db.close()
        return result > 0
    }
    // end delete category berdasarkan id category

    // start update category
    fun updateCategory(category: Category): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("category_name", category.category_name)
            put("icon_path", category.iconPath)
        }
        val result = db.update("category_recipe", values, "id = ?", arrayOf(category.id.toString()))
        db.close()
        return result > 0
    }
    // end update category

    // start getRecipeByCategoryId
    fun getRecipeByCategoryId(categoryId: Int): List<Recipe> {
        val recipes = mutableListOf<Recipe>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_RECIPE,
            null,
            "$COLUMN_RECIPE_CATEGORY_ID = ?",
            arrayOf(categoryId.toString()),
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
                    reference = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REFERENCES)),
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
    // end get recipe by category id

    // start insert cooking technique
    fun insertCookingTechnique(title: String,imagePath: String, description: String, method: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_COOKING_TECHNIQUE_TITLE, title)
            put(COLUMN_COOKING_TECHNIQUE_DESCRIPTION, description)
            put(COLUMN_COOKING_TECHNIQUE_METHOD, method)
            put(COLUMN_COOKING_TECHNIQUE_IMAGE_PATH, imagePath)
        }

        val result = db.insert(TABLE_COOKING_TECHNIQUE, null, values)
        return result
    }
    // end insert cooking technique

    // start delete Cooking technique By Id
    fun deleteCookingTechnique(cookingTechniqueId: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_COOKING_TECHNIQUE, "id = ?", arrayOf(cookingTechniqueId.toString()))
        db.close()
        return result > 0
    }
    // end delete cooking technique by id

    // start update cooking technique By id
    fun updateCookingTechnique(cookingTechnique: CookingTechnique): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_COOKING_TECHNIQUE_TITLE, cookingTechnique.title)
            put(COLUMN_COOKING_TECHNIQUE_DESCRIPTION, cookingTechnique.description)
            put(COLUMN_COOKING_TECHNIQUE_METHOD, cookingTechnique.method)
            put(COLUMN_COOKING_TECHNIQUE_IMAGE_PATH, cookingTechnique.imagePath)
        }

        val result = db.update(TABLE_COOKING_TECHNIQUE, values, "id = ?", arrayOf(cookingTechnique.id.toString()))
        db.close()
        return result > 0
    }
    // end update cooking technique By id

    // start get all cooking technique
    fun getAllCookingTechnique(): List<CookingTechnique> {
        val cookingTechniqueList = mutableListOf<CookingTechnique>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_COOKING_TECHNIQUE"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val cookingTechnique = CookingTechnique(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    method = cursor.getString(cursor.getColumnIndexOrThrow("method")),
                    imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"))
                )
                cookingTechniqueList.add(cookingTechnique)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return cookingTechniqueList
    }
    // start get detail cooking technique berdasarkan id
    fun getCookingTechniqueById(id: Int): CookingTechnique? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_COOKING_TECHNIQUE WHERE id = ?", arrayOf(id.toString()))

        var cookingTechniq: CookingTechnique? = null

        if (cursor.moveToFirst()) {
            cookingTechniq = CookingTechnique(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COOKING_TECHNIQUE_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COOKING_TECHNIQUE_TITLE)),
                imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COOKING_TECHNIQUE_IMAGE_PATH)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COOKING_TECHNIQUE_DESCRIPTION)),
                method = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COOKING_TECHNIQUE_METHOD))
            )
        }

        cursor.close()
        db.close()

        return cookingTechniq
    }
    // end get detail cooking technique berdasarkan
//    fun countRecipeByUserId(userId: Int): Int {
//        val db = readableDatabase
//        val query = "SELECT COUNT(*) FROM recipe WHERE user_id = ?"
//        val cursor = db.rawQuery(query, arrayOf(userId.toString()))
//        var count = 0
//
//        if (cursor.moveToFirst()) {
//            count = cursor.getInt(0)
//        }
//
//        cursor.close()
//        db.close()
//        return count
//    }
    // start count category
    fun countCategory(): Int {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_CATEGORY"
        val cursor = db.rawQuery(query, null)
        var count_category = 0

        if (cursor.moveToFirst()) {
            count_category = cursor.getInt(0)
        }

        cursor.close()
        return count_category
    }
    // end count category

    // start count cooking technique
    fun countCookingTechnique(): Int {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_COOKING_TECHNIQUE"
        val cursor = db.rawQuery(query, null)

        var count_cookingTechnique = 0

        if (cursor.moveToFirst()) {
            count_cookingTechnique = cursor.getInt(0)
        }

        cursor.close()
        return count_cookingTechnique
    }
    // end count cooking technique

    // start count user
    fun countUser(): Int {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_USER"
        val cursor = db.rawQuery(query, null)

        var count_user = 0

        if (cursor.moveToFirst()) {
            count_user = cursor.getInt(0)
        }

        cursor.close()
        return count_user
    }
    // end count user

}