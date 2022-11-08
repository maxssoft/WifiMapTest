package com.maxssoft.wifimaptest.domain.model

import java.lang.Exception

/**
 * Состояние обновления базы данных
 *
 * @author Сидоров Максим on 06.11.2022
 */
sealed class DatabaseUpdateState {

    /**
     * Начальный статус, обновление не запущено
     */
    object None : DatabaseUpdateState()

    /**
     * Распаковка данных
     */
    object Unpacking : DatabaseUpdateState()

    /**
     * Загрузка данных в базу данных
     * @property percent процент загруженных данных
     */
    data class Loading(val percent: Int) : DatabaseUpdateState()

    /**
     * Индексация даных
     */
    object Indexing : DatabaseUpdateState()

    /**
     * Обновление завершено успешно
     */
    object Done : DatabaseUpdateState()

    /**
     * Ошибка обновления
     * @property exception ошибка, которая привела к сбою обновления данных
     */
    data class UpdateFailed(val exception: Exception) : DatabaseUpdateState()
}