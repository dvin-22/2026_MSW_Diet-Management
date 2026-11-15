package com.example.diet_management.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.diet_management.R
import com.example.diet_management.data.AssetFoodRepository
import com.example.diet_management.data.FileMealRepository
import com.example.diet_management.data.FilePhotoStorage
import com.example.diet_management.data.FoodRepository
import com.example.diet_management.data.MealRepository
import com.example.diet_management.data.PhotoStorage
import com.example.diet_management.ui.analysis.AnalysisScreen
import com.example.diet_management.ui.analysis.AnalysisViewModel
import com.example.diet_management.ui.input.MealInputScreen
import com.example.diet_management.ui.input.MealInputViewModel
import com.example.diet_management.ui.list.MealRecordPane
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diet_management.ui.navigation.Analysis
import com.example.diet_management.ui.navigation.MealInput
import com.example.diet_management.ui.navigation.MealList

private const val NEW_MEAL_ID_KEY = "newMealId"

/** 하단 바/레일에 보여 줄 탭. */
private enum class Tab(val label: String, val iconRes: Int, val route: Any) {
    RECORD("기록", R.drawable.ic_list, MealList),
    ANALYSIS("분석", R.drawable.ic_bar_chart, Analysis),
}

/**
 * 앱 최상단 Composable.
 * Repository와 PhotoStorage를 여기서 한 번만 만들고(remember), 각 화면에 인터페이스 타입으로 넘긴다.
 * 화면에서는 `viewModel { MealListViewModel(mealRepository, foodRepository) }`처럼 람다로 ViewModel을 만든다.
 */
@Composable
fun DietApp() {
    val context = LocalContext.current.applicationContext
    val foodRepository: FoodRepository = remember { AssetFoodRepository(context) }
    val mealRepository: MealRepository = remember { FileMealRepository(context) }
    val photoStorage: PhotoStorage = remember { FilePhotoStorage(context) }

    DietContent(
        foodRepository = foodRepository,
        mealRepository = mealRepository,
        photoStorage = photoStorage,
    )
}

@Composable
private fun DietContent(
    foodRepository: FoodRepository,
    mealRepository: MealRepository,
    photoStorage: PhotoStorage,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    // 입력 화면에서는 탭 바를 숨기고, 그 외에는 창 크기에 따라 하단 바/레일을 자동 선택한다.
    val isInput = destination?.hasRoute<MealInput>() == true
    val layoutType = if (isInput) {
        NavigationSuiteType.None
    } else {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
    }

    NavigationSuiteScaffold(
        layoutType = layoutType,
        navigationSuiteItems = {
            Tab.entries.forEach { tab ->
                item(
                    selected = destination?.hierarchy?.any { it.hasRoute(tab.route::class) } == true,
                    onClick = { navController.navigateToTab(tab.route) },
                    icon = { Icon(painterResource(tab.iconRes), contentDescription = null) },
                    label = { Text(tab.label) },
                )
            }
        },
    ) {
        NavHost(navController = navController, startDestination = MealList) {
            composable<MealList> { entry ->
                // 입력 화면이 저장 후 넘겨 준 새 식사 id
                val newMealId by entry.savedStateHandle
                    .getStateFlow<String?>(NEW_MEAL_ID_KEY, null)
                    .collectAsState()
                MealRecordPane(
                    mealRepository = mealRepository,
                    foodRepository = foodRepository,
                    newMealId = newMealId,
                    onNewMealShown = { entry.savedStateHandle[NEW_MEAL_ID_KEY] = null },
                    onAddClick = { navController.navigate(MealInput) },
                )
            }
            composable<Analysis> {
                val viewModel = viewModel { AnalysisViewModel(mealRepository, foodRepository) }
                AnalysisScreen(viewModel = viewModel)
            }
            composable<MealInput> {
                val viewModel = viewModel {
                    MealInputViewModel(mealRepository, foodRepository, photoStorage)
                }
                MealInputScreen(
                    viewModel = viewModel,
                    onBack = { navController.navigateUp() },
                    // 저장한 식사 id를 목록 화면에 넘기고 돌아가면, 목록이 그 식사를 선택한다.
                    onSaved = { mealId ->
                        navController.previousBackStackEntry?.savedStateHandle?.set(NEW_MEAL_ID_KEY, mealId)
                        navController.popBackStack()
                    },
                )
            }
        }
    }
}

/** 탭 이동: 시작 화면까지 비우고 각 탭의 상태를 저장·복원한다. */
private fun NavHostController.navigateToTab(route: Any) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
