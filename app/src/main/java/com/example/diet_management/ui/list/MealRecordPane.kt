package com.example.diet_management.ui.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diet_management.data.FoodRepository
import com.example.diet_management.data.MealRepository
import com.example.diet_management.ui.detail.MealDetailScreen
import com.example.diet_management.ui.detail.MealDetailViewModel
import kotlinx.coroutines.launch

/**
 * 기록 탭: 목록(list)과 상세(detail)를 [ListDetailPaneScaffold]로 배치한다.
 * 넓은 창에서는 2단, 좁은 창에서는 한 화면씩 보여 준다(기본 동작).
 *
 * @param newMealId 입력 화면에서 방금 저장한 식사 id. 있으면 그 식사를 선택한다.
 * @param onNewMealShown [newMealId]를 처리한 뒤 호출해 다시 선택되지 않게 한다.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MealRecordPane(
    mealRepository: MealRepository,
    foodRepository: FoodRepository,
    newMealId: String?,
    onNewMealShown: () -> Unit,
    onAddClick: () -> Unit,
) {
    // 선택된 식사 id(contentKey)는 navigator가 저장하므로 화면을 회전해도 유지된다.
    val navigator = rememberListDetailPaneScaffoldNavigator<String>()
    val scope = rememberCoroutineScope()
    val selectedMealId = navigator.currentDestination?.contentKey

    // 한 화면일 때만 상세 → 목록으로 돌아가는 Back이 있다. 2단에서는 탭 이동 등 기본 Back을 쓴다.
    BackHandler(enabled = navigator.canNavigateBack()) {
        scope.launch { navigator.navigateBack() }
    }

    LaunchedEffect(newMealId) {
        if (newMealId != null) {
            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, newMealId)
            onNewMealShown()
        }
    }

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                val viewModel = viewModel { MealListViewModel(mealRepository, foodRepository) }
                MealListScreen(
                    viewModel = viewModel,
                    selectedMealId = selectedMealId,
                    onMealClick = { mealId ->
                        scope.launch { navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, mealId) }
                    },
                    onAddClick = onAddClick,
                )
            }
        },
        detailPane = {
            AnimatedPane {
                if (selectedMealId == null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "왼쪽에서 식사를 선택하세요",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    // 식사마다 다른 ViewModel이 필요하므로 id를 key로 준다.
                    val viewModel = viewModel(key = selectedMealId) {
                        MealDetailViewModel(mealRepository, foodRepository, selectedMealId)
                    }
                    // 목록이 숨겨진 한 화면 상태일 때만 뒤로가기 아이콘을 보인다.
                    val isSinglePane =
                        navigator.scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Hidden
                    MealDetailScreen(
                        viewModel = viewModel,
                        showBack = isSinglePane,
                        onBack = { scope.launch { navigator.navigateBack() } },
                    )
                }
            }
        },
    )
}
