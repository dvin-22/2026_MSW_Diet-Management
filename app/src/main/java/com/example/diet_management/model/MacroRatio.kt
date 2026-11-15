package com.example.diet_management.model

/** 탄수화물·단백질·지방이 열량에서 차지하는 비율(0.0~1.0). 셋의 합은 1이거나, 섭취가 없으면 모두 0이다. */
data class MacroRatio(
    val carb: Double,
    val protein: Double,
    val fat: Double,
)
