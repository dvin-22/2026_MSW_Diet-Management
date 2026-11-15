package com.example.diet_management.data

import com.example.diet_management.model.Meal

/** 식사 기록 저장소. ViewModel은 이 인터페이스에만 의존한다. */
interface MealRepository {
    /** 저장된 식사 목록(최신순). 기록이 없으면 빈 목록. */
    suspend fun getMeals(): List<Meal>

    suspend fun addMeal(meal: Meal)

    suspend fun getMeal(id: String): Meal?
}
