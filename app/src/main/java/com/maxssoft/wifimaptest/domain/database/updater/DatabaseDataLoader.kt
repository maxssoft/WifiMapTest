package com.maxssoft.wifimaptest.domain.database.updater

import com.maxssoft.wifimaptest.domain.model.WifiPoint

/**
 * Интерфейс загрузчика данных о wifi точках в базу данных
 *
 * @author Сидоров Максим on 07.11.2022
 */
interface DatabaseDataLoader {
    /**
     * Пересоздает таблицу с wifi точками
     */
    fun recreateTable()

    /**
     * Вставляет в таблицу wifi точек порцию данных (рекомендуется не более 1000 строк)
     */
    suspend fun appendRows(data: List<WifiPoint>)
}
