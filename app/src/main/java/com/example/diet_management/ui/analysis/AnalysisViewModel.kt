package com.example.diet_management.ui.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diet_management.data.FoodRepository
import com.example.diet_management.data.MealRepository
import com.example.diet_management.domain.MealAnalyzer
import com.example.diet_management.domain.NutritionCalculator
import com.example.diet_management.model.MealType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * 최근 30일(오늘 포함) 식사를 분석한다.
 * @param today 오늘 날짜. 테스트에서 날짜를 고정할 수 있게 매개변수로 받는다.
 */
class AnalysisViewModel(
    private val mealRepository: MealRepository,
    private val foodRepository: FoodRepository,
    private val today: () -> LocalDate = { LocalDate.now() },
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnalysisUiState>(AnalysisUiState.Loading)
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    /** 다시 계산한다. 분석 화면이 보일 때마다 호출된다. */
    fun refresh() {
        viewModelScope.launch {
            try {
                val foods = foodRepository.getFoods().associateBy { it.id }
                val meals = MealAnalyzer.filterLast30Days(mealRepository.getMeals(), today())
                val byType = MealAnalyzer.nutritionByMealType(meals, foods)

                _uiState.value = AnalysisUiState.Success(
                    totalKcal = MealAnalyzer.totalKcal(meals, foods),
                    mealCount = meals.size,
                    byMealType = MealType.entries.map { type ->
                        val nutrition = byType.getValue(type)
                        MealTypeAnalysis(type, nutrition, NutritionCalculator.macroRatio(nutrition))
                    },
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = AnalysisUiState.Error("분석 데이터를 불러오지 못했어요.")
            }
        }
    }
}
