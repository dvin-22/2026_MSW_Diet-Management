package com.example.diet_management.domain

import com.example.diet_management.model.Food
import com.example.diet_management.model.MacroRatio
import com.example.diet_management.model.Nutrition
import java.util.Locale

/** 영양소 계산용 순수 함수 모음. Android 클래스에 의존하지 않는다. */
object NutritionCalculator {

    /** 100g당 값 × [grams] / 100으로 영양소를 계산한다. [grams]가 0 이하면 모두 0이다. */
    fun calculate(food: Food, grams: Double): Nutrition {
        if (grams <= 0) return Nutrition(0.0, 0.0, 0.0, 0.0)
        val ratio = grams / 100
        return Nutrition(
            kcal = food.kcalPer100g * ratio,
            carb = food.carbPer100g * ratio,
            protein = food.proteinPer100g * ratio,
            fat = food.fatPer100g * ratio,
        )
    }

    /** 영양소 목록의 합계. 빈 목록이면 모두 0이다. */
    fun sum(list: List<Nutrition>): Nutrition = Nutrition(
        kcal = list.sumOf { it.kcal },
        carb = list.sumOf { it.carb },
        protein = list.sumOf { it.protein },
        fat = list.sumOf { it.fat },
    )

    /**
     * 탄·단·지 열량 비율. 1g당 탄수화물 4kcal, 단백질 4kcal, 지방 9kcal로 환산해 나눈다.
     * 세 영양소가 모두 0이면 비율도 모두 0이다.
     */
    fun macroRatio(nutrition: Nutrition): MacroRatio {
        val carbKcal = nutrition.carb * 4
        val proteinKcal = nutrition.protein * 4
        val fatKcal = nutrition.fat * 9
        val total = carbKcal + proteinKcal + fatKcal
        if (total <= 0) return MacroRatio(0.0, 0.0, 0.0)
        return MacroRatio(carbKcal / total, proteinKcal / total, fatKcal / total)
    }

    /** 표시용 문자열. 소수점 1자리 반올림, 천 단위 쉼표 (예: 1850.25 → "1,850.3"). */
    fun formatOneDecimal(value: Double): String =
        String.format(Locale.US, "%,.1f", value)
}
