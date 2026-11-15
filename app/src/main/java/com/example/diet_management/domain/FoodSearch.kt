package com.example.diet_management.domain

import com.example.diet_management.model.Food

/** 이름에 [query]가 포함된 음식만 돌려준다. [query]가 비었거나 공백뿐이면 빈 목록이다. */
fun searchFoods(foods: List<Food>, query: String): List<Food> {
    val keyword = query.trim()
    if (keyword.isEmpty()) return emptyList()
    return foods.filter { it.name.contains(keyword) }
}
