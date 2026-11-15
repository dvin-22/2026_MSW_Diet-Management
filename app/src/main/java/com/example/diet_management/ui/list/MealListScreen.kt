package com.example.diet_management.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.diet_management.R
import com.example.diet_management.domain.NutritionCalculator
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateFormatter = DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN)
private val timeFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN)

/**
 * 식사 목록 화면. 날짜별로 묶어 최신순으로 보여 준다.
 * 이 화면이 다시 보일 때마다(입력 화면에서 돌아올 때 포함) 목록을 새로 불러온다.
 *
 * @param selectedMealId 2단 화면에서 선택된 식사 id. 해당 카드를 강조한다.
 */
@Composable
fun MealListScreen(
    viewModel: MealListViewModel,
    selectedMealId: String?,
    onMealClick: (String) -> Unit,
    onAddClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    // 다른 화면에서 돌아오면 이 Composable이 다시 컴포지션에 들어오므로 그때마다 실행된다.
    LaunchedEffect(Unit) { viewModel.refresh() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(painterResource(R.drawable.ic_add), contentDescription = "식사 추가")
            }
        },
    ) { innerPadding ->
        val modifier = Modifier.fillMaxSize().padding(innerPadding)
        when (val state = uiState) {
            MealListUiState.Loading -> CenterBox(modifier) { CircularProgressIndicator() }
            is MealListUiState.Error -> CenterBox(modifier) { Text(state.message) }
            is MealListUiState.Success ->
                if (state.groups.isEmpty()) {
                    CenterBox(modifier) { Text("아직 기록한 식사가 없어요") }
                } else {
                    MealList(
                        groups = state.groups,
                        selectedMealId = selectedMealId,
                        onMealClick = onMealClick,
                        modifier = modifier,
                    )
                }
        }
    }
}

@Composable
private fun CenterBox(modifier: Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) { content() }
}

@Composable
private fun MealList(
    groups: List<MealDayGroup>,
    selectedMealId: String?,
    onMealClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        // FAB에 마지막 카드가 가려지지 않도록 아래 여백을 둔다.
        contentPadding = PaddingValues(bottom = 88.dp),
    ) {
        groups.forEach { group ->
            stickyHeader(key = group.date.toString()) {
                DayHeader(group)
            }
            items(group.meals, key = { it.meal.id }) { item ->
                MealCard(
                    item = item,
                    selected = item.meal.id == selectedMealId,
                    onClick = { onMealClick(item.meal.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }
        }
    }
}

/** "11월 14일 (금) · 1,850.0 kcal · 3끼" 형식의 날짜 헤더. */
@Composable
private fun DayHeader(group: MealDayGroup) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Text(
            text = "${formatDate(group.date)} · " +
                "${NutritionCalculator.formatOneDecimal(group.totalKcal)} kcal · ${group.mealCount}끼",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun MealCard(
    item: MealWithKcal,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val meal = item.meal
    val mealTypeLabel = meal.mealType.label
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            },
        ),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = File(meal.photoPath),
                contentDescription = "$mealTypeLabel 식사 사진",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
            Spacer(Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(mealTypeLabel, style = MaterialTheme.typography.titleMedium)
                Text(
                    meal.localDateTime().format(timeFormatter),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "${NutritionCalculator.formatOneDecimal(item.kcal)} kcal",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

private fun formatDate(date: LocalDate): String = date.format(dateFormatter)
