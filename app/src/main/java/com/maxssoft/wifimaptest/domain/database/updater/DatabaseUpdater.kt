package com.maxssoft.wifimaptest.domain.database.updater

import android.content.Context
import android.content.SharedPreferences
import com.maxssoft.wifimaptest.domain.model.DatabaseUpdateState
import com.maxssoft.wifimaptest.domain.model.WifiPoint
import com.maxssoft.wifimaptest.util.logger.LoggerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Интерфейс класса, обновляющего базу данных
 *
 * При первом старте загружает в базу данных информацию о точках из транспортного zip-архива, лежащего в ресурсах
 * Чтение CSV файла с данными происходит лениво через [Sequence]
 * Вставка данных в базу данных происходит пакетам по 1000 записей
 *
 * TODO лучше бы перенести его в WorkManager, чтобы процесс загрузки не зависел от foreground приложения
 *
 * @author Сидоров Максим on 06.11.2022
 */
interface DatabaseUpdater {
    /**
     * Подписка на состояние процесса обновления
     */
    val updateState: StateFlow<DatabaseUpdateState>

    /**
     * Проверяет что есть обновления
     */
    fun isHaveUpdates(): Boolean

    /**
     * Распаковывает zip архив с данными и загружает их в базу данных
     * Все старые данные при этом удаляются
     *
     * TODO здесь в будующем можно сделать и полноценное обновление из доп.архивов,
     * TODO чтобы данные не перезатирались, а именно обновлялись и добавлялись (но в тестовом задании этого нет)
     */
    fun updateDatabase()
}

class DatabaseUpdaterImpl @Inject constructor(
    loggerFactory: LoggerFactory,
    context: Context,
    private val dataFileExtractor: DataFileExtractor,
    private val databaseDataLoader: DatabaseDataLoader,
) : DatabaseUpdater {

    private val logger = loggerFactory.get("DatabaseUpdater")
    private val preferences = PreferencesDelegate(context)

    private val _updateState = MutableStateFlow<DatabaseUpdateState>(DatabaseUpdateState.None)
    override val updateState: StateFlow<DatabaseUpdateState> = _updateState

    override fun isHaveUpdates(): Boolean = preferences.wasUpdated

    override fun updateDatabase() {
        logger.d { "updateDatabase()" }

        preferences.wasUpdated = false
        _updateState.value = DatabaseUpdateState.Unpacking

        try {
            // распаковываем zip файл из ресурсов и получаем для него reader
            val csv = dataFileExtractor.extractDataFile()
            logger.d { "updateDatabase: zip file extracted" }
            val recordCount = csv.useLines { it.count() }
            var appendedCount = 0

            logger.d { "updateDatabase: zip file contains $recordCount record" }
            _updateState.value = DatabaseUpdateState.Loading(recordCount.percent(appendedCount))

            // пересоздаем таблицу для быстрой очистки данных
            databaseDataLoader.recreateTable()

            // Используем ленивое чтение файла через Sequence
            csv.useLines { linesSequence ->
                // Промежуточный буфер для чтения пакета записей,
                // когда он заполняется, происходит вставка пакета в базу данных
                val packetRecords = mutableListOf<WifiPoint>()

                linesSequence
                    .drop(1) // пропускаем заголовок
                    .map { line ->
                        val fields = line.split(",")
                        val pointId = runCatching { fields[1].toLong() }.getOrNull()
                        val latitude = runCatching { fields[2].toDouble() }.getOrNull()
                        val longitude = runCatching { fields[3].toDouble() }.getOrNull()
                        if (pointId != null && latitude != null && longitude != null) {
                            WifiPoint(
                                pointId = pointId,
                                latitude = latitude,
                                longitude = longitude
                            )
                        } else {
                            null
                        }
                    }
                    .forEach { wifiPoint ->
                        if (wifiPoint != null) {
                            packetRecords.add(wifiPoint)

                            // Вставляем прочитанную порцию строк в базу данных
                            if (packetRecords.size >= PACKET_RECORD_COUNT) {
                                logger.d { "updateDatabase: read $PACKET_RECORD_COUNT records" }
                                appendToDataBase(packetRecords)
                                appendedCount += packetRecords.size
                                logger.d { "updateDatabase: records appended [$appendedCount / $recordCount] to database" }

                                packetRecords.clear()
                                _updateState.value = DatabaseUpdateState.Loading(recordCount.percent(appendedCount))
                            }
                        }
                    }
                appendToDataBase(packetRecords)
            }

            preferences.wasUpdated = true
            // На всякий случай принудитеьно удаляем временный файл с данными
            csv.delete()

            // создаем индексы отдельно, в конце, чтобы они не тормозили вставку данных
            _updateState.value = DatabaseUpdateState.Indexing
            databaseDataLoader.createIndices()

            logger.d { "updateDatabase: update process finished" }
            _updateState.value = DatabaseUpdateState.Done
        } catch (exception: Exception) {
            logger.e(exception) { "updateDatabase: error of update process" }
            _updateState.value = DatabaseUpdateState.UpdateFailed(exception)
        }
    }

    private fun appendToDataBase(records: List<WifiPoint>) {
        logger.d { "appendToDataBase(), ${records.size} records" }
        if (records.isEmpty()) {
            return
        }
        databaseDataLoader.appendRows(records)
        logger.d { "appendToDataBase: ${records.size} records appended" }
    }

    private fun Int.percent(recNo: Int): Int = recNo * 100 / this
}

/**
 * Делегат для чтения/записи настроек [DatabaseUpdater] в [SharedPreferences]
 *
 * @author Сидоров Максим on 07.11.2022
 */
private class PreferencesDelegate(private val context: Context) {
    private val prefs: SharedPreferences get() = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /**
     * Признак, что обновление базы данных было успешно завершено
     */
    var wasUpdated: Boolean
        get() = !prefs.getBoolean(WAS_UPDATED_KEY, false)
        set(value) = with(prefs.edit()) { putBoolean(WAS_UPDATED_KEY, value).apply() }

    companion object {
        private const val PREF_NAME = "DatabaseUpdater"
        private const val WAS_UPDATED_KEY = "was_updated"
    }
}

private const val PACKET_RECORD_COUNT = 1000
