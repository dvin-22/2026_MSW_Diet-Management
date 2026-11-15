package com.example.diet_management.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.example.diet_management.domain.NutritionCalculator.formatOneDecimal
import com.example.diet_management.model.Nutrition
import java.io.File
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E) a h:mm", Locale.KOREAN)

/**
 * 식사 상세 화면.
 * @param showBack 한 화면(1단)일 때만 true로 넘겨 뒤로가기 아이콘을 보인다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(
    viewModel: MealDetailViewModel,
    showBack: Boolean,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("식사 상세") },
                navigationIcon = {
                    if (showBack) {
                        IconButton(onClick = onBack) {
                            Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "뒤로가기")
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        val modifier = Modifier.fillMaxSize().padding(innerPadding)
        when (val state = uiState) {
            MealDetailUiState.Loading ->
                Box(modifier, contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is MealDetailUiState.Error ->
                Box(modifier, contentAlignment = Alignment.Center) { Text(state.message) }
            is MealDetailUiState.Success -> MealDetailContent(state, modifier)
        }
    }
}

@Composable
private fun MealDetailContent(state: MealDetailUiState.Success, modifier: Modifier) {
    val meal = state.meal
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AsyncImage(
            model = File(meal.photoPath),
            contentDescription = "${meal.mealType.label} 식사 사진",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .clip(MaterialTheme.shapes.medium),
        )
        Column {
            Text(
                meal.localDateTime().format(dateTimeFormatter),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(meal.mealType.label, style = MaterialTheme.typography.headlineSmall)
        }

        Text("먹은 음식", style = MaterialTheme.typography.titleMedium)
        state.items.forEachIndexed { index, item ->
            if (index > 0) HorizontalDivider()
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(item.name, style = MaterialTheme.typography.titleSmall)
                    Text("${formatOneDecimal(item.grams)} g")
                }
                Text(
                    "탄 ${formatOneDecimal(item.nutrition.carb)}g · " +
                        "단 ${formatOneDecimal(item.nutrition.protein)}g · " +
                        "지 ${formatOneDecimal(item.nutrition.fat)}g · " +
                        "${formatOneDecimal(item.nutrition.kcal)} kcal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        TotalCard(state.total)
    }
}

/** 총 탄수화물·단백질·지방·열량 카드. */
@Composable
private fun TotalCard(total: Nutrition) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text("합계", style = MaterialTheme.typography.titleMedium)
            TotalRow("탄수화물", "${formatOneDecimal(total.carb)} g")
            TotalRow("단백질", "${formatOneDecimal(total.protein)} g")
            TotalRow("지방", "${formatOneDecimal(total.fat)} g")
            TotalRow("열량", "${formatOneDecimal(total.kcal)} kcal")
        }
    }
}

@Composable
private fun TotalRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(value)
    }
}
