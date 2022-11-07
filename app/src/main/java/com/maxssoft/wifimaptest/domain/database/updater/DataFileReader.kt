package com.maxssoft.wifimaptest.domain.database.updater

import com.maxssoft.wifimaptest.domain.model.WifiPoint

/**
 * Интерфейс Reader файла с транспортными данными
 */
interface DataFileReader : AutoCloseable {
    /**
     * количество записей в дата файле
     */
    val count: Int

    /**
     * Возвращает [Sequence] с данными из транспортного файла
     */
    fun useLines(): Sequence<WifiPoint>
}
