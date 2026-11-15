package com.example.diet_management.ui.input

import com.example.diet_management.fake.FakeFoodRepository
import com.example.diet_management.fake.FakeMealRepository
import com.example.diet_management.fake.FakePhotoStorage
import com.example.diet_management.fake.TestDispatcherRule
import com.example.diet_management.model.Food
import com.example.diet_management.model.MealType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class MealInputViewModelTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val delta = 1e-9
    private val rice = Food("A", "쌀밥", "밥류", 100.0, 20.0, 5.0, 2.0)
    private val soup = Food("B", "미역국", "국 및 탕류", 200.0, 10.0, 10.0, 10.0)
    private val fixedNow = LocalDateTime.of(2026, 11, 16, 12, 30)

    private fun createViewModel(mealRepository: FakeMealRepository = FakeMealRepository()) =
        MealInputViewModel(
            mealRepository = mealRepository,
            foodRepository = FakeFoodRepository(listOf(rice, soup)),
            photoStorage = FakePhotoStorage(),
            now = { fixedNow },
        )

    @Test
    fun addFood_calculatesPreviewNutrition() = runTest {
        // Given
        val viewModel = createViewModel()

        // When: 쌀밥 200g + 미역국 50g
        viewModel.addFood(rice, "200")
        viewModel.addFood(soup, "50")

        // Then
        val preview = viewModel.uiState.value.preview
        assertEquals(300.0, preview.kcal, delta)
        assertEquals(45.0, preview.carb, delta)
        assertEquals(15.0, preview.protein, delta)
        assertEquals(9.0, preview.fat, delta)
    }

    @Test
    fun save_withoutPhoto_doesNotSaveAndShowsError() = runTest {
        // Given: 끼니와 음식은 있지만 사진이 없음
        val mealRepository = FakeMealRepository()
        val viewModel = createViewModel(mealRepository)
        viewModel.onMealTypeChange(MealType.LUNCH)
        viewModel.addFood(rice, "200")

        // When
        viewModel.save()

        // Then
        val state = viewModel.uiState.value
        assertTrue(mealRepository.meals.isEmpty())
        assertNull(state.savedMealId)
        assertNotNull(state.errorMessage)
    }
}
