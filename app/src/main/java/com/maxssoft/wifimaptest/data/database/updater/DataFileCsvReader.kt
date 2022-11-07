package com.maxssoft.wifimaptest.data.database.updater

import com.maxssoft.wifimaptest.domain.model.WifiPoint
import com.maxssoft.wifimaptest.domain.database.updater.DataFileReader
import java.io.File

/**
 * reader дата файла на основе CSV
 * Читает данные лениво, через [Sequence]
 *
 * @author Сидоров Максим on 07.11.2022
 */
class DataFileCsvReader(private val dataFileDirectory: File) : DataFileReader {

    private val csv = File(dataFileDirectory, DATA_FILENAME)

    override val count by lazy { csv.useLines { it.count() } }

    override fun useLines(): Sequence<WifiPoint> =
        csv.useLines { linesSequence ->
            linesSequence
                .drop(1) // пропускаем заголовок
                .map { line -> line.split(",") } // преобразуем строку в список полей
                .filter { fields -> fields.find { it.isNullOrBlank() } == null } // пропускаем строки с хотя бы одним пустым полем
                .map { fields ->
                    WifiPoint(
                        id = fields[0].toLong(),
                        pointId = fields[1].toLong(),
                        latitude = fields[2].toDouble(),
                        longitude = fields[3].toDouble()
                    )
                }
        }

    override fun close() {
        dataFileDirectory.deleteRecursively()
    }
}

private const val DATA_FILENAME = "hotspots.csv"
