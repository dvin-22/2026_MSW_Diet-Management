package com.example.diet_management.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diet_management.data.FoodRepository
import com.example.diet_management.data.MealRepository
import com.example.diet_management.domain.MealAnalyzer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MealListViewModel(
    private val mealRepository: MealRepository,
    private val foodRepository: FoodRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MealListUiState>(MealListUiState.Loading)
    val uiState: StateFlow<MealListUiState> = _uiState.asStateFlow()

    /** 식사와 음식 정보를 다시 불러와 날짜별 그룹을 만든다. 목록 화면이 보일 때마다 호출된다. */
    fun refresh() {
        viewModelScope.launch {
            try {
                val meals = mealRepository.getMeals()
                val foods = foodRepository.getFoods().associateBy { it.id }
                val mealsByDate = meals.groupBy { it.localDateTime().toLocalDate() }

                val groups = MealAnalyzer.dailySummaries(meals, foods).map { summary ->
                    MealDayGroup(
                        date = summary.date,
                        totalKcal = summary.totalKcal,
                        mealCount = summary.mealCount,
                        meals = mealsByDate.getValue(summary.date)
                            .sortedByDescending { it.localDateTime() }
                            .map { MealWithKcal(it, MealAnalyzer.mealNutrition(it, foods).kcal) },
                    )
                }
                _uiState.value = MealListUiState.Success(groups)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = MealListUiState.Error(e.message ?: "식사 목록을 불러오지 못했습니다.")
            }
        }
    }
}
