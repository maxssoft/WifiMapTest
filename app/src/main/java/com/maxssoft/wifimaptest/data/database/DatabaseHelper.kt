package com.maxssoft.wifimaptest.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.maxssoft.wifimaptest.util.logger.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Хелпер для создания и обновления внутренней базы данных
 *
 * Голый SQLite используется для скорости. Таблица у нас одна и простая, а вот данных очень много
 *
 * @author Сидоров Максим on 07.11.2022
 */
@Singleton
class DatabaseHelper @Inject constructor(
    context: Context,
    loggerFactory: LoggerFactory,
) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    private val logger = loggerFactory.get("DatabaseHelper")

    override fun onCreate(db: SQLiteDatabase) {
        logger.d { "onCreate()" }
        recreateWifiTable(db)
        createWifiTableIndices(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        logger.d { "onUpgrade(oldVersion = $oldVersion, newVersion = $newVersion)" }
        recreateWifiTable(db)
        createWifiTableIndices(db)
    }

    fun recreateWifiTable(db: SQLiteDatabase) {
        logger.d { "recreateWifiTable()" }
        db.execSQL("drop table if exists $WIFI_POINTS_TABLE;")
        logger.d { "table $WIFI_POINTS_TABLE dropped" }

        db.execSQL(
            "create table $WIFI_POINTS_TABLE (" + "\n" +
                "$WIFI_POINTS_FIELD_ID integer primary key autoincrement," + "\n" +
                "$WIFI_POINTS_FIELD_POINT_ID integer," + "\n" +
                "$WIFI_POINTS_FIELD_LATITUDE real," + "\n" +
                "$WIFI_POINTS_FIELD_LONGITUDE real" + "\n" +
                ");"
        )
        logger.d { "table $WIFI_POINTS_TABLE created" }
    }

    fun createWifiTableIndices(db: SQLiteDatabase) {
        logger.d { "createWifiTableIndices()" }
        db.execSQL("create index location_idx on $WIFI_POINTS_TABLE($WIFI_POINTS_FIELD_LATITUDE, $WIFI_POINTS_FIELD_LONGITUDE);")
        logger.d { "createWifiTableIndices(): indices created" }
    }

    companion object {
        private const val DATABASE_NAME = "wifiDatabase"

        const val WIFI_POINTS_TABLE = "wifi_points"
        const val WIFI_POINTS_FIELD_ID = "id"
        const val WIFI_POINTS_FIELD_POINT_ID = "pointId"
        const val WIFI_POINTS_FIELD_LATITUDE = "latitude"
        const val WIFI_POINTS_FIELD_LONGITUDE = "longitude"
    }
}

