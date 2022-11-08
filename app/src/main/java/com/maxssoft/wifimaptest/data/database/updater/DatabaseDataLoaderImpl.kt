package com.maxssoft.wifimaptest.data.database.updater

import android.database.sqlite.SQLiteStatement
import com.maxssoft.wifimaptest.data.database.DatabaseHelper
import com.maxssoft.wifimaptest.domain.database.updater.DatabaseDataLoader
import com.maxssoft.wifimaptest.domain.model.WifiPoint
import com.maxssoft.wifimaptest.util.logger.LoggerFactory
import javax.inject.Inject

/**
 * Загрузчик данных в базу данных
 * Использует быструю пакетную загрузку данных в рамках одной транзакции
 * Использует прекомпилированную sql команду для исключения потерь на преобразование строк
 */
class DatabaseDataLoaderImpl @Inject constructor(
    loggerFactory: LoggerFactory,
    private val databaseHelper: DatabaseHelper,
) : DatabaseDataLoader {
    private val logger = loggerFactory.get("DatabaseDataLoader")

    override fun recreateTable() {
        databaseHelper.writableDatabase.use { db -> databaseHelper.recreateWifiTable(db) }
    }

    override fun createIndices() {
        databaseHelper.writableDatabase.use { db -> databaseHelper.createWifiTableIndices(db) }
    }

    override fun appendRows(data: List<WifiPoint>) {
        logger.d { "appendRows()" }

        databaseHelper.writableDatabase.use { db ->
            val sql = "insert into ${DatabaseHelper.WIFI_POINTS_TABLE} (" +
                "${DatabaseHelper.WIFI_POINTS_FIELD_POINT_ID}, " +
                "${DatabaseHelper.WIFI_POINTS_FIELD_LATITUDE}, " +
                "${DatabaseHelper.WIFI_POINTS_FIELD_LONGITUDE}" +
                ") values (?, ?, ?);"
            val sqlCommand: SQLiteStatement = db.compileStatement(sql)

            db.beginTransaction()
            logger.d { "appendRows: beginTransaction()" }

            data.forEach { wifiPoint ->
                sqlCommand.bindLong(1, wifiPoint.pointId)
                sqlCommand.bindDouble(2, wifiPoint.latitude)
                sqlCommand.bindDouble(3, wifiPoint.longitude)
                sqlCommand.executeInsert()
                sqlCommand.clearBindings()
            }

            db.setTransactionSuccessful()
            db.endTransaction()
            logger.d { "appendRows: endTransaction()" }
        }
    }
}