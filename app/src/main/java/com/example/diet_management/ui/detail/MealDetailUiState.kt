package com.example.diet_management.ui.detail

import com.example.diet_management.model.Meal
import com.example.diet_management.model.Nutrition

/** 먹은 음식 한 줄: 이름, 섭취량(g), 영양소. */
data class DetailFoodItem(
    val name: String,
    val grams: Double,
    val nutrition: Nutrition,
)

sealed interface MealDetailUiState {
    data object Loading : MealDetailUiState

    data class Success(
        val meal: Meal,
        val items: List<DetailFoodItem>,
        val total: Nutrition,
    ) : MealDetailUiState

    /** 없는 id이거나 불러오기에 실패했을 때. */
    data class Error(val message: String) : MealDetailUiState
}
