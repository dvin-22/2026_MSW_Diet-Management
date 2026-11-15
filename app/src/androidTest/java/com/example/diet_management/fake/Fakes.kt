package com.example.diet_management.fake

import com.example.diet_management.data.FoodRepository
import com.example.diet_management.data.MealRepository
import com.example.diet_management.data.PhotoStorage
import com.example.diet_management.model.Food
import com.example.diet_management.model.Meal

// UI 테스트용 Fake. src/test의 Fake는 androidTest에서 보이지 않아 따로 둔다.

class FakeFoodRepository(private val foods: List<Food>) : FoodRepository {
    override suspend fun getFoods(): List<Food> = foods
}

class FakeMealRepository(initialMeals: List<Meal> = emptyList()) : MealRepository {
    val meals = initialMeals.toMutableList()

    override suspend fun getMeals(): List<Meal> = meals.sortedByDescending { it.localDateTime() }

    override suspend fun addMeal(meal: Meal) {
        meals.add(meal)
    }

    override suspend fun getMeal(id: String): Meal? = meals.find { it.id == id }
}

class FakePhotoStorage : PhotoStorage {
    override suspend fun savePhoto(uri: String): String = "fake/photos/$uri"
}

/** 테스트 공통 데이터 */
object TestData {
    val rice = Food("A", "쌀밥", "밥류", 166.0, 37.33, 3.36, 0.32)
    val soup = Food("B", "미역국", "국 및 탕류", 12.0, 1.5, 0.8, 0.4)
    val foods = listOf(rice, soup)

    /** 2026-11-15 중식: 쌀밥 210g + 미역국 300g */
    val lunch = Meal(
        id = "meal-1",
        dateTime = "2026-11-15T12:30",
        mealType = com.example.diet_management.model.MealType.LUNCH,
        photoPath = "",
        items = listOf(
            com.example.diet_management.model.MealItem("A", 210.0),
            com.example.diet_management.model.MealItem("B", 300.0),
        ),
    )
}
