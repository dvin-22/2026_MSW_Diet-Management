package com.example.diet_management.domain

import com.example.diet_management.model.Food
import org.junit.Assert.assertEquals
import org.junit.Test

class FoodSearchTest {

    private fun food(id: String, name: String) = Food(id, name, "기타", 0.0, 0.0, 0.0, 0.0)

    private val foods = listOf(
        food("1", "배추김치"),
        food("2", "김치찌개"),
        food("3", "된장찌개"),
        food("4", "쌀밥"),
    )

    @Test
    fun searchFoods_kimchi_returnsOnlyNamesContainingKimchi() {
        // Given
        val query = "김치"

        // When
        val result = searchFoods(foods, query)

        // Then
        assertEquals(listOf("배추김치", "김치찌개"), result.map { it.name })
    }

    @Test
    fun searchFoods_blankQuery_returnsEmptyList() {
        // Given
        val query = "   "

        // When
        val result = searchFoods(foods, query)

        // Then
        assertEquals(emptyList<Food>(), result)
    }
}
