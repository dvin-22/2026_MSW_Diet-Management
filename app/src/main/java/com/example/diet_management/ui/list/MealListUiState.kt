package com.example.diet_management.ui.list

import com.example.diet_management.model.Meal
import java.time.LocalDate

/** 식사 한 끼와 그 열량. */
data class MealWithKcal(
    val meal: Meal,
    val kcal: Double,
)

/** 하루치 식사 묶음: 날짜, 총 열량, 끼니 수, 그날의 식사 목록. */
data class MealDayGroup(
    val date: LocalDate,
    val totalKcal: Double,
    val mealCount: Int,
    val meals: List<MealWithKcal>,
)

sealed interface MealListUiState {
    data object Loading : MealListUiState

    /** [groups]는 최신 날짜가 앞에 온다. */
    data class Success(val groups: List<MealDayGroup>) : MealListUiState

    data class Error(val message: String) : MealListUiState
}
