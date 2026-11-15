package com.example.diet_management.fake

import com.example.diet_management.data.MealRepository
import com.example.diet_management.model.Meal

/**
 * 파일 대신 메모리 목록에 식사를 보관하는 테스트용 [MealRepository].
 * @param error null이 아니면 모든 함수가 이 예외를 던진다(오류 상태 테스트용).
 */
class FakeMealRepository(
    initialMeals: List<Meal> = emptyList(),
    private val error: Exception? = null,
) : MealRepository {

    /** 테스트에서 저장 결과를 확인할 수 있게 공개한다. */
    val meals = initialMeals.toMutableList()

    override suspend fun getMeals(): List<Meal> {
        error?.let { throw it }
        return meals.sortedByDescending { it.localDateTime() }
    }

    override suspend fun addMeal(meal: Meal) {
        error?.let { throw it }
        meals.add(meal)
    }

    override suspend fun getMeal(id: String): Meal? {
        error?.let { throw it }
        return meals.find { it.id == id }
    }
}
