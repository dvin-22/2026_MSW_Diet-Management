package com.example.diet_management.domain

import com.example.diet_management.model.Food
import com.example.diet_management.model.Nutrition
import org.junit.Assert.assertEquals
import org.junit.Test

class NutritionCalculatorTest {

    private val delta = 1e-9

    private val rice = Food(
        id = "D301-022000000-0001",
        name = "쌀밥",
        category = "밥류",
        kcalPer100g = 166.0,
        carbPer100g = 37.33,
        proteinPer100g = 3.36,
        fatPer100g = 0.32,
    )

    private fun assertNutrition(expected: Nutrition, actual: Nutrition) {
        assertEquals(expected.kcal, actual.kcal, delta)
        assertEquals(expected.carb, actual.carb, delta)
        assertEquals(expected.protein, actual.protein, delta)
        assertEquals(expected.fat, actual.fat, delta)
    }

    @Test
    fun calculate_100g_equalsDbValues() {
        // Given
        val grams = 100.0

        // When
        val result = NutritionCalculator.calculate(rice, grams)

        // Then
        assertNutrition(Nutrition(166.0, 37.33, 3.36, 0.32), result)
    }

    @Test
    fun calculate_250g_isTwoAndHalfTimes() {
        // Given
        val grams = 250.0

        // When
        val result = NutritionCalculator.calculate(rice, grams)

        // Then
        assertNutrition(Nutrition(415.0, 93.325, 8.4, 0.8), result)
    }

    @Test
    fun calculate_0g_returnsZero() {
        // Given
        val grams = 0.0

        // When
        val result = NutritionCalculator.calculate(rice, grams)

        // Then
        assertNutrition(Nutrition(0.0, 0.0, 0.0, 0.0), result)
    }

    @Test
    fun sum_multipleFoods_addsEachField() {
        // Given
        val list = listOf(
            Nutrition(kcal = 166.0, carb = 37.33, protein = 3.36, fat = 0.32),
            Nutrition(kcal = 12.0, carb = 1.5, protein = 0.8, fat = 0.4),
            Nutrition(kcal = 280.0, carb = 20.0, protein = 15.0, fat = 16.0),
        )

        // When
        val result = NutritionCalculator.sum(list)

        // Then
        assertNutrition(Nutrition(458.0, 58.83, 19.16, 16.72), result)
    }

    @Test
    fun formatOneDecimal_roundsDown_whenSecondDecimalBelowFive() {
        // Given
        val value = 12.345

        // When
        val result = NutritionCalculator.formatOneDecimal(value)

        // Then
        assertEquals("12.3", result)
    }

    @Test
    fun formatOneDecimal_roundsUp_whenSecondDecimalIsFive() {
        // Given
        val value = 12.35

        // When
        val result = NutritionCalculator.formatOneDecimal(value)

        // Then
        assertEquals("12.4", result)
    }
}
