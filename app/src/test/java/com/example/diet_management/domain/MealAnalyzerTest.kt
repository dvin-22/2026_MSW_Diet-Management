package com.example.diet_management.domain

import com.example.diet_management.model.Food
import com.example.diet_management.model.Meal
import com.example.diet_management.model.MealItem
import com.example.diet_management.model.MealType
import com.example.diet_management.model.Nutrition
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class MealAnalyzerTest {

    private val delta = 1e-9
    private val today = LocalDate.of(2026, 11, 15)

    // 테스트용 음식 2개 (100g 기준, 계산하기 쉬운 값)
    private val foodA = Food("A", "음식A", "밥류", 100.0, 20.0, 5.0, 2.0)
    private val foodB = Food("B", "음식B", "반찬류", 200.0, 10.0, 10.0, 10.0)
    private val foods = mapOf(foodA.id to foodA, foodB.id to foodB)

    // 11/15 조식: A 200g → 200kcal, 탄 40, 단 10, 지 4
    private val breakfastToday = meal("1", "2026-11-15T08:00", MealType.BREAKFAST, MealItem("A", 200.0))

    // 11/15 중식: A 100g + B 50g → 200kcal, 탄 25, 단 10, 지 7
    private val lunchToday = meal("2", "2026-11-15T12:30", MealType.LUNCH, MealItem("A", 100.0), MealItem("B", 50.0))

    // 11/14 석식: B 150g → 300kcal, 탄 15, 단 15, 지 15
    private val dinnerYesterday = meal("3", "2026-11-14T18:00", MealType.DINNER, MealItem("B", 150.0))

    // 11/13 중식: B 100g → 200kcal, 탄 10, 단 10, 지 10
    private val lunchTwoDaysAgo = meal("4", "2026-11-13T12:00", MealType.LUNCH, MealItem("B", 100.0))

    private fun meal(id: String, dateTime: String, type: MealType, vararg items: MealItem) =
        Meal(id = id, dateTime = dateTime, mealType = type, photoPath = "", items = items.toList())

    private fun assertNutrition(expected: Nutrition, actual: Nutrition?) {
        requireNotNull(actual)
        assertEquals(expected.kcal, actual.kcal, delta)
        assertEquals(expected.carb, actual.carb, delta)
        assertEquals(expected.protein, actual.protein, delta)
        assertEquals(expected.fat, actual.fat, delta)
    }

    @Test
    fun filterLast30Days_includesTodayAnd29DaysAgo() {
        // Given
        val todayMeal = meal("t", "2026-11-15T23:59", MealType.SNACK)
        val day29Meal = meal("d29", "2026-10-17T00:00", MealType.SNACK)

        // When
        val result = MealAnalyzer.filterLast30Days(listOf(todayMeal, day29Meal), today)

        // Then
        assertEquals(listOf("t", "d29"), result.map { it.id })
    }

    @Test
    fun filterLast30Days_excludes30DaysAgo() {
        // Given
        val day30Meal = meal("d30", "2026-10-16T23:59", MealType.SNACK)

        // When
        val result = MealAnalyzer.filterLast30Days(listOf(day30Meal), today)

        // Then
        assertEquals(emptyList<Meal>(), result)
    }

    @Test
    fun totalKcal_emptyList_returnsZero() {
        // Given
        val meals = emptyList<Meal>()

        // When
        val result = MealAnalyzer.totalKcal(meals, foods)

        // Then
        assertEquals(0.0, result, delta)
    }

    @Test
    fun nutritionByMealType_sumsEachType() {
        // Given
        val meals = listOf(breakfastToday, lunchToday, dinnerYesterday, lunchTwoDaysAgo)

        // When
        val result = MealAnalyzer.nutritionByMealType(meals, foods)

        // Then
        assertNutrition(Nutrition(200.0, 40.0, 10.0, 4.0), result[MealType.BREAKFAST])
        assertNutrition(Nutrition(400.0, 35.0, 20.0, 17.0), result[MealType.LUNCH])
        assertNutrition(Nutrition(300.0, 15.0, 15.0, 15.0), result[MealType.DINNER])
    }

    @Test
    fun nutritionByMealType_typeWithoutMeals_isZero() {
        // Given
        val meals = listOf(breakfastToday, lunchToday, dinnerYesterday)

        // When
        val result = MealAnalyzer.nutritionByMealType(meals, foods)

        // Then
        assertEquals(MealType.entries.size, result.size)
        assertNutrition(Nutrition(0.0, 0.0, 0.0, 0.0), result[MealType.SNACK])
    }

    @Test
    fun dailySummaries_countsMealsAndSumsKcalPerDate() {
        // Given
        val meals = listOf(breakfastToday, lunchToday, dinnerYesterday)

        // When
        val result = MealAnalyzer.dailySummaries(meals, foods)

        // Then
        val todaySummary = result.first { it.date == today }
        assertEquals(2, todaySummary.mealCount)
        assertEquals(400.0, todaySummary.totalKcal, delta)
        val yesterdaySummary = result.first { it.date == today.minusDays(1) }
        assertEquals(1, yesterdaySummary.mealCount)
        assertEquals(300.0, yesterdaySummary.totalKcal, delta)
    }

    @Test
    fun dailySummaries_sortedLatestDateFirst() {
        // Given: 일부러 날짜 순서를 섞어 둠
        val meals = listOf(lunchTwoDaysAgo, breakfastToday, dinnerYesterday)

        // When
        val result = MealAnalyzer.dailySummaries(meals, foods)

        // Then
        assertEquals(
            listOf(today, today.minusDays(1), today.minusDays(2)),
            result.map { it.date },
        )
    }
}
