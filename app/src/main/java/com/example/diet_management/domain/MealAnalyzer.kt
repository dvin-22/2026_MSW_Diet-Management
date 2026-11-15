package com.example.diet_management.domain

import com.example.diet_management.model.DailySummary
import com.example.diet_management.model.Food
import com.example.diet_management.model.Meal
import com.example.diet_management.model.MealType
import com.example.diet_management.model.Nutrition
import java.time.LocalDate

/** 식사 분석용 순수 함수 모음. 음식 정보는 식품코드 → [Food] 맵으로 받는다. */
object MealAnalyzer {

    /** 식사 한 끼의 영양 합계. [foods]에 없는 식품코드는 계산에서 뺀다. */
    fun mealNutrition(meal: Meal, foods: Map<String, Food>): Nutrition =
        NutritionCalculator.sum(
            meal.items.mapNotNull { item ->
                foods[item.foodId]?.let { NutritionCalculator.calculate(it, item.grams) }
            }
        )

    /** [today]를 포함한 최근 30일(today-29일 ~ today) 식사만 남긴다. */
    fun filterLast30Days(meals: List<Meal>, today: LocalDate): List<Meal> {
        val start = today.minusDays(29)
        return meals.filter {
            val date = it.localDateTime().toLocalDate()
            !date.isBefore(start) && !date.isAfter(today)
        }
    }

    /** 모든 식사의 열량 총량. */
    fun totalKcal(meals: List<Meal>, foods: Map<String, Food>): Double =
        meals.sumOf { mealNutrition(it, foods).kcal }

    /** [MealType]별 영양 합계. 기록이 없는 끼니도 0으로 포함한다. */
    fun nutritionByMealType(meals: List<Meal>, foods: Map<String, Food>): Map<MealType, Nutrition> =
        MealType.entries.associateWith { type ->
            NutritionCalculator.sum(
                meals.filter { it.mealType == type }.map { mealNutrition(it, foods) }
            )
        }

    /** 날짜별 열량 합계와 끼니 수. 최신 날짜가 앞에 온다. */
    fun dailySummaries(meals: List<Meal>, foods: Map<String, Food>): List<DailySummary> =
        meals.groupBy { it.localDateTime().toLocalDate() }
            .map { (date, dayMeals) ->
                DailySummary(
                    date = date,
                    totalKcal = totalKcal(dayMeals, foods),
                    mealCount = dayMeals.size,
                )
            }
            .sortedByDescending { it.date }
}
