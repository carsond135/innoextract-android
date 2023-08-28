package uk.co.armedpineapple.innoextract.service

import com.lazygeniouz.dfc.file.DocumentFileCompat
import java.io.IOException

class DocumentFileCache(private val rootDocument: DocumentFileCompat) {
    private val cache: MutableMap<String, DocumentFileCompat> = mutableMapOf()

    fun getDirectory(path: String): DocumentFileCompat {
        val cachedDir = cache[path]
        return if (cachedDir != null && cachedDir.exists() && cachedDir.isDirectory()) {
            cachedDir
        } else {
            val directory = createOrResolveDirectory(path)
            cache[path] = directory
            directory
        }
    }

    private fun createOrResolveDirectory(path: String): DocumentFileCompat {
        val pathComponents = path.split("/").filterNot { it.isEmpty() }
        var documentDir = rootDocument

        pathComponents.forEach { dirComponent ->
            val cachedDir = cache[dirComponent]
            if (cachedDir != null && cachedDir.exists() && cachedDir.isDirectory()) {
                documentDir = cachedDir
            } else {
                val newDir = documentDir.listFiles()
                    .firstOrNull { it.isDirectory() && it.name == dirComponent }

                documentDir = newDir ?: documentDir.createDirectory(dirComponent)
                        ?: throw IOException("Could not create directory.")

                cache[dirComponent] = documentDir
            }
        }

        return documentDir
    }

    fun clearCache() {
        cache.clear()
    }
}