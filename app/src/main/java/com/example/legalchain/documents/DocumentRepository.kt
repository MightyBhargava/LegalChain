package com.example.legalchain.documents

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Simple repository that persists an index of documents to internal storage,
 * and keeps each document's actual file in filesDir.
 */

data class Document(
    val id: String,
    val name: String,
    val type: String,
    val size: String,
    val caseName: String,
    val uploadedAt: String,
    val category: String,
    val fileName: String, // name of the file under context.filesDir
    val tags: List<String> = emptyList() // tags associated with the document
)

object DocumentRepository {

    private const val INDEX_FILE = "documents_index.json"
    private var isInitialized = false

    private val _documents = MutableStateFlow<List<Document>>(emptyList())
    val documents: StateFlow<List<Document>> = _documents

    fun init(context: Context) {
        if (isInitialized) return
        isInitialized = true
        loadFromDisk(context)
    }

    fun addDocuments(newDocs: List<Document>, context: Context) {
        _documents.value = _documents.value + newDocs
        saveToDisk(context)
    }

    /**
     * Update tags for a document.
     */
    fun updateDocumentTags(id: String, tags: List<String>, context: Context) {
        val current = _documents.value
        val doc = current.find { it.id == id } ?: return

        _documents.value = current.map { d ->
            if (d.id == id) {
                d.copy(tags = tags)
            } else {
                d
            }
        }
        saveToDisk(context)
    }

    /**
     * Remove a document and delete the corresponding file from internal storage.
     */
    fun removeDocument(id: String, context: Context) {
        val current = _documents.value
        val doc = current.find { it.id == id } ?: return

        _documents.value = current.filterNot { it.id == id }
        saveToDisk(context)

        val file = File(context.filesDir, doc.fileName)
        if (file.exists()) {
            file.delete()
        }
    }

    private fun loadFromDisk(context: Context) {
        val indexFile = File(context.filesDir, INDEX_FILE)
        if (!indexFile.exists()) {
            _documents.value = emptyList()
            return
        }
        runCatching {
            val text = indexFile.readText()
            val arr = JSONArray(text)
            val loaded = mutableListOf<Document>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val tagsList = mutableListOf<String>()
                if (obj.has("tags")) {
                    val tagsArray = obj.getJSONArray("tags")
                    for (j in 0 until tagsArray.length()) {
                        tagsList.add(tagsArray.getString(j))
                    }
                }
                loaded.add(
                    Document(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        type = obj.getString("type"),
                        size = obj.getString("size"),
                        caseName = obj.getString("caseName"),
                        uploadedAt = obj.getString("uploadedAt"),
                        category = obj.getString("category"),
                        fileName = obj.getString("fileName"),
                        tags = tagsList
                    )
                )
            }
            _documents.value = loaded
        }.onFailure {
            _documents.value = emptyList()
        }
    }

    private fun saveToDisk(context: Context) {
        val arr = JSONArray()
        _documents.value.forEach { doc ->
            val obj = JSONObject()
            obj.put("id", doc.id)
            obj.put("name", doc.name)
            obj.put("type", doc.type)
            obj.put("size", doc.size)
            obj.put("caseName", doc.caseName)
            obj.put("uploadedAt", doc.uploadedAt)
            obj.put("category", doc.category)
            obj.put("fileName", doc.fileName)
            val tagsArray = JSONArray()
            doc.tags.forEach { tag ->
                tagsArray.put(tag)
            }
            obj.put("tags", tagsArray)
            arr.put(obj)
        }
        File(context.filesDir, INDEX_FILE).writeText(arr.toString())
    }
}

