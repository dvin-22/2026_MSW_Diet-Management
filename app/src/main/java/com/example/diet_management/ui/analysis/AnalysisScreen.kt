package com.example.diet_management.ui.analysis

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.example.diet_management.domain.NutritionCalculator.formatOneDecimal

/** UI 테스트에서 찾을 testTag 모음. */
object AnalysisTestTags {
    const val SOURCE_TEXT = "source_text"
}

/** 최근 30일 분석 화면. 화면이 보일 때마다 다시 계산한다. */
@Composable
fun AnalysisScreen(viewModel: AnalysisViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.refresh() }

    when (val state = uiState) {
        AnalysisUiState.Loading ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        is AnalysisUiState.Error ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message) }
        is AnalysisUiState.Success -> AnalysisContent(state)
    }
}

@Composable
private fun AnalysisContent(state: AnalysisUiState.Success) {
    val isExpanded = currentWindowAdaptiveInfo().windowSizeClass
        .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val columns = if (isExpanded) 2 else 1

    // 상단 카드와 출처는 모든 열을 차지하고, 끼니별 카드만 1열 또는 2열로 놓인다.
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxSize(),
        // 앱바가 없는 화면이라 상태 표시줄(시간·배터리) 높이만큼 위쪽 여백을 더한다.
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp + WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
            bottom = 16.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            TotalKcalCard(totalKcal = state.totalKcal, mealCount = state.mealCount)
        }
        items(state.byMealType, key = { it.mealType.name }) { analysis ->
            MealTypeCard(analysis)
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                "영양 데이터 출처: 식품의약품안전처 식품영양성분 데이터베이스",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .testTag(AnalysisTestTags.SOURCE_TEXT),
            )
        }
    }
}

/** 최근 30일 총 섭취 열량을 크게 보여 주는 카드. */
@Composable
private fun TotalKcalCard(totalKcal: Double, mealCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("최근 30일 총 섭취 열량", style = MaterialTheme.typography.titleMedium)
            Text(
                "${formatOneDecimal(totalKcal)} kcal",
                style = MaterialTheme.typography.displaySmall,
            )
            Text("총 ${mealCount}끼 기록", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/** 끼니 종류 하나의 열량과 탄·단·지 비율 막대. */
@Composable
private fun MealTypeCard(analysis: MealTypeAnalysis) {
    val nutrition = analysis.nutrition
    val ratio = analysis.ratio
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(analysis.mealType.label, style = MaterialTheme.typography.titleMedium)
                Text("${formatOneDecimal(nutrition.kcal)} kcal", style = MaterialTheme.typography.titleMedium)
            }
            MacroBar("탄수화물", nutrition.carb, ratio.carb)
            MacroBar("단백질", nutrition.protein, ratio.protein)
            MacroBar("지방", nutrition.fat, ratio.fat)
        }
    }
}

/** 라벨, g, 비율(%)과 막대 하나. [ratio]는 0.0~1.0이다. */
@Composable
private fun MacroBar(label: String, grams: Double, ratio: Double) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("$label ${formatOneDecimal(grams)}g", style = MaterialTheme.typography.bodyMedium)
            Text("${formatOneDecimal(ratio * 100)}%", style = MaterialTheme.typography.bodyMedium)
        }
        LinearProgressIndicator(
            progress = { ratio.toFloat() },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
