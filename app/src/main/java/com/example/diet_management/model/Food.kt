package com.example.diet_management.model

import kotlinx.serialization.Serializable

/** foods.json 한 항목. 영양소 값은 모두 100g 기준이다. */
@Serializable
data class Food(
    val id: String,
    val name: String,
    val category: String,
    val kcalPer100g: Double,
    val carbPer100g: Double,
    val proteinPer100g: Double,
    val fatPer100g: Double,
)
