package com.example.diet_management.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import com.example.diet_management.fake.FakeFoodRepository
import com.example.diet_management.fake.FakeMealRepository
import com.example.diet_management.fake.TestData
import com.example.diet_management.ui.analysis.AnalysisScreen
import com.example.diet_management.ui.analysis.AnalysisTestTags
import com.example.diet_management.ui.analysis.AnalysisViewModel
import com.example.diet_management.ui.theme.DietmanagementTheme
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class AnalysisScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsDataSourceAtBottom() {
        // Given
        val viewModel = AnalysisViewModel(
            FakeMealRepository(listOf(TestData.lunch)),
            FakeFoodRepository(TestData.foods),
            today = { LocalDate.of(2026, 11, 16) },
        )
        composeRule.setContent {
            DietmanagementTheme { AnalysisScreen(viewModel = viewModel) }
        }

        // When: 계산이 끝난 뒤 맨 아래까지 스크롤
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodes(hasScrollToNodeAction()).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNode(hasScrollToNodeAction())
            .performScrollToNode(hasTestTag(AnalysisTestTags.SOURCE_TEXT))

        // Then
        composeRule.onNodeWithTag(AnalysisTestTags.SOURCE_TEXT)
            .assertIsDisplayed()
            .assertTextEquals("영양 데이터 출처: 식품의약품안전처 식품영양성분 데이터베이스")
    }
}
