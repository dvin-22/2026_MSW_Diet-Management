package com.example.diet_management.data

/** 식사 사진 저장소. 테스트에서는 Fake 구현으로 바꿔 끼운다. */
interface PhotoStorage {
    /** [uri](Photo Picker로 받은 사진 Uri 문자열)의 사진을 앱 내부에 복사하고 저장된 파일 경로를 돌려준다. */
    suspend fun savePhoto(uri: String): String
}
