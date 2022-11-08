package com.maxssoft.wifimaptest.util

import android.util.Log
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipInputStream

/**
 * Функции для распаковки zip архивов
 *
 * @author Сидоров Максим on 06.11.2022
 */

/**
 * Распаковывает архив в указанную папку
 * @param zipFile файл архива
 * @param destDirectory папка приемник
 * @throws IOException
 */
@Throws(IOException::class)
fun unzip(zipFile: File, destDirectory: File) {
    unzip(FileInputStream(zipFile), destDirectory)
}

/**
 * Распаковывает архив в указанную папку
 * @param zipInputStream поток zip-архива
 * @param destDirectory папка приемник
 * @throws IOException
 */
@Throws(IOException::class)
fun unzip(zipInputStream: InputStream, destDirectory: File) {
    if (!destDirectory.exists()) {
        Log.d("zip", "unzip: destDirectory not exists")
        destDirectory.mkdirs()
    }
    ZipInputStream(zipInputStream).use { zis ->
        var zipEntry = zis.nextEntry
        while (zipEntry != null) {
            Log.d("zip", "name = ${zipEntry.name}, isDirectory = ${zipEntry.isDirectory}")
            val entryFile = File(destDirectory, zipEntry.name)
            if (zipEntry.isDirectory) {
                entryFile.mkdir()
            } else {
                extractFile(zis, entryFile)
            }
            zipEntry = zis.nextEntry
        }
    }
}

@Throws(IOException::class)
fun unzipFile(zipInputStream: InputStream, filename: String, destFile: File) {
    ZipInputStream(zipInputStream).use { zis ->
        var zipEntry = zis.nextEntry
        while (zipEntry != null) {
            Log.d("zip", "name = ${zipEntry.name}, isDirectory = ${zipEntry.isDirectory}")
            if (zipEntry.name.equals(filename)) {
                extractFile(zis, destFile)
            }
            zipEntry = zis.nextEntry
        }
    }
}

@Throws(IOException::class)
private fun extractFile(inputStream: InputStream, destFile: File) {
    Log.d("zip", "extractFile: destFile = $destFile")
    BufferedOutputStream(FileOutputStream(destFile)).use { bos ->
        val bytesIn = ByteArray(BUFFER_SIZE)
        var read = 0
        while (inputStream.read(bytesIn).also { read = it } != -1) {
            bos.write(bytesIn, 0, read)
        }
    }
}

private const val BUFFER_SIZE = 8192
