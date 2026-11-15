package com.example.diet_management.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diet_management.data.FoodRepository
import com.example.diet_management.data.MealRepository
import com.example.diet_management.domain.NutritionCalculator
import com.example.diet_management.model.Nutrition
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** [mealId]로 식사를 조회해 음식별 영양소와 합계를 만든다. 화면에는 id만 전달된다. */
class MealDetailViewModel(
    private val mealRepository: MealRepository,
    private val foodRepository: FoodRepository,
    private val mealId: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MealDetailUiState>(MealDetailUiState.Loading)
    val uiState: StateFlow<MealDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                val meal = mealRepository.getMeal(mealId)
                if (meal == null) {
                    _uiState.value = MealDetailUiState.Error(NOT_FOUND)
                    return@launch
                }
                val foods = foodRepository.getFoods().associateBy { it.id }
                val items = meal.items.map { item ->
                    val food = foods[item.foodId]
                    DetailFoodItem(
                        name = food?.name ?: "알 수 없는 음식",
                        grams = item.grams,
                        nutrition = food?.let { NutritionCalculator.calculate(it, item.grams) }
                            ?: Nutrition(0.0, 0.0, 0.0, 0.0),
                    )
                }
                _uiState.value = MealDetailUiState.Success(
                    meal = meal,
                    items = items,
                    total = NutritionCalculator.sum(items.map { it.nutrition }),
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = MealDetailUiState.Error("기록을 불러오지 못했어요.")
            }
        }
    }

    companion object {
        const val NOT_FOUND = "기록을 찾을 수 없어요"
    }
}
