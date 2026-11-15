package com.example.diet_management.model

import kotlinx.serialization.Serializable

/** 끼니 종류. [label]은 화면에 표시할 한글 이름이다. */
@Serializable
enum class MealType(val label: String) {
    BREAKFAST("조식"),
    LUNCH("중식"),
    DINNER("석식"),
    SNACK("간식/음료"),
}
