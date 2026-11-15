package com.example.diet_management.ui.list

import com.example.diet_management.fake.FakeFoodRepository
import com.example.diet_management.fake.FakeMealRepository
import com.example.diet_management.fake.TestDispatcherRule
import com.example.diet_management.model.Food
import com.example.diet_management.model.Meal
import com.example.diet_management.model.MealItem
import com.example.diet_management.model.MealType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.time.LocalDate

class MealListViewModelTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val delta = 1e-9
    private val rice = Food("A", "쌀밥", "밥류", 100.0, 20.0, 5.0, 2.0)

    private fun meal(id: String, dateTime: String, type: MealType, grams: Double) =
        Meal(id, dateTime, type, photoPath = "", items = listOf(MealItem("A", grams)))

    @Test
    fun refresh_withMeals_groupsByDateInSuccess() = runTest {
        // Given: 11/16에 2끼, 11/15에 1끼
        val meals = listOf(
            meal("1", "2026-11-15T12:00", MealType.LUNCH, 100.0),
            meal("2", "2026-11-16T08:00", MealType.BREAKFAST, 200.0),
            meal("3", "2026-11-16T12:30", MealType.LUNCH, 300.0),
        )
        val viewModel = MealListViewModel(FakeMealRepository(meals), FakeFoodRepository(listOf(rice)))

        // When
        viewModel.refresh()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is MealListUiState.Success)
        val groups = (state as MealListUiState.Success).groups
        assertEquals(listOf(LocalDate.of(2026, 11, 16), LocalDate.of(2026, 11, 15)), groups.map { it.date })
        assertEquals(2, groups[0].mealCount)
        assertEquals(500.0, groups[0].totalKcal, delta)
        assertEquals(listOf("3", "2"), groups[0].meals.map { it.meal.id })
        assertEquals(1, groups[1].mealCount)
    }

    @Test
    fun refresh_whenRepositoryThrows_isError() = runTest {
        // Given
        val viewModel = MealListViewModel(
            FakeMealRepository(error = IOException("파일 읽기 실패")),
            FakeFoodRepository(listOf(rice)),
        )

        // When
        viewModel.refresh()

        // Then
        assertTrue(viewModel.uiState.value is MealListUiState.Error)
    }
}
