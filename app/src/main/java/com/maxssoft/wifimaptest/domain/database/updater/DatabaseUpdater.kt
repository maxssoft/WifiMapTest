package com.maxssoft.wifimaptest.domain.database.updater

import android.content.Context
import android.content.SharedPreferences
import com.maxssoft.wifimaptest.domain.model.DatabaseUpdateState
import com.maxssoft.wifimaptest.domain.model.WifiPoint
import com.maxssoft.wifimaptest.ui.logger.LoggerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

/**
 * Интерфейс класса, обновляющего базу данных
 *
 * При первом старте загружает в базу данных информацию о точках из транспортного zip-архива, лежащего в ресурсах
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
    suspend fun updateDatabase()
}

class DatabaseUpdaterImpl(
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

    override suspend fun updateDatabase() = withContext(Dispatchers.IO) {
        logger.d { "updateDatabase()" }

        preferences.wasUpdated = false
        _updateState.value = DatabaseUpdateState.Unpacking

        try {
            // распаковываем zip файл из ресурсов и получаем для него reader
            dataFileExtractor.extractDataFile().use { reader ->
                logger.d { "updateDatabase: zip file extracted" }
                val recordCount = reader.count
                var appendedCount = 0

                logger.d { "updateDatabase: zip file contains $recordCount record" }
                _updateState.value = DatabaseUpdateState.Loading(recordCount.percent(appendedCount))

                val packetRecords = mutableListOf<WifiPoint>()
                // читаем лениво из Sequence данные по [PACKET_RECORD_COUNT] записей и пакетно вставляем их в базу данных
                reader.useLines().forEach { wifiPoint ->
                    packetRecords.add(wifiPoint)

                    if (packetRecords.size >= PACKET_RECORD_COUNT) {
                        logger.d { "updateDatabase: read $PACKET_RECORD_COUNT records" }
                        appendToDataBase(packetRecords)
                        appendedCount += packetRecords.size
                        logger.d { "updateDatabase: records appended [$appendedCount / $recordCount] to database" }

                        packetRecords.clear()
                        _updateState.value = DatabaseUpdateState.Loading(recordCount.percent(appendedCount))
                    }
                }
                appendToDataBase(packetRecords)

                logger.d { "updateDatabase: update process finished" }
                _updateState.value = DatabaseUpdateState.Done
                preferences.wasUpdated = true
            }
        } catch (exception: Exception) {
            logger.e(exception) { "updateDatabase: error of update process" }
            _updateState.value = DatabaseUpdateState.UpdateFailed(exception)
        }
    }

    private suspend fun appendToDataBase(records: List<WifiPoint>) {
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
