package com.maxssoft.wifimaptest.util

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
        destDirectory.mkdirs()
    }
    ZipInputStream(zipInputStream).use { zis ->
        var zipEntry = zis.nextEntry
        while (zipEntry != null) {
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
private fun extractFile(inputStream: InputStream, destFile: File) {
    BufferedOutputStream(FileOutputStream(destFile)).use { bos ->
        val bytesIn = ByteArray(BUFFER_SIZE)
        var read: Int
        while (inputStream.read(bytesIn).also { read = it } != -1) {
            bos.write(bytesIn, 0, read)
        }
    }
}

private const val BUFFER_SIZE = 8192
