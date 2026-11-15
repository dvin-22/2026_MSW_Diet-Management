package com.example.diet_management.data

import android.content.Context
import com.example.diet_management.model.Food
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/** `assets/foods.json`에서 음식 목록을 읽는 [FoodRepository] 구현. */
class AssetFoodRepository(context: Context) : FoodRepository {

    private val appContext = context.applicationContext

    override suspend fun getFoods(): List<Food> = withContext(Dispatchers.IO) {
        val text = appContext.assets.open(FILE_NAME).bufferedReader().use { it.readText() }
        Json.decodeFromString<List<Food>>(text)
    }

    private companion object {
        const val FILE_NAME = "foods.json"
    }
}
