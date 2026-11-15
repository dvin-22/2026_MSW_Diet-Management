package com.example.diet_management.ui.analysis

import com.example.diet_management.model.MacroRatio
import com.example.diet_management.model.MealType
import com.example.diet_management.model.Nutrition

/** 끼니 종류 하나의 30일 영양 합계와 탄·단·지 비율. */
data class MealTypeAnalysis(
    val mealType: MealType,
    val nutrition: Nutrition,
    val ratio: MacroRatio,
)

sealed interface AnalysisUiState {
    data object Loading : AnalysisUiState

    /** [byMealType]은 조식·중식·석식·간식/음료 순서로 4개가 항상 들어 있다. */
    data class Success(
        val totalKcal: Double,
        val mealCount: Int,
        val byMealType: List<MealTypeAnalysis>,
    ) : AnalysisUiState

    data class Error(val message: String) : AnalysisUiState
}
