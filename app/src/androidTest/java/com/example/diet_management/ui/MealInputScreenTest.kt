package com.example.diet_management.ui

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.example.diet_management.fake.FakeFoodRepository
import com.example.diet_management.fake.FakeMealRepository
import com.example.diet_management.fake.FakePhotoStorage
import com.example.diet_management.fake.TestData
import com.example.diet_management.ui.input.MealInputScreen
import com.example.diet_management.ui.input.MealInputTestTags
import com.example.diet_management.ui.input.MealInputViewModel
import com.example.diet_management.ui.theme.DietmanagementTheme
import org.junit.Rule
import org.junit.Test

class MealInputScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun addFood200g_showsCalculatedKcalInPreview() {
        // Given
        val viewModel = MealInputViewModel(
            FakeMealRepository(),
            FakeFoodRepository(TestData.foods),
            FakePhotoStorage(),
        )
        composeRule.setContent {
            DietmanagementTheme {
                MealInputScreen(viewModel = viewModel, onBack = {}, onSaved = {})
            }
        }

        // When: "쌀"로 검색 → 쌀밥 선택 → 200g 담기
        composeRule.onNode(hasSetTextAction() and hasText("음식 이름 검색"))
            .performScrollTo()
            .performTextInput("쌀")
        composeRule.onNodeWithText("쌀밥").performScrollTo().performClick()
        composeRule.onNode(hasSetTextAction() and hasAnyAncestor(isDialog()))
            .performTextInput("200")
        composeRule.onNodeWithText("담기").performClick()

        // Then: 166kcal × 200 / 100 = 332.0kcal
        composeRule.onNodeWithTag(MealInputTestTags.PREVIEW_KCAL)
            .performScrollTo()
            .assertTextEquals("332.0 kcal")
    }
}

