package com.example.diet_management.data

import com.example.diet_management.model.Food

/** 음식 영양 정보 저장소. ViewModel은 이 인터페이스에만 의존한다. */
interface FoodRepository {
    suspend fun getFoods(): List<Food>
}
