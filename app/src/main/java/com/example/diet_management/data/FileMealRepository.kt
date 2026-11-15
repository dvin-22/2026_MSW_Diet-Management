package com.example.diet_management.data

import android.content.Context
import com.example.diet_management.model.Meal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

/** `filesDir/meals.json`에 식사 전체 목록을 JSON으로 저장하는 [MealRepository] 구현. */
class FileMealRepository(context: Context) : MealRepository {

    private val file = File(context.applicationContext.filesDir, FILE_NAME)

    override suspend fun getMeals(): List<Meal> =
        readAll().sortedByDescending { it.localDateTime() }

    override suspend fun addMeal(meal: Meal) = withContext(Dispatchers.IO) {
        val meals = readAll() + meal
        file.writeText(Json.encodeToString(meals))
    }

    override suspend fun getMeal(id: String): Meal? =
        readAll().find { it.id == id }

    private suspend fun readAll(): List<Meal> = withContext(Dispatchers.IO) {
        if (!file.exists()) {
            emptyList()
        } else {
            Json.decodeFromString<List<Meal>>(file.readText())
        }
    }

    private companion object {
        const val FILE_NAME = "meals.json"
    }
}
