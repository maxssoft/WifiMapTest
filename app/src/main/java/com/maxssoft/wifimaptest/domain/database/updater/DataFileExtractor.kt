package com.maxssoft.wifimaptest.domain.database.updater

/**
 * Интерфейс распаковщика транспортного zip-файла с данными о wifi точках
 *
 * Транспортный файл представляет собой zip-архив, лежащий в assets
 * Данные извлекаются во временный файл и читаются пакетами по 100 записей
 *
 * @author Сидоров Максим on 06.11.2022
 */
interface DataFileExtractor {

    /**
     * распаковывает архив во временный файл
     * @return директория, содержащая распакованные файлы zip-архива
     */
    fun extractDataFile(): DataFileReader
}
