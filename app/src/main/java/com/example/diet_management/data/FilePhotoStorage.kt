package com.example.diet_management.data

import android.content.Context
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.UUID

/** 사진을 `filesDir/photos/` 아래에 고유한 파일명으로 복사하는 [PhotoStorage] 구현. */
class FilePhotoStorage(context: Context) : PhotoStorage {

    private val appContext = context.applicationContext

    override suspend fun savePhoto(uri: String): String = withContext(Dispatchers.IO) {
        val dir = File(appContext.filesDir, DIR_NAME).apply { mkdirs() }
        val target = File(dir, "${UUID.randomUUID()}.jpg")
        val input = appContext.contentResolver.openInputStream(uri.toUri())
            ?: throw IOException("사진을 열 수 없습니다: $uri")
        input.use { source ->
            target.outputStream().use { source.copyTo(it) }
        }
        target.absolutePath
    }

    private companion object {
        const val DIR_NAME = "photos"
    }
}
