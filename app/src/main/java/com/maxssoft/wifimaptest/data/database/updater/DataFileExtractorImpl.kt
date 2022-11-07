package com.maxssoft.wifimaptest.data.database.updater

import android.content.Context
import com.maxssoft.wifimaptest.domain.database.updater.DataFileReader
import com.maxssoft.wifimaptest.domain.database.updater.DataFileExtractor
import com.maxssoft.wifimaptest.ui.logger.LoggerFactory
import com.maxssoft.wifimaptest.util.unzip
import java.io.File
import javax.inject.Inject

/**
 * Распаковщик транспортного файла с данными о wifi точках
 * возвращает ленивый reader дата файла
 *
 * @author Сидоров Максим on 06.11.2022
 */
class DataFileExtractorImpl @Inject constructor(
    private val context: Context,
    loggerFactory: LoggerFactory,
    private val readerFactory: DataFileReaderFactory
) : DataFileExtractor {

    private val logger = loggerFactory.get("DataFileRepository")

    override fun extractDataFile(): DataFileReader {
        logger.d { "extractDataFile()" }
        return readerFactory.dataFileCsvReader(unpackDataFiles())
    }

    private fun unpackDataFiles(): File {
        logger.d { "unpackDataFiles()" }
        return File.createTempFile(ZIP_FILENAME, null).apply {
            mkdir()
            deleteOnExit()
            unzip(context.assets.open(ZIP_FILENAME), this)
        }.also {
            logger.d { "unpackDataFiles(), data file saved to ${it.absolutePath}" }
        }
    }
}

private const val ZIP_FILENAME = "hotspots.csv.zip"
