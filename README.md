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

### 조가람 (@river040118)
