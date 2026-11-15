# 2026_MSW_Diet-Management

## 1. 앱 소개

2026 모바일 소프트웨어 학기말 프로젝트로 만든 **학생 식당 개인 사용 관리 앱**입니다.
매일 먹은 음식을 사진과 함께 기록하면, 공공 영양 데이터베이스를 바탕으로 탄수화물·단백질·지방과 열량을 계산해 보여줍니다.

### 주요 기능

**음식 입력하기**
- 식사 날짜 및 시간 입력
- 음식 사진 입력
- 음식과 반찬 이름 입력
- 음식 섭취량(g) 입력
- 입력 화면에서 영양소(탄수화물, 단백질, 지방) 미리 보기

**식사 보여주기**
- 음식 사진을 식사 날짜와 함께 목록으로 보기
- 목록을 터치하면 입력 내용과 영양소·열량 섭취량 보기
- 식사 날짜별 열량, 끼니 수 요약

**식사 분석하기**
- 최근 1달간 칼로리 총량
- 최근 1달간 섭취 영양소를 끼니 종류(조식, 중식, 석식, 간식/음료)별로 분석

**기타**
- 약 100개 음식으로 구성한 영양 데이터 repository
- 앱 화면에 영양 데이터 출처 표시
- 핵심 로직 로컬 단위 테스트 12개 이상, Compose UI 테스트 2개 이상

## 2. 설치 및 실행 방법

### 개발 환경
- Android Studio Quail 3 | 2026.1.3
- minSdk 30 (Android 11) / targetSdk 37

### 설치
1. 저장소를 clone한다.
2. Android Studio에서 프로젝트 폴더를 연다.
3. Gradle Sync가 끝날 때까지 기다린다.

### 실행
4. 에뮬레이터 또는 실기기(Android 11 이상)를 선택하고 `Run 'app'`을 누른다.

### 테스트 실행
- 단위 테스트: `./gradlew test`
- Compose UI 테스트(에뮬레이터 또는 실기기 연결 필요): `./gradlew connectedAndroidTest`

## 3. 영양 데이터 출처
- 식품의약품안전처 식품영양성분 데이터베이스(https://various.foodsafetykorea.go.kr/nutrient/)

## 4. 역할 분담

| 이름 | GitHub | 담당 역할 |
|---|---|---|
| 김다인 | [@dvin-22](https://github.com/dvin-22) | 코드 설계 및 구현 |
| 조가람 | [@river040118](https://github.com/river040118) | 코드 검토, 시연 영상 제작 |

## 5. 팀원별 Vibe Coding 사용 목록

### 김다인 (@dvin-22)

1. 요구사항 요약
```
식단 관리 앱을 단계별로 만들 거야. 이번에는 코드를 작성하지 말고, 이해한 내용을 짧게 요약해줘. 구체적인 구현은 단계마다 따로 요청할게.

- 앱: 학생 식당에서 먹은 식사를 사진과 함께 기록하고, 식약처 식품영양성분 DB(음식 100개)로 영양소(탄수화물·단백질·지방)와 열량을 계산해 보여주는 Android 앱
- 기술: Kotlin, Jetpack Compose, Material 3
- 화면: 식사 목록(날짜별 요약), 식사 입력, 식사 상세, 최근 30일 분석
- 구조: Repository → ViewModel(StateFlow) → Compose 화면
- 기타: 자몽색 라이트·다크 테마, 태블릿 가로 화면 대응, 로컬 단위 테스트 12개 이상과 Compose UI 테스트 2개 이상
```
**결과**: 코드 작성 없이 앱 목적·기술 스택·4개 화면·계층 구조·테마/테스트 요구사항을 요약해 이해 내용을 확인함.

2. 음식 100개 선정 및 변환 스크립트
```
1. `data/raw/`의 원본 파일에서 학생 식당 메뉴에 자주 나오는 음식 100개를 골라 `data/selected_foods.txt`에 한 줄에 식품코드 하나씩 적어줘. 밥류, 국·탕·찌개류, 반찬류, 면류, 간식·디저트, 음료가 고르게 들어가게 해줘. 같은 음식이 여러 개 있으면 가장 일반적인 것 하나만 골라. 영양성분 함량 기준량이 100g인 항목을 우선으로 골라줘.
2. `tools/convert_foods.py` 스크립트를 만들어줘. `data/raw/`의 원본 파일에서 `data/selected_foods.txt`에 있는 식품코드만 골라 `app/src/main/assets/foods.json`으로 저장해. 각 항목은 `id`(식품코드), `name`, `category`, `kcalPer100g`, `carbPer100g`, `proteinPer100g`, `fatPer100g` 필드를 갖게 해. 100g 기준이 아닌 항목이 있으면 100g당 값으로 환산하고, 값이 비어 있으면 0으로 넣고, 비어 있던 항목 이름을 콘솔에 출력해줘.
```
**결과**: 100g 기준·분석 함량 데이터만으로 음식 100개를 골라 `selected_foods.txt`를 작성함. `convert_foods.py`로 `foods.json`을 생성함(ml 기준은 1 mL = 1 g으로 환산, 빈 값은 0으로 넣고 콘솔에 출력).

3. 데이터 모델
```
`model` 패키지에 데이터 모델을 만들어줘. 필요한 Kotlin serialization 플러그인과 kotlinx-serialization-json 의존성도 추가해줘.

- `Food`: `@Serializable`이고 `foods.json`의 필드(`id`, `name`, `category`, `kcalPer100g`, `carbPer100g`, `proteinPer100g`, `fatPer100g`)와 같게
- `MealType`: enum으로 조식, 중식, 석식, 간식/음료 4가지. 각각 화면에 표시할 한글 라벨을 가짐
- `MealItem`: `foodId`, `grams`
- `Meal`: `id`(String), `dateTime`(String, ISO 형식 예: `"2026-11-15T12:30"`), `mealType`, `photoPath`, `items: List<MealItem>`
- `Nutrition`: `kcal`, `carb`, `protein`, `fat` (Double)

`Meal`은 JSON 파일에 저장할 거라 `@Serializable`로 만들어줘. 날짜·시각이 필요할 때는 `LocalDateTime.parse(dateTime)`으로 변환해서 쓰게 하고, 변환 함수를 `Meal` 안에 하나 만들어줘.
```
**결과**: serialization 플러그인과 kotlinx-serialization-json 1.9.0을 추가하고, `model` 패키지에 `Food`·`MealType`(한글 라벨)·`MealItem`·`Meal`(`localDateTime()` 변환 함수)·`Nutrition`을 만듦. 빌드 성공.

4. 영양소 계산 함수
```
`domain/NutritionCalculator.kt`에 순수 함수만 모아줘. Android 클래스에 의존하지 않게 해.

1. `calculate(food, grams)`: `100g당 값 × grams / 100`으로 `Nutrition` 계산
2. `sum(list: List<Nutrition>)`: 합계
3. `formatOneDecimal(value)`: 표시용으로 소수점 1자리 반올림 문자열 반환(저장값은 반올림하지 않음)

grams가 0 이하면 0을 반환하게 해줘.
```
**결과**: `NutritionCalculator` object에 `calculate`(grams ≤ 0이면 0)·`sum`·`formatOneDecimal`(소수점 1자리, 천 단위 쉼표)을 순수 함수로 만듦. 빌드 성공.

5. 영양소 계산 단위 테스트
```
`NutritionCalculator`의 JUnit 단위 테스트를 `app/src/test`에 만들어줘. Given-When-Then 구조로 다음을 검증해줘.

- 100g이면 DB 값과 같음
- 250g이면 2.5배
- 0g이면 0 (경계값)
- 여러 음식 합계
- 소수점 1자리 반올림(예: 12.345 → "12.3", 12.35 → "12.4")
```
**결과**: `NutritionCalculatorTest`에 Given-When-Then 구조의 테스트 6개(100g, 250g, 0g, 합계, 반올림 2개)를 작성함. 6개 모두 통과.

6. 식사 분석 함수
```
`domain/MealAnalyzer.kt`에 순수 함수를 만들어줘. 음식 정보는 `Map<String, Food>`로 받아.

1. `mealNutrition(meal, foods)`: 식사 한 끼의 영양 합계
2. `filterLast30Days(meals, today: LocalDate)`: 오늘 포함 최근 30일 식사만
3. `totalKcal(meals, foods)`: 열량 총량
4. `nutritionByMealType(meals, foods)`: `MealType`별 탄·단·지·열량 합계. 기록이 없는 끼니도 0으로 포함
5. `dailySummaries(meals, foods)`: 날짜별 열량 합계와 끼니 수. `groupBy`를 써서 최신 날짜 순으로

`today`는 매개변수로 받아서 테스트에서 날짜를 고정할 수 있게 해줘.
```
**결과**: `MealAnalyzer` object에 5개 순수 함수를 만들고, 날짜별 요약용 `DailySummary` 모델을 추가함. 최근 30일은 `today-29일 ~ today`로 계산함. 빌드 성공.

7. 식사 분석 단위 테스트
```
`MealAnalyzer`의 JUnit 테스트를 만들어줘. 테스트용 음식 2개와 식사 몇 개를 직접 만들어서 다음을 검증해줘.

- 30일 경계: 오늘, 29일 전은 포함되고 30일 전은 제외됨
- 빈 목록이면 총 열량 0
- 끼니별 합계가 맞고, 기록이 없는 끼니는 0
- 날짜별 요약의 끼니 수와 열량, 날짜 정렬 순서
```
**결과**: `MealAnalyzerTest`에 테스트 7개(30일 경계 2, 빈 목록 1, 끼니별 합계 2, 날짜별 요약 2)를 작성함. 모두 통과해 단위 테스트가 총 13개가 됨.

8. 음식 Repository와 검색 함수
```
`data/FoodRepository` 인터페이스와 이를 구현한 `AssetFoodRepository`를 만들어줘. ViewModel이 인터페이스에만 의존하게 해서, 테스트에서는 Fake 구현으로 바꿔 끼울 수 있게 해줘.

- `suspend fun getFoods(): List<Food>`: `assets/foods.json`을 kotlinx-serialization으로 파싱. `withContext(Dispatchers.IO)`로 main-safe하게

검색 기능은 Repository에 넣지 말고, `domain/FoodSearch.kt`에 순수 함수 `searchFoods(foods: List<Food>, query: String): List<Food>`로 만들어줘. 이름에 query가 포함된 음식만 `filter`로 거르고, query가 공백뿐이면 빈 목록을 돌려줘.
```
**결과**: `FoodRepository` 인터페이스와 assets의 JSON을 IO 디스패처에서 파싱하는 `AssetFoodRepository`를 만듦. 순수 함수 `searchFoods`(앞뒤 공백 제거 후 이름 포함 검색)를 추가함. 빌드 성공.

9. 식사 Repository
```
`data/MealRepository` 인터페이스와 `FileMealRepository` 구현을 만들어줘.

- 저장 위치: `context.filesDir/meals.json`에 식사 전체 목록을 JSON으로
- `suspend fun getMeals(): List<Meal>`: 저장된 식사 목록(최신순). 파일이 없으면 빈 목록
- `suspend fun addMeal(meal: Meal)`, `suspend fun getMeal(id: String): Meal?`
- 파일 읽기와 쓰기는 `withContext(Dispatchers.IO)`로
```
**결과**: `MealRepository` 인터페이스와 `filesDir/meals.json`에 전체 목록을 저장하는 `FileMealRepository`를 만듦(최신순 정렬, 파일 없으면 빈 목록, 파일 입출력은 IO 디스패처). 빌드 성공.

10. 사진 저장소
```
`data/PhotoStorage` 인터페이스와 구현 클래스 `FilePhotoStorage`를 만들어줘(테스트에서 Fake로 교체할 수 있게).

- `suspend fun savePhoto(uri: String): String`: Photo Picker로 받은 사진의 Uri를 문자열로 받아(로컬 단위 테스트에서 안드로이드 `Uri`를 만들 수 없으니까) 구현 안에서 `Uri`로 바꾸고, `filesDir/photos/` 아래에 고유한 파일명으로 복사한 뒤 저장된 파일 경로를 반환
- 복사는 `withContext(Dispatchers.IO)`로
```
**결과**: `PhotoStorage` 인터페이스와 Uri 문자열을 `filesDir/photos/UUID.jpg`로 복사해 경로를 돌려주는 `FilePhotoStorage`를 만듦(IO 디스패처, 열 수 없으면 `IOException`). 빌드 성공.

11. 의존성 연결 준비
```
ViewModel은 생성자로 Repository와 `PhotoStorage` 인터페이스를 받게 할 거야(테스트에서 Fake로 교체할 수 있게). 앱의 최상단 Composable에서 `LocalContext`로 `AssetFoodRepository`, `FileMealRepository`, `FilePhotoStorage`를 한 번만 만들고(`remember`), 각 화면에서는 `viewModel { MealListViewModel(mealRepository, foodRepository) }`처럼 람다로 넘겨서 만들 수 있게 준비해줘. `Application` 클래스나 `ViewModelProvider.Factory`는 만들지 마. 필요한 lifecycle-viewmodel-compose 의존성도 추가해줘.
```
**결과**: lifecycle-viewmodel-compose 의존성을 추가하고, 최상단 `DietApp`에서 Repository·PhotoStorage를 `remember`로 한 번만 만들어 인터페이스 타입으로 넘기도록 함. `MainActivity`는 `DietApp()`만 호출하게 바꿈. 빌드 성공.

12. Fake 구현과 검색 테스트
```
테스트용 `FakeFoodRepository`, `FakeMealRepository`, `FakePhotoStorage`(받은 문자열로 가짜 경로를 돌려줌)를 `app/src/test`에 만들어줘. 네트워크나 파일 없이 미리 정한 값을 돌려주는 인터페이스 구현이야. 필요한 test 의존성(kotlinx-coroutines-test)도 추가해줘. 그리고 `searchFoods` 함수의 JUnit 테스트를 만들어줘.

- "김치"로 검색하면 이름에 "김치"가 들어간 음식만 나옴
- 공백 검색어는 빈 목록
```
**결과**: kotlinx-coroutines-test 1.10.2를 추가하고, `fake` 패키지에 메모리 기반 Fake 3개를 만듦. `FoodSearchTest`에 테스트 2개를 작성해 모두 통과.

13. 적응형 화면 뼈대
```
적응형 화면 뼈대를 만들어줘. 필요한 navigation-compose와 material3-adaptive-navigation-suite 의존성도 추가해줘.

- 라우트(`@Serializable`): `MealList`, `Analysis`, `MealInput`
- `NavigationSuiteScaffold`에 "기록"(`MealList`), "분석"(`Analysis`) 탭 2개. 창 크기에 따라 하단 바와 레일이 자동으로 바뀌게
- 그 안에 `NavHost`로 3개 화면을 연결하고, 지금은 화면 이름만 보이는 빈 Composable로
- 입력 화면에서는 탭 바를 숨기고, 상단 앱바의 뒤로가기는 `navigateUp()`으로
- 목록→상세 이동은 다음 단계에서 `ListDetailPaneScaffold`로 만들 거니까 지금은 라우트를 만들지 마
```
**결과**: navigation-compose 2.9.5와 navigation-suite 의존성을 추가함. `NavigationSuiteScaffold`의 "기록"·"분석" 탭과 `NavHost`로 화면 3개를 연결하고, 입력 화면에서는 탭 바를 숨기도록 함. 아이콘은 벡터 drawable로 추가함. 빌드 성공.

14. 식사 목록 ViewModel
```
`MealListViewModel`을 만들어줘. `viewModelScope.launch`로 `MealRepository.getMeals()`와 `FoodRepository.getFoods()`를 불러와 화면 상태를 만들어줘.

- 다시 불러오는 `refresh()` 함수를 만들고, 목록 화면이 다시 보일 때(입력 화면에서 저장하고 돌아왔을 때 포함) 호출되게 해줘
- 불러오다 예외가 나면 `Error` 상태로

- UI 상태는 `sealed interface MealListUiState { Loading, Success, Error }`
- `Success`에는 날짜별 그룹 목록을 담아줘. 그룹마다 날짜, 그날의 총 열량, 끼니 수(`dailySummaries` 활용), 그날의 식사 목록(식사마다 열량 포함)
- `StateFlow`는 읽기 전용으로 공개해줘(`private val _uiState = MutableStateFlow`, 외부에는 `asStateFlow()`)
```
**결과**: `MealListUiState`(Loading/Success/Error)와 날짜별 그룹 모델, `refresh()`로 목록을 불러오는 `MealListViewModel`을 만듦. 목록 화면에서 `viewModel { }` 람다로 생성하고, 화면이 다시 보일 때마다 `LaunchedEffect`로 `refresh()`를 호출함. 빌드 성공.

15. 식사 목록 화면
```
`MealListScreen`을 만들어줘. 목록은 `LazyColumn`으로. 사진 표시에 필요한 coil-compose 의존성도 추가해줘.

- 날짜 그룹마다 `stickyHeader`로 "11월 14일 (금) · 1,850.0 kcal · 3끼" 같은 헤더를 보여줘
- 식사마다 `Card`로 왼쪽에 사진(Coil `AsyncImage`, 정사각형 Crop), 오른쪽에 끼니 종류, 시각, 열량
- `items`에는 `key = { it.id }`를 줘
- 카드를 탭하면 `onMealClick(mealId)` 콜백을 호출해줘(상세 연결은 7단계에서)
- 오른쪽 아래 FAB(+)를 누르면 `MealInput`으로 이동
- 선택된 식사 id를 매개변수로 받아서, 선택된 카드는 강조 색으로 표시해줘(2단 화면용)
- 기록이 없으면 "아직 기록한 식사가 없어요" 안내
- 사진에는 `contentDescription`을 넣어줘(스크린리더용 접근성)
```
**결과**: coil-compose 3.3.0을 추가하고, `LazyColumn`에 날짜별 `stickyHeader`와 식사 `Card`(정사각형 사진·끼니·시각·열량)를 보여 주는 목록 화면을 만듦. 선택 카드 강조, 빈 목록 안내, 사진 `contentDescription`, FAB 이동을 넣음. 빌드 성공.

16. 식사 입력 ViewModel
```
`MealInputViewModel`을 만들어줘. 생성자로 `MealRepository`, `FoodRepository`, `PhotoStorage`를 받아. 입력 상태는 `MealInputUiState` data class 하나로 묶고 `update { copy() }`로 갱신해줘.

- 상태: 날짜, 시각(기본값은 현재), 끼니 종류(선택 전 null), 사진 경로, 담은 음식 목록(음식과 g), 검색어, 검색 결과, 미리보기 영양 합계
- 이벤트 함수: 날짜·시각·끼니 변경, 검색어 변경, 음식 추가(g 입력), 음식 빼기, 사진 선택, 저장
- 사진 선택 `selectPhoto(uri: String)`: `viewModelScope.launch`로 `PhotoStorage.savePhoto(uri)`를 호출해 복사하고, 돌려받은 경로를 상태에 저장. 복사에 실패하면 이유를 상태에 담아줘
- ViewModel이 생성될 때 `viewModelScope.launch`로 `FoodRepository.getFoods()`를 한 번 불러와 음식 목록을 상태로 들고 있고, 검색어가 바뀌면 그 목록을 `searchFoods`로 걸러 검색 결과를 갱신
- 미리보기 영양 합계는 음식 목록이 바뀔 때마다 `NutritionCalculator`로 다시 계산
- g 입력은 `toDoubleOrNull() ?: 0.0`으로 변환
- 저장 조건: 사진, 끼니 종류, 음식 1개 이상. 조건이 안 맞으면 저장하지 않고 이유를 상태에 담아줘
```
**결과**: `MealInputUiState` data class와 `MealInputViewModel`을 만듦. 생성 시 음식 목록을 불러오고, 검색·음식 추가/빼기(같은 음식은 g 합산, 0 이하 거부)·사진 복사·저장(사진·끼니·음식·미래 시각 검사)을 `update { copy() }`로 처리함. 빌드 성공.

17. 식사 입력 화면(날짜·시각·끼니)
```
`MealInputScreen`의 첫 부분을 만들어줘.

- 날짜 버튼을 누르면 Material 3 `DatePicker` 다이얼로그, 시각 버튼을 누르면 `TimePicker` 다이얼로그
- 끼니 종류는 `FilterChip` 4개(조식/중식/석식/간식·음료) 중 하나 선택
- 화면은 상태를 표시하고 이벤트만 ViewModel로 올려줘(단방향 데이터 흐름: 상태는 아래로, 이벤트는 위로)
```
**결과**: 입력 화면에 날짜(`DatePickerDialog`, 미래 날짜 선택 불가)·시각(`TimePicker`, 오전/오후) 버튼과 끼니 `FilterChip` 4개를 만듦. 상태는 `MealInputContent`로 내려 주고 이벤트는 ViewModel 함수로 올리도록 나눔. `DietApp`에서 `MealInputViewModel`을 람다로 생성해 연결함. 빌드 성공.

18. 식사 입력 화면(사진)
```
입력 화면에 사진 영역을 추가해줘.

- Photo Picker(`PickVisualMedia`, 이미지만)로 사진을 고르고, 고른 사진의 Uri를 문자열(`uri.toString()`)로 ViewModel의 `selectPhoto`에 전달. 복사는 ViewModel이 맡으니 화면에서는 하지 마
- 선택한 사진은 Coil `AsyncImage`로 미리보기. 선택 전에는 "사진 추가" 안내 박스
```
**결과**: 입력 화면 맨 위에 4:3 사진 영역을 추가함. 누르면 Photo Picker(이미지만)를 열고 Uri 문자열을 `selectPhoto`로 전달하며, 선택 전에는 "사진 추가" 안내, 선택 후에는 `AsyncImage` 미리보기를 보여 줌. 빌드 성공.

19. 식사 입력 화면(음식 담기·미리보기·저장)
```
입력 화면에 음식 담기 영역을 추가해줘.

- 검색창에 입력하면 아래에 검색 결과 목록이 실시간으로 걸러짐(검색어 상태 + `list.filter`)
- 결과를 누르면 섭취량(g) 입력 다이얼로그(숫자 키보드)가 뜨고, 확인하면 담은 음식 목록에 추가
- 담은 음식은 이름, g, 열량을 보여주고 삭제 버튼을 둠
- 그 아래 "영양소 미리보기" 카드에 탄수화물, 단백질, 지방(g), 열량(kcal)을 소수점 1자리로 표시
- 맨 아래 "저장" 버튼. 저장하면 목록 화면으로 돌아감
- 미리보기 영양소 텍스트와 저장 버튼에 `testTag`를 붙여줘(UI 테스트용)
```
**결과**: 검색창·검색 결과, 섭취량 입력 다이얼로그(숫자 키보드), 담은 음식 목록(삭제 버튼), 영양소 미리보기 카드, 오류 문구, 저장 버튼을 추가함. 저장이 끝나면 목록으로 돌아가고, 미리보기 값 4개와 저장 버튼에 `testTag`를 붙임. 빌드 성공.

20. 식사 입력 화면 적응형 레이아웃
```
입력 화면을 적응형으로 바꿔줘. 창 너비는 `currentWindowAdaptiveInfo()`로 읽어줘.

- 창 너비가 Expanded(840dp) 이상이면(`isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)`) `Row`로 2단: 왼쪽은 사진 미리보기, 날짜·시각·끼니, 오른쪽은 음식 검색과 담은 목록, 영양소 미리보기 카드, 저장 버튼
- 840dp 미만이면 지금처럼 한 줄로 세로 스크롤
- 두 레이아웃이 같은 ViewModel 상태와 하위 Composable을 재사용하게 해줘(중복 코드 없이)
```
**결과**: 기본 정보와 음식·저장 영역을 Composable 묶음으로 한 번만 정의하고, 840dp 이상이면 `Row` 2단, 미만이면 한 줄 세로 스크롤로 배치함. 빌드 성공.

21. 식사 상세 화면
```
`MealDetailViewModel`과 `MealDetailScreen`을 만들어줘. 매개변수로 받은 `mealId`로 `MealRepository`에서 식사를 조회해줘(화면에는 id만 전달하고 데이터는 Repository에서 조회).

- 위에서부터 큰 사진, 날짜와 시각, 끼니 종류
- 먹은 음식마다 이름, 섭취량(g), 탄·단·지, 열량
- 맨 아래 합계 카드: 총 탄수화물, 단백질, 지방, 열량
- 숫자는 소수점 1자리. 없는 id면 "기록을 찾을 수 없어요"
- 상단 앱바의 뒤로가기 아이콘은 한 화면일 때만 보이게 `showBack` 매개변수로 받아줘
```
**결과**: `mealId`로 식사와 음식 정보를 조회해 음식별 영양소·합계를 만드는 `MealDetailViewModel`(Loading/Success/Error)과, 사진·날짜·끼니·음식 목록·합계 카드를 보여 주는 `MealDetailScreen`(`showBack`으로 뒤로가기 표시)을 만듦. 빌드 성공.

22. 기록 탭 목록-상세 2단 화면
```
`ListDetailPaneScaffold`로 기록 탭을 구성해줘. 필요한 material3-adaptive 레이아웃 의존성도 추가해줘.

- `rememberListDetailPaneScaffoldNavigator<String>()`를 쓰고, `listPane`에는 `MealListScreen`, `detailPane`에는 `MealDetailScreen`
- 카드를 누르면 `navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, mealId)`
- 2단으로 바뀌는 기준은 `ListDetailPaneScaffold`의 기본 동작 그대로 둬
- 2단인데 선택한 식사가 없으면 상세 쪽에 "왼쪽에서 식사를 선택하세요"
- 한 화면일 때 시스템 Back을 누르면 상세에서 목록으로 돌아가게 해줘
- 선택된 식사 id는 회전해도 유지되게 해줘
```
**결과**: adaptive-layout·adaptive-navigation 1.2.0을 추가하고, `MealRecordPane`에서 `ListDetailPaneScaffold`로 목록과 상세를 배치함. 카드 선택, 빈 상세 안내, 한 화면일 때 `BackHandler`로 목록 복귀, navigator로 선택 id 유지를 구현함. 저장 후 돌아오면 새 식사를 선택하도록 연결함. 빌드 성공.

23. 분석 ViewModel
```
`AnalysisViewModel`을 만들어줘. `MealAnalyzer`로 최근 30일 데이터를 계산해서 상태로 공개해줘.

- 30일 총 열량, 30일 끼니 수
- 끼니 종류별 탄수화물·단백질·지방(g)과 열량, 그리고 탄·단·지 비율
```
**결과**: `AnalysisUiState`(Loading/Success/Error)와 `AnalysisViewModel`을 만들어 최근 30일 총 열량·끼니 수·끼니 종류별 영양 합계와 비율을 계산함. 비율은 열량 기준(탄·단 4, 지 9kcal/g)으로 계산하는 순수 함수 `macroRatio`를 `NutritionCalculator`에 추가함. 빌드 성공.

24. 분석 화면
```
`AnalysisScreen`을 만들어줘.

- 상단 카드: "최근 30일 총 섭취 열량"을 크게 표시
- 끼니 종류마다 카드 하나: 열량, 그리고 탄수화물·단백질·지방 비율을 `LinearProgressIndicator` 막대 3개로(라벨과 g 함께)
- 맨 아래 작은 글씨로 "영양 데이터 출처: 식품의약품안전처 식품영양성분 데이터베이스"
- 출처 텍스트에 `testTag`를 붙여줘
- 끼니별 카드 4개는 `LazyVerticalGrid`로 배치해서, 창 너비가 840dp 이상이면 2열(`GridCells.Fixed(2)`), 그보다 좁으면 1열이 되게 해줘. 창 너비는 `currentWindowAdaptiveInfo()`로 읽어줘
```
**결과**: `LazyVerticalGrid`(840dp 이상 2열, 미만 1열)에 총 열량 카드(전체 폭), 끼니별 카드 4개(열량, 탄·단·지 g·비율 막대), 출처 문구(전체 폭, `testTag`)를 배치한 분석 화면을 만듦. 빌드 성공.

25. 첫 실행 샘플 데이터
```
앱을 처음 실행할 때만(`meals.json`이 없을 때) 최근 30일치 샘플 식사를 만들어 저장해줘.

- 하루 2~4끼, 끼니 종류와 시각은 그럴듯하게, 음식은 `foods.json`에서 끼니에 어울리게
- 사진은 `res/drawable/sample_meal/`의 이미지를 `filesDir/photos/`로 복사해서 사용
- 샘플 생성 로직은 `data/SampleDataSeeder`로 분리해줘
```
**결과**: 리소스 폴더는 하위 폴더·한글 파일명을 쓸 수 없어 사진 10장을 `assets/sample_meal/`로 옮기고 1280px로 줄임. `SampleDataSeeder`가 `meals.json`이 없을 때만 사진별 음식 구성으로 하루 2~4끼, 30일치 샘플을 만들고, `DietApp`이 샘플 생성 후 화면을 띄우도록 함. 빌드 성공.

26. 자몽색 라이트·다크 테마
```
`ui/theme`의 Material 3 테마를 자몽색 테마로 바꿔줘. `MaterialTheme`의 colorScheme·typography·shapes로 한 곳에서 관리해줘.

- primary는 자몽색(#F2735A 근처)으로 하고, 나머지 색도 자몽 계열 단색 톤으로 맞춰줘
- 라이트용 `lightColorScheme`과 다크용 `darkColorScheme`을 둘 다 만들어줘. 다크는 배경과 surface까지 자몽 계열의 어두운 톤으로, primary는 밝은 자몽색으로
- `isSystemInDarkTheme()`으로 시스템 설정을 따라 전환하고, 다이나믹 컬러는 꺼줘
- 컴포넌트에서는 색을 하드코딩하지 말고 `MaterialTheme.colorScheme`을 참조하게 해줘
- 라이트와 다크 모두 글자와 배경의 대비가 충분한지 확인해줘

- 이미 만든 화면에 하드코딩된 색이 있으면 `MaterialTheme.colorScheme` 참조로 바꿔줘
```
**결과**: 자몽 단색 팔레트로 `lightColorScheme`과 `darkColorScheme`을 만들고, 시스템 다크 모드를 따르며 다이나믹 컬러를 제거함. 주요 글자/배경 조합의 대비를 계산해 모두 4.5:1 이상인지 확인함. `Shape.kt`를 추가하고 화면의 모서리 값을 `MaterialTheme.shapes`로 바꿈. 빌드 성공.

27. ViewModel 단위 테스트
```
ViewModel 단위 테스트를 만들어줘. `runTest`와 `TestDispatcherRule`(`Dispatchers.Main` 교체)을 쓰고, Repository는 Fake를 넣어줘.

- `MealInputViewModel`: 음식을 담으면 미리보기 영양 합계가 계산됨
- `MealInputViewModel`: 사진이 없으면 저장되지 않고 오류 상태가 됨
- `MealListViewModel`: 식사가 있으면 `Success`, 날짜별 그룹으로 나뉨
- `MealListViewModel`: Repository가 예외를 던지면 `Error`
```
**결과**: `Dispatchers.Main`을 바꾸는 `TestDispatcherRule`을 만들고, `FakeMealRepository`에 예외를 던지는 옵션을 추가함. `MealInputViewModelTest` 2개, `MealListViewModelTest` 2개를 작성해 모두 통과.

28. Compose UI 테스트
```
`app/src/androidTest`에 Compose UI 테스트를 만들어줘(`createComposeRule`). 화면에는 Fake Repository를 넣은 ViewModel을 주입해줘.

1. 입력 화면에서 음식을 검색해 200g을 담으면 영양소 미리보기에 계산된 열량이 표시됨
2. 목록 화면에서 식사 카드를 누르면 상세 화면으로 이동해 음식 이름이 보임
3. 분석 화면 하단에 데이터 출처 문구가 표시됨
4. 다크 테마(`darkTheme = true`)로 목록 화면을 띄워도 식사 카드와 날짜 헤더가 표시됨
5. 기록 탭에서 카드를 누르면 기기 크기에 맞게 보이는지 테스트 2개로 나눠줘. 창 크기를 코드로 강제로 바꾸지 말고, 각 테스트는 시작할 때 현재 기기가 대상이 아니면 `assumeTrue`로 건너뛰게 해줘
   - 넓은 화면용(2단이 되는 태블릿 가로 화면): 목록과 상세 내용이 동시에 보임
   - 좁은 화면용(폰 세로): 상세만 보이고 목록은 보이지 않음
```
**결과**: androidTest용 Fake와 UI 테스트 6개(입력 미리보기, 카드→상세, 분석 출처, 다크 테마 목록, 넓은/좁은 화면 기록 탭)를 작성함. 2단 여부는 `ListDetailPaneScaffold` 기본 계산과 같은 `calculatePaneScaffoldDirective`로 판단해 `assumeTrue`로 건너뜀. Pixel 8 에뮬레이터에서 5개 통과, 넓은 화면 테스트 1개는 건너뜀.

### 조가람 (@river040118)
