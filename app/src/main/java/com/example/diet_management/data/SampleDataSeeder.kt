package com.example.diet_management.data

import android.content.Context
import com.example.diet_management.model.Meal
import com.example.diet_management.model.MealItem
import com.example.diet_management.model.MealType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.random.Random

/**
 * 앱을 처음 실행할 때(`meals.json`이 없을 때)만 최근 30일치 샘플 식사를 만들어 저장한다.
 * 사진은 `assets/sample_meal/`의 이미지를 `filesDir/photos/`로 복사해 쓴다.
 */
class SampleDataSeeder(
    context: Context,
    private val mealRepository: MealRepository,
) {
    private val appContext = context.applicationContext

    /** 사진 한 장과 그 사진에 담긴 음식(식품코드, g), 어울리는 끼니 종류. */
    private data class SampleMenu(
        val photo: String,
        val items: List<MealItem>,
        val mealTypes: Set<MealType>,
    )

    private val menus = listOf(
        SampleMenu(
            "아메리카노.jpg",
            listOf(MealItem(AMERICANO, 400.0)),
            setOf(MealType.BREAKFAST, MealType.SNACK),
        ),
        SampleMenu(
            "햄버거.jpg",
            listOf(MealItem(HAMBURGER, 220.0)),
            setOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER),
        ),
        SampleMenu(
            "육개장_시금치나물.jpg",
            listOf(MealItem(YUKGAEJANG, 500.0), MealItem(SPINACH, 70.0)),
            setOf(MealType.LUNCH, MealType.DINNER),
        ),
        SampleMenu(
            "볶음밥_군만두.jpg",
            listOf(MealItem(FRIED_RICE, 350.0), MealItem(DUMPLING, 120.0)),
            setOf(MealType.LUNCH, MealType.DINNER),
        ),
        SampleMenu(
            "짬뽕.jpg",
            listOf(MealItem(JJAMPPONG, 600.0)),
            setOf(MealType.LUNCH, MealType.DINNER),
        ),
        SampleMenu(
            "라면_레몬청차.jpg",
            listOf(MealItem(RAMEN, 500.0), MealItem(LEMON_TEA, 300.0)),
            setOf(MealType.LUNCH, MealType.DINNER),
        ),
        SampleMenu(
            "떡볶이.jpg",
            listOf(MealItem(TTEOKBOKKI, 300.0)),
            setOf(MealType.LUNCH, MealType.DINNER, MealType.SNACK),
        ),
        SampleMenu(
            "빙수.jpg",
            listOf(MealItem(BINGSU, 350.0)),
            setOf(MealType.SNACK),
        ),
        SampleMenu(
            "생크림케이크_카페모카.jpg",
            listOf(MealItem(CAKE, 120.0), MealItem(MOCHA, 350.0)),
            setOf(MealType.SNACK),
        ),
        SampleMenu(
            "카페라떼_카푸치노_바닐라.jpg",
            listOf(MealItem(LATTE, 350.0), MealItem(CAPPUCCINO, 350.0), MealItem(VANILLA_LATTE, 350.0)),
            setOf(MealType.SNACK),
        ),
    )

    /** 끼니 종류별 그럴듯한 시각 범위(시작 시각, 범위 분). */
    private val timeRanges = mapOf(
        MealType.BREAKFAST to (LocalTime.of(7, 30) to 90),
        MealType.LUNCH to (LocalTime.of(11, 50) to 90),
        MealType.SNACK to (LocalTime.of(15, 0) to 120),
        MealType.DINNER to (LocalTime.of(18, 0) to 100),
    )

    /** `meals.json`이 없으면 샘플을 만든다. 이미 있으면 아무것도 하지 않는다. */
    suspend fun seedIfNeeded() {
        val mealsFile = File(appContext.filesDir, FileMealRepository.FILE_NAME)
        if (withContext(Dispatchers.IO) { mealsFile.exists() }) return

        val photoPaths = copyPhotos()
        val now = LocalDateTime.now()
        // 매번 같은 샘플이 나오도록 시드를 고정한다.
        val random = Random(SEED)

        for (daysAgo in 29 downTo 0) {
            val date = now.toLocalDate().minusDays(daysAgo.toLong())
            for (type in pickMealTypes(random)) {
                val dateTime = randomTime(date, type, random)
                if (dateTime.isAfter(now)) continue // 오늘의 아직 오지 않은 끼니는 만들지 않는다.
                val menu = menus.filter { type in it.mealTypes }.random(random)
                mealRepository.addMeal(
                    Meal(
                        id = "sample-$date-${type.name}",
                        dateTime = dateTime.toString(),
                        mealType = type,
                        photoPath = photoPaths.getValue(menu.photo),
                        items = menu.items,
                    )
                )
            }
        }
    }

    /** 하루 2~4끼: 중식·석식은 항상, 조식·간식은 무작위로 더한다. */
    private fun pickMealTypes(random: Random): List<MealType> {
        val extras = listOf(MealType.BREAKFAST, MealType.SNACK).shuffled(random).take(random.nextInt(0, 3))
        return MealType.entries.filter { it == MealType.LUNCH || it == MealType.DINNER || it in extras }
    }

    private fun randomTime(date: LocalDate, type: MealType, random: Random): LocalDateTime {
        val (start, rangeMinutes) = timeRanges.getValue(type)
        // 5분 단위로 맞춘다.
        val minutes = random.nextInt(0, rangeMinutes / 5 + 1) * 5L
        return LocalDateTime.of(date, start).plusMinutes(minutes)
    }

    /** assets의 샘플 사진을 `filesDir/photos/`로 한 번씩 복사하고, 파일명 → 저장 경로 맵을 돌려준다. */
    private suspend fun copyPhotos(): Map<String, String> = withContext(Dispatchers.IO) {
        val dir = File(appContext.filesDir, "photos").apply { mkdirs() }
        menus.map { it.photo }.distinct().withIndex().associate { (index, name) ->
            val target = File(dir, "sample_$index.jpg")
            appContext.assets.open("$ASSET_DIR/$name").use { input ->
                target.outputStream().use { input.copyTo(it) }
            }
            name to target.absolutePath
        }
    }

    private companion object {
        const val ASSET_DIR = "sample_meal"
        const val SEED = 2026

        // foods.json의 식품코드
        const val AMERICANO = "D320-748080000-0001"
        const val HAMBURGER = "D102-123000000-0001"
        const val YUKGAEJANG = "D105-245000000-0001"
        const val SPINACH = "D113-586000000-0001"
        const val FRIED_RICE = "D101-017000000-0001"
        const val DUMPLING = "D103-150030000-0001"
        const val JJAMPPONG = "D103-172000000-0001"
        const val RAMEN = "D103-148000000-0001"
        const val LEMON_TEA = "D320-725000000-0001"
        const val TTEOKBOKKI = "D110-467000000-0001"
        const val BINGSU = "D319-709053900-0001"
        const val CAKE = "D102-110210000-0001"
        const val MOCHA = "D320-748120000-0001"
        const val LATTE = "D320-748110000-0001"
        const val CAPPUCCINO = "D320-748130000-0001"
        const val VANILLA_LATTE = "D320-723050000-0001"
    }
}
