package com.example.diet_management.fake

import com.example.diet_management.data.FoodRepository
import com.example.diet_management.model.Food

/** 미리 정한 [foods]를 그대로 돌려주는 테스트용 [FoodRepository]. */
class FakeFoodRepository(
    private val foods: List<Food> = emptyList(),
) : FoodRepository {
    override suspend fun getFoods(): List<Food> = foods
}
