package com.example.diet_management.ui

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.diet_management.fake.FakeFoodRepository
import com.example.diet_management.fake.FakeMealRepository
import com.example.diet_management.fake.TestData
import com.example.diet_management.ui.list.MealRecordPane
import com.example.diet_management.ui.theme.DietmanagementTheme
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test

/**
 * 기록 탭(목록-상세) 테스트. 창 크기를 코드로 바꾸지 않고, 실행한 기기의 실제 창 크기에 따라 판단한다.
 * 넓은 화면 테스트는 태블릿 가로, 좁은 화면 테스트는 폰 세로 에뮬레이터에서 실행된다(나머지는 건너뜀).
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
class MealRecordPaneTest {

    @get:Rule
    val composeRule = createComposeRule()

    /** 기록 탭을 띄우고, ListDetailPaneScaffold 기본 동작으로 몇 단이 되는지 돌려준다. */
    private fun setRecordPane(): Int {
        var paneCount = 0
        composeRule.setContent {
            // ListDetailPaneScaffold가 기본으로 쓰는 것과 같은 계산으로 단 수를 읽는다.
            paneCount = calculatePaneScaffoldDirective(currentWindowAdaptiveInfo()).maxHorizontalPartitions
            DietmanagementTheme {
                MealRecordPane(
                    mealRepository = FakeMealRepository(listOf(TestData.lunch)),
                    foodRepository = FakeFoodRepository(TestData.foods),
                    newMealId = null,
                    onNewMealShown = {},
                    onAddClick = {},
                )
            }
        }
        composeRule.waitForIdle()
        return paneCount
    }

    private fun clickMealCard() {
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("중식").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("중식").performClick()
        // 상세에만 있는 음식 이름이 나타날 때까지 기다린다.
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("쌀밥").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun clickCard_opensDetailWithFoodName() {
        // Given
        setRecordPane()

        // When
        clickMealCard()

        // Then
        // 상세는 세로 스크롤 화면이라, 넓은 창에서는 큰 사진 아래로 밀려 있을 수 있어 먼저 스크롤한다.
        composeRule.onNodeWithText("쌀밥").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun wideScreen_clickCard_showsListAndDetailTogether() {
        // Given: 2단이 되는 넓은 화면(태블릿 가로)에서만 실행
        val paneCount = setRecordPane()
        assumeTrue("2단 화면이 아니라서 건너뜀", paneCount >= 2)

        // When
        clickMealCard()

        // Then: 목록(FAB, 카드)과 상세(음식 이름)가 함께 보임
        composeRule.onNodeWithContentDescription("식사 추가").assertIsDisplayed()
        composeRule.onNodeWithText("오후 12:30").assertIsDisplayed()
        // 상세는 세로 스크롤 화면이라, 넓은 창에서는 큰 사진 아래로 밀려 있을 수 있어 먼저 스크롤한다.
        composeRule.onNodeWithText("쌀밥").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun narrowScreen_clickCard_showsOnlyDetail() {
        // Given: 한 화면(폰 세로)에서만 실행
        val paneCount = setRecordPane()
        assumeTrue("한 화면이 아니라서 건너뜀", paneCount == 1)

        // When
        clickMealCard()

        // Then: 상세만 보이고 목록은 사라짐
        // 상세는 세로 스크롤 화면이라, 넓은 창에서는 큰 사진 아래로 밀려 있을 수 있어 먼저 스크롤한다.
        composeRule.onNodeWithText("쌀밥").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithContentDescription("뒤로가기").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("식사 추가").assertDoesNotExist()
    }
}
