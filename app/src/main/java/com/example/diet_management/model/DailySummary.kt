package com.example.diet_management.model

import java.time.LocalDate

/** 하루 요약: 날짜, 열량 합계, 끼니 수. */
data class DailySummary(
    val date: LocalDate,
    val totalKcal: Double,
    val mealCount: Int,
)
