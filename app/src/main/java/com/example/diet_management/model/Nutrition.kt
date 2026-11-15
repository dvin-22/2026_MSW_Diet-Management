package com.example.diet_management.model

/** 열량(kcal)과 탄수화물·단백질·지방(g). */
data class Nutrition(
    val kcal: Double,
    val carb: Double,
    val protein: Double,
    val fat: Double,
)
