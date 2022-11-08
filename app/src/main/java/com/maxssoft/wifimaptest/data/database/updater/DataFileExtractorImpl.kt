package com.maxssoft.wifimaptest.data.database.updater

import android.content.Context
import com.maxssoft.wifimaptest.domain.database.updater.DataFileExtractor
import com.maxssoft.wifimaptest.util.logger.LoggerFactory
import com.maxssoft.wifimaptest.util.unzipFile
import java.io.File
import javax.inject.Inject

/**
 * Распаковщик транспортного файла с данными о wifi точках
 *
 * @author Сидоров Максим on 06.11.2022
 */
class DataFileExtractorImpl @Inject constructor(
    private val context: Context,
    loggerFactory: LoggerFactory,
) : DataFileExtractor {

    private val logger = loggerFactory.get("DataFileExtractor")

    override fun extractDataFile(): File {
        logger.d { "extractDataFile()" }
        return unpackDataFile()
    }

    private fun unpackDataFile(): File {
        logger.d { "unpackDataFiles()" }
        return File.createTempFile(DATA_FILENAME, null).apply {
            deleteOnExit()
            unzipFile(context.assets.open(ZIP_FILENAME), DATA_FILENAME,this)
        }.also {
            logger.d { "unpackDataFiles(), data file saved to ${it.absolutePath}" }
        }
    }
}

private const val DATA_FILENAME = "hotspots.csv"
private const val ZIP_FILENAME = "hotspots.csv.zip"
