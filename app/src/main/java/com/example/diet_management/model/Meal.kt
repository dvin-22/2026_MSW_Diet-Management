package com.example.diet_management.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

/** 식사에 포함된 음식 하나와 섭취량(g). */
@Serializable
data class MealItem(
    val foodId: String,
    val grams: Double,
)

/** 한 끼 식사. [dateTime]은 ISO 형식 문자열(예: "2026-11-15T12:30")로 저장한다. */
@Serializable
data class Meal(
    val id: String,
    val dateTime: String,
    val mealType: MealType,
    val photoPath: String,
    val items: List<MealItem>,
) {
    /** 저장된 [dateTime] 문자열을 [LocalDateTime]으로 변환한다. */
    fun localDateTime(): LocalDateTime = LocalDateTime.parse(dateTime)
}
