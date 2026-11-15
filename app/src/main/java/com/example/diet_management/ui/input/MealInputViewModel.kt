package com.example.diet_management.ui.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diet_management.data.FoodRepository
import com.example.diet_management.data.MealRepository
import com.example.diet_management.data.PhotoStorage
import com.example.diet_management.domain.NutritionCalculator
import com.example.diet_management.domain.searchFoods
import com.example.diet_management.model.Food
import com.example.diet_management.model.Meal
import com.example.diet_management.model.MealItem
import com.example.diet_management.model.MealType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

/**
 * 식사 입력 화면의 ViewModel.
 * @param now 현재 시각. 테스트에서 시각을 고정할 수 있게 매개변수로 받는다.
 */
class MealInputViewModel(
    private val mealRepository: MealRepository,
    private val foodRepository: FoodRepository,
    private val photoStorage: PhotoStorage,
    private val now: () -> LocalDateTime = { LocalDateTime.now() },
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        now().let { MealInputUiState(date = it.toLocalDate(), time = it.toLocalTime().withSecond(0).withNano(0)) }
    )
    val uiState: StateFlow<MealInputUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val foods = foodRepository.getFoods()
                _uiState.update { it.copy(allFoods = foods, searchResults = searchFoods(foods, it.query)) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "음식 정보를 불러오지 못했어요.") }
            }
        }
    }

    fun onDateChange(date: LocalDate) {
        _uiState.update { it.copy(date = date, errorMessage = null) }
    }

    fun onTimeChange(time: LocalTime) {
        _uiState.update { it.copy(time = time, errorMessage = null) }
    }

    fun onMealTypeChange(mealType: MealType) {
        _uiState.update { it.copy(mealType = mealType, errorMessage = null) }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query, searchResults = searchFoods(it.allFoods, query)) }
    }

    /** 음식을 [gramsText]g만큼 담는다. 이미 담은 음식이면 g을 더한다. 0 이하는 거부한다. */
    fun addFood(food: Food, gramsText: String) {
        val grams = gramsText.toDoubleOrNull() ?: 0.0
        if (grams <= 0) {
            _uiState.update { it.copy(errorMessage = "섭취량은 0보다 큰 숫자로 입력해 주세요.") }
            return
        }
        _uiState.update { state ->
            val exists = state.items.any { it.food.id == food.id }
            val items = if (exists) {
                state.items.map { if (it.food.id == food.id) it.copy(grams = it.grams + grams) else it }
            } else {
                state.items + SelectedFood(food, grams)
            }
            state.copy(items = items, preview = previewOf(items), errorMessage = null)
        }
    }

    fun removeFood(foodId: String) {
        _uiState.update { state ->
            val items = state.items.filterNot { it.food.id == foodId }
            state.copy(items = items, preview = previewOf(items))
        }
    }

    /** Photo Picker로 고른 사진을 앱 내부로 복사하고 경로를 상태에 저장한다. */
    fun selectPhoto(uri: String) {
        viewModelScope.launch {
            try {
                val path = photoStorage.savePhoto(uri)
                _uiState.update { it.copy(photoPath = path, errorMessage = null) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "사진을 저장하지 못했어요.") }
            }
        }
    }

    /** 조건(사진, 끼니 종류, 음식 1개 이상, 미래 아님)을 확인하고 식사를 저장한다. */
    fun save() {
        val state = _uiState.value
        if (state.isSaving) return
        val dateTime = LocalDateTime.of(state.date, state.time)
        val reason = when {
            state.photoPath == null -> "사진을 선택해 주세요."
            state.mealType == null -> "끼니 종류를 선택해 주세요."
            state.items.isEmpty() -> "음식을 1개 이상 담아 주세요."
            dateTime.isAfter(now()) -> "미래 날짜·시각은 저장할 수 없어요."
            else -> null
        }
        if (reason != null) {
            _uiState.update { it.copy(errorMessage = reason) }
            return
        }

        val meal = Meal(
            id = UUID.randomUUID().toString(),
            dateTime = dateTime.toString(),
            mealType = state.mealType!!,
            photoPath = state.photoPath!!,
            items = state.items.map { MealItem(it.food.id, it.grams) },
        )
        _uiState.update { it.copy(isSaving = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                mealRepository.addMeal(meal)
                _uiState.update { it.copy(isSaving = false, savedMealId = meal.id) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = "식사를 저장하지 못했어요.") }
            }
        }
    }

    private fun previewOf(items: List<SelectedFood>) =
        NutritionCalculator.sum(items.map { NutritionCalculator.calculate(it.food, it.grams) })
}
