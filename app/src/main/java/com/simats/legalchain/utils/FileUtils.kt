package com.simats.legalchain.utils

import android.content.Context
import android.net.Uri
import java.io.File

fun uriToFile(uri: Uri, context: Context): File {
    val input = context.contentResolver.openInputStream(uri)!!
    val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}")
    file.outputStream().use { input.copyTo(it) }
    return file
}
