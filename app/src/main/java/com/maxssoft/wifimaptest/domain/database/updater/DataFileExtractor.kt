package com.maxssoft.wifimaptest.domain.database.updater

import java.io.File

/**
 * Интерфейс распаковщика транспортного zip-файла с данными о wifi точках
 *
 * Транспортный файл представляет собой zip-архив, лежащий в assets
 *
 * @author Сидоров Максим on 06.11.2022
 */
interface DataFileExtractor {

    /**
     * распаковывает архив во временный файл
     * @return csv файл с данными
     */
    fun extractDataFile(): File
}
