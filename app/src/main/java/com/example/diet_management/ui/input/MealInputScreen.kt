package com.example.diet_management.ui.input

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import coil3.compose.AsyncImage
import com.example.diet_management.domain.NutritionCalculator
import com.example.diet_management.model.Food
import com.example.diet_management.model.Nutrition
import java.io.File
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.diet_management.R
import com.example.diet_management.model.MealType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)", Locale.KOREAN)
private val timeFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN)

/** 식사 입력 화면. ViewModel의 상태를 읽어 아래로 내려 주고, 이벤트는 ViewModel 함수로 올린다. */
@Composable
fun MealInputScreen(
    viewModel: MealInputViewModel,
    onBack: () -> Unit,
    onSaved: (mealId: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    // 저장이 끝나 새 식사 id가 생기면 목록 화면으로 돌아간다.
    LaunchedEffect(uiState.savedMealId) {
        uiState.savedMealId?.let(onSaved)
    }

    MealInputContent(
        state = uiState,
        onBack = onBack,
        onDateChange = viewModel::onDateChange,
        onTimeChange = viewModel::onTimeChange,
        onMealTypeChange = viewModel::onMealTypeChange,
        onPhotoPicked = viewModel::selectPhoto,
        onQueryChange = viewModel::onQueryChange,
        onAddFood = viewModel::addFood,
        onRemoveFood = viewModel::removeFood,
        onSave = viewModel::save,
    )
}

/** 상태만 받아 그리는 화면 본문. 다음 단계에서 사진·음식 검색·미리보기 영역을 이어 붙인다. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealInputContent(
    state: MealInputUiState,
    onBack: () -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    onMealTypeChange: (MealType) -> Unit,
    onPhotoPicked: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onAddFood: (Food, String) -> Unit,
    onRemoveFood: (String) -> Unit,
    onSave: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("식사 입력") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "뒤로가기")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            PhotoSection(
                photoPath = state.photoPath,
                onPhotoPicked = onPhotoPicked,
            )
            DateTimeSection(
                date = state.date,
                time = state.time,
                onDateChange = onDateChange,
                onTimeChange = onTimeChange,
            )
            MealTypeSection(
                selected = state.mealType,
                onSelect = onMealTypeChange,
            )
            FoodSearchSection(
                query = state.query,
                results = state.searchResults,
                onQueryChange = onQueryChange,
                onAddFood = onAddFood,
            )
            SelectedFoodsSection(
                items = state.items,
                onRemove = onRemoveFood,
            )
            NutritionPreviewCard(preview = state.preview)
            state.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            Button(
                onClick = onSave,
                enabled = !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(MealInputTestTags.SAVE_BUTTON),
            ) {
                Text("저장")
            }
        }
    }
}

/**
 * 사진 영역. 누르면 Photo Picker(이미지만)를 열고, 고른 사진의 Uri 문자열을 [onPhotoPicked]로 올린다.
 * 앱 내부로 복사하는 일은 ViewModel이 맡는다. 사진을 고른 뒤에도 다시 눌러 바꿀 수 있다.
 */
@Composable
private fun PhotoSection(
    photoPath: String?,
    onPhotoPicked: (String) -> Unit,
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        // 사용자가 아무것도 고르지 않고 닫으면 uri는 null이다.
        if (uri != null) onPhotoPicked(uri.toString())
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable {
                launcher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        if (photoPath == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painterResource(R.drawable.ic_add),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text("사진 추가", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            AsyncImage(
                model = File(photoPath),
                contentDescription = "선택한 식사 사진",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/** 날짜·시각 버튼. 누르면 각각 DatePicker / TimePicker 다이얼로그를 연다. */
@Composable
private fun DateTimeSection(
    date: LocalDate,
    time: LocalTime,
    onDateChange: (LocalDate) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
) {
    // 다이얼로그 열림 여부는 이 화면 안에서만 쓰는 UI 상태라 ViewModel에 두지 않는다.
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("날짜·시각", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { showDatePicker = true }) {
                Text(date.format(dateFormatter))
            }
            OutlinedButton(onClick = { showTimePicker = true }) {
                Text(time.format(timeFormatter))
            }
        }
    }

    if (showDatePicker) {
        MealDatePickerDialog(
            initialDate = date,
            onConfirm = {
                onDateChange(it)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
        )
    }
    if (showTimePicker) {
        MealTimePickerDialog(
            initialTime = time,
            onConfirm = {
                onTimeChange(it)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false },
        )
    }
}

/** 날짜 선택 다이얼로그. 오늘 이후 날짜는 고를 수 없다. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealDatePickerDialog(
    initialDate: LocalDate,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    // DatePicker는 UTC 자정 기준 밀리초를 쓴다.
    val todayMillis = LocalDate.now().toUtcMillis()
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.toUtcMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= todayMillis
        },
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                pickerState.selectedDateMillis?.let { onConfirm(it.toLocalDate()) } ?: onDismiss()
            }) { Text("확인") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        },
    ) {
        DatePicker(state = pickerState)
    }
}

/** 시각 선택 다이얼로그(오전/오후 12시간 형식). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealTimePickerDialog(
    initialTime: LocalTime,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    val pickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = false,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("시각 선택") },
        text = { TimePicker(state = pickerState) },
        confirmButton = {
            TextButton(onClick = { onConfirm(LocalTime.of(pickerState.hour, pickerState.minute)) }) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        },
    )
}

/** 끼니 종류 FilterChip 4개 중 하나를 고른다. */
@Composable
private fun MealTypeSection(
    selected: MealType?,
    onSelect: (MealType) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("끼니 종류", style = MaterialTheme.typography.titleMedium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MealType.entries.forEach { type ->
                FilterChip(
                    selected = type == selected,
                    onClick = { onSelect(type) },
                    label = { Text(type.label) },
                )
            }
        }
    }
}

/**
 * 음식 검색창과 검색 결과. 검색어가 바뀔 때마다 ViewModel이 걸러 준 결과를 보여 준다.
 * 결과를 누르면 섭취량(g) 입력 다이얼로그를 연다.
 */
@Composable
private fun FoodSearchSection(
    query: String,
    results: List<Food>,
    onQueryChange: (String) -> Unit,
    onAddFood: (Food, String) -> Unit,
) {
    // 섭취량을 입력 중인 음식. null이면 다이얼로그를 닫는다.
    var pendingFood by remember { mutableStateOf<Food?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("음식 담기", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("음식 이름 검색") },
            leadingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        if (query.isNotBlank() && results.isEmpty()) {
            Text("검색 결과가 없어요", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        // 화면 전체가 스크롤되므로 LazyColumn을 겹치지 않고 Column으로 그린다.
        results.forEach { food ->
            ListItem(
                headlineContent = { Text(food.name) },
                supportingContent = {
                    Text("${food.category} · 100g당 ${NutritionCalculator.formatOneDecimal(food.kcalPer100g)} kcal")
                },
                modifier = Modifier.clickable { pendingFood = food },
            )
        }
    }

    pendingFood?.let { food ->
        GramsDialog(
            food = food,
            onConfirm = { gramsText ->
                onAddFood(food, gramsText)
                pendingFood = null
            },
            onDismiss = { pendingFood = null },
        )
    }
}

/** 섭취량(g) 입력 다이얼로그. 숫자 키보드를 띄운다. */
@Composable
private fun GramsDialog(
    food: Food,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var gramsText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(food.name) },
        text = {
            OutlinedTextField(
                value = gramsText,
                onValueChange = { gramsText = it },
                label = { Text("섭취량") },
                suffix = { Text("g") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(gramsText) }) { Text("담기") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        },
    )
}

/** 담은 음식 목록: 이름, g, 열량과 삭제 버튼. */
@Composable
private fun SelectedFoodsSection(
    items: List<SelectedFood>,
    onRemove: (String) -> Unit,
) {
    if (items.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("담은 음식", style = MaterialTheme.typography.titleMedium)
        items.forEach { item ->
            val kcal = NutritionCalculator.calculate(item.food, item.grams).kcal
            ListItem(
                headlineContent = { Text(item.food.name) },
                supportingContent = {
                    Text(
                        "${NutritionCalculator.formatOneDecimal(item.grams)} g · " +
                            "${NutritionCalculator.formatOneDecimal(kcal)} kcal"
                    )
                },
                trailingContent = {
                    IconButton(onClick = { onRemove(item.food.id) }) {
                        Icon(
                            painterResource(R.drawable.ic_close),
                            contentDescription = "${item.food.name} 삭제",
                        )
                    }
                },
            )
        }
    }
}

/** 담은 음식의 영양소 합계를 소수점 1자리로 보여 주는 카드. */
@Composable
private fun NutritionPreviewCard(preview: Nutrition) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text("영양소 미리보기", style = MaterialTheme.typography.titleMedium)
            NutrientRow("탄수화물", preview.carb, "g", MealInputTestTags.PREVIEW_CARB)
            NutrientRow("단백질", preview.protein, "g", MealInputTestTags.PREVIEW_PROTEIN)
            NutrientRow("지방", preview.fat, "g", MealInputTestTags.PREVIEW_FAT)
            NutrientRow("열량", preview.kcal, "kcal", MealInputTestTags.PREVIEW_KCAL)
        }
    }
}

@Composable
private fun NutrientRow(label: String, value: Double, unit: String, tag: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(
            "${NutritionCalculator.formatOneDecimal(value)} $unit",
            modifier = Modifier.testTag(tag),
        )
    }
}

/** UI 테스트에서 찾을 testTag 모음. */
object MealInputTestTags {
    const val PREVIEW_CARB = "preview_carb"
    const val PREVIEW_PROTEIN = "preview_protein"
    const val PREVIEW_FAT = "preview_fat"
    const val PREVIEW_KCAL = "preview_kcal"
    const val SAVE_BUTTON = "save_button"
}

private fun LocalDate.toUtcMillis(): Long =
    atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
