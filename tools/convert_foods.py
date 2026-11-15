"""식약처 음식 DB 원본(xlsx)에서 선택한 음식만 골라 앱용 foods.json으로 변환한다.

실행 (프로젝트 루트에서):
    python tools/convert_foods.py

필요 패키지: pandas, openpyxl
"""

import json
import re
import sys
from pathlib import Path

import pandas as pd

ROOT = Path(__file__).resolve().parent.parent
RAW_DIR = ROOT / "data" / "raw"
SELECTED_PATH = ROOT / "data" / "selected_foods.txt"
OUTPUT_PATH = ROOT / "app" / "src" / "main" / "assets" / "foods.json"

# 원본 열 이름 → JSON 필드 이름
NUTRIENT_COLUMNS = {
    "kcalPer100g": "에너지(kcal)",
    "carbPer100g": "탄수화물(g)",
    "proteinPer100g": "단백질(g)",
    "fatPer100g": "지방(g)",
}


def read_selected_codes() -> list[str]:
    with open(SELECTED_PATH, encoding="utf-8") as f:
        return [line.strip() for line in f if line.strip()]


def find_raw_file() -> Path:
    files = sorted(RAW_DIR.glob("*.xlsx"))
    if not files:
        sys.exit(f"원본 파일이 없습니다: {RAW_DIR}")
    return files[0]


def base_amount(text: str) -> float:
    """'100g', '100ml' 같은 기준량에서 숫자를 꺼낸다. ml은 1 mL = 1 g으로 본다."""
    match = re.match(r"\s*([\d.]+)\s*(g|ml)\s*$", str(text), re.IGNORECASE)
    if not match:
        raise ValueError(f"기준량을 해석할 수 없습니다: {text}")
    return float(match.group(1))


def to_number(value) -> float | None:
    if pd.isna(value) or str(value).strip() in ("", "-"):
        return None
    return float(str(value).replace(",", ""))


def main() -> None:
    codes = read_selected_codes()
    df = pd.read_excel(find_raw_file(), dtype=str).set_index("식품코드")

    missing_codes = [c for c in codes if c not in df.index]
    if missing_codes:
        sys.exit(f"원본에 없는 식품코드: {missing_codes}")

    foods = []
    for code in codes:
        row = df.loc[code]
        factor = 100.0 / base_amount(row["영양성분함량기준량"])
        food = {
            "id": code,
            "name": row["식품명"],
            "category": row["식품대분류명"],
        }
        empty_fields = []
        for field, column in NUTRIENT_COLUMNS.items():
            value = to_number(row[column])
            if value is None:
                empty_fields.append(column)
                value = 0.0
            food[field] = round(value * factor, 2)
        if empty_fields:
            print(f"[빈 값 → 0] {row['식품명']} ({code}): {', '.join(empty_fields)}")
        foods.append(food)

    OUTPUT_PATH.parent.mkdir(parents=True, exist_ok=True)
    with open(OUTPUT_PATH, "w", encoding="utf-8", newline="\n") as f:
        json.dump(foods, f, ensure_ascii=False, indent=2)
        f.write("\n")
    print(f"{len(foods)}개 음식을 저장했습니다: {OUTPUT_PATH.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
