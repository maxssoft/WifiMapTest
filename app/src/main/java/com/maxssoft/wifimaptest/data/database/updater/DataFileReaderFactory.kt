package com.maxssoft.wifimaptest.data.database.updater

import com.maxssoft.wifimaptest.domain.database.updater.DataFileReader
import java.io.File
import javax.inject.Inject

/**
 * Фабрика для создания reader дата файлов
 *
 * @author Сидоров Максим on 07.11.2022
 */
interface DataFileReaderFactory {
    fun dataFileCsvReader(dataFileDirectory: File): DataFileReader
}

class DataFileReaderFactoryImpl @Inject constructor() : DataFileReaderFactory {
    override fun dataFileCsvReader(dataFileDirectory: File) = DataFileCsvReader(dataFileDirectory)
}
