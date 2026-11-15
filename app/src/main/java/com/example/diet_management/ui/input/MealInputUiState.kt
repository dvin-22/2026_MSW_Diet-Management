package com.example.diet_management.ui.input

import com.example.diet_management.model.Food
import com.example.diet_management.model.MealType
import com.example.diet_management.model.Nutrition
import java.time.LocalDate
import java.time.LocalTime

/** 식사에 담은 음식과 섭취량(g). */
data class SelectedFood(
    val food: Food,
    val grams: Double,
)

/** 식사 입력 화면의 전체 상태. */
data class MealInputUiState(
    val date: LocalDate,
    val time: LocalTime,
    val mealType: MealType? = null,
    val photoPath: String? = null,
    val items: List<SelectedFood> = emptyList(),
    val query: String = "",
    val searchResults: List<Food> = emptyList(),
    val preview: Nutrition = Nutrition(0.0, 0.0, 0.0, 0.0),
    /** 생성 시 한 번 불러온 전체 음식 목록. 검색은 이 목록을 거른다. */
    val allFoods: List<Food> = emptyList(),
    /** 사진 복사 실패, 저장 조건 불충족 등 사용자에게 보여 줄 이유. */
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    /** 저장이 끝나면 새 식사의 id. 화면은 이 값을 보고 목록으로 돌아간다. */
    val savedMealId: String? = null,
)
