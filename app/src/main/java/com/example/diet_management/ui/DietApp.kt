package com.example.diet_management.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.diet_management.data.AssetFoodRepository
import com.example.diet_management.data.FileMealRepository
import com.example.diet_management.data.FilePhotoStorage
import com.example.diet_management.data.FoodRepository
import com.example.diet_management.data.MealRepository
import com.example.diet_management.data.PhotoStorage

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

/** 화면이 생기기 전까지 쓰는 임시 내용. 다음 단계에서 탭·화면으로 바꾼다. */
@Composable
private fun DietContent(
    foodRepository: FoodRepository,
    mealRepository: MealRepository,
    photoStorage: PhotoStorage,
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Text(text = "식단 관리", modifier = Modifier.padding(innerPadding))
    }
}
