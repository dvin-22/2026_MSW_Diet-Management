package com.example.diet_management.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import com.example.diet_management.fake.FakeFoodRepository
import com.example.diet_management.fake.FakeMealRepository
import com.example.diet_management.fake.TestData
import com.example.diet_management.ui.list.MealListScreen
import com.example.diet_management.ui.list.MealListViewModel
import com.example.diet_management.ui.theme.DietmanagementTheme
import org.junit.Rule
import org.junit.Test

class MealListScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun darkTheme_showsMealCardAndDateHeader() {
        // Given: 다크 테마로 목록 화면을 띄움
        val viewModel = MealListViewModel(
            FakeMealRepository(listOf(TestData.lunch)),
            FakeFoodRepository(TestData.foods),
        )
        composeRule.setContent {
            DietmanagementTheme(darkTheme = true) {
                MealListScreen(
                    viewModel = viewModel,
                    selectedMealId = null,
                    onMealClick = {},
                    onAddClick = {},
                )
            }
        }

        // When: 목록을 불러올 때까지 기다림
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("중식").fetchSemanticsNodes().isNotEmpty()
        }

        // Then: 날짜 헤더(쌀밥 210g 348.6 + 미역국 300g 36.0 = 384.6kcal)와 식사 카드
        composeRule.onNodeWithText("11월 15일 (일) · 384.6 kcal · 1끼").assertIsDisplayed()
        composeRule.onNodeWithText("중식").assertIsDisplayed()
        composeRule.onNodeWithText("오후 12:30").assertIsDisplayed()
    }
}
