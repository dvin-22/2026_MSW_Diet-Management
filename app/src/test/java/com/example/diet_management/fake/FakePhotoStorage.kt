package com.example.diet_management.fake

import com.example.diet_management.data.PhotoStorage

/** 파일을 복사하지 않고 받은 문자열로 가짜 경로를 돌려주는 테스트용 [PhotoStorage]. */
class FakePhotoStorage : PhotoStorage {

    /** 테스트에서 호출 여부를 확인할 수 있게 받은 Uri를 기록한다. */
    val savedUris = mutableListOf<String>()

    override suspend fun savePhoto(uri: String): String {
        savedUris.add(uri)
        return "fake/photos/$uri"
    }
}
