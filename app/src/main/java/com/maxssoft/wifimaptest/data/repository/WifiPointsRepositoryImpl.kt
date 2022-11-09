package com.maxssoft.wifimaptest.data.repository

import com.maxssoft.wifimaptest.data.database.DatabaseHelper
import com.maxssoft.wifimaptest.domain.model.MapArea
import com.maxssoft.wifimaptest.domain.model.WifiCluster
import com.maxssoft.wifimaptest.domain.model.WifiPoint
import com.maxssoft.wifimaptest.domain.model.getLatitudeStep
import com.maxssoft.wifimaptest.domain.model.getLongitudeStep
import com.maxssoft.wifimaptest.domain.repository.WifiPointsRepository
import com.maxssoft.wifimaptest.util.logger.LoggerFactory
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import java.util.Locale
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

/**
 * Репозиторий, возвращающий данные о wifi точках
 *
 * @author Сидоров Максим on 07.11.2022
 */
class WifiPointsRepositoryImpl @Inject constructor(
    loggerFactory: LoggerFactory,
    private val databaseHelper: DatabaseHelper,
) : WifiPointsRepository {

    private val logger = loggerFactory.get("WifiPointsRepository")

    override suspend fun getPoints(mapArea: MapArea): List<WifiPoint> {
        logger.d { "getPoints(mapArea = $mapArea)" }

        val records = mutableListOf<WifiPoint>()
        val sql = mapArea.getPointsSql()
        val db = databaseHelper.readableDatabase
        db.rawQuery(sql, arrayOf()).use { cursor ->
            logger.d { "getPoints: rawQuery executed" }
            var hasData = cursor.moveToFirst()
            while (hasData && currentCoroutineContext().isActive) {
                records.add(
                    WifiPoint(
                        pointId = cursor.getLong(0),
                        longitude = cursor.getDouble(1),
                        latitude = cursor.getDouble(2)
                    )
                )

                hasData = cursor.moveToNext()
            }
            logger.d { "getPoints: all data [${records.size}] fetched" }
        }
        return records
    }

    /**
     * Загружает для переданной области карты [mapArea] список кластеров [WifiCluster]
     *
     * Принцип вычисления кластеров следущий:
     * Разбиваем область на 64 квадраты (8 * 8) и формируем отдельные запросы по каждому квадрату,
     * Для каждого запроса считаем количество точек в квадрате и среднее положение по ширине, долготе
     * Запросы объединяем через UNION и выполняем одним общим запросом к базе данных
     */
    override suspend fun getClusters(mapArea: MapArea, countSquare: Int): List<WifiCluster> {
        logger.d { "getClusters(mapArea = $mapArea)" }
        val queries = mutableListOf<String>()

        val latitudeStep = mapArea.getLatitudeStep(countSquare)
        val longitudeStep = mapArea.getLongitudeStep(countSquare)
        var latitude: Double = mapArea.minLatitude
        (1..countSquare).forEach { latitudeStepIndex ->
            var longitude: Double = mapArea.minLongitude
            (1..countSquare).forEach { longitudeStepIndex ->
                val areaLongitudeStep = MapArea(
                    minLatitude = latitude, minLongitude = longitude,
                    maxLatitude = latitude + latitudeStep, maxLongitude = longitude + longitudeStep
                )
                queries.add(areaLongitudeStep.getAreaClusterSql())
                longitude += longitudeStep
            }
            latitude += latitudeStep
        }

        val records = mutableListOf<WifiCluster>()
        if (currentCoroutineContext().isActive) {
            val sql = queries.joinToString(separator = "\n UNION ALL \n")
            // logger.d { "getClusters: result sql query \n $sql" }
            val db = databaseHelper.readableDatabase
            db.rawQuery(sql, arrayOf()).use { cursor ->
                logger.d { "getClusters: rawQuery executed" }
                var hasData = cursor.moveToFirst()
                while (hasData && currentCoroutineContext().isActive) {
                    records.add(
                        WifiCluster(
                            countPoints = cursor.getInt(0),
                            longitude = cursor.getDouble(1),
                            latitude = cursor.getDouble(2)
                        )
                    )

                    hasData = cursor.moveToNext()
                }
                logger.d { "getClusters: all data [${records.size}] fetched" }
            }
        }
        return records
    }

    /**
     * Возвращает SQL запрос, возвращающий точки, попадающие в [MapArea]
     */
    private fun MapArea.getPointsSql(): String =
        "select ${DatabaseHelper.WIFI_POINTS_FIELD_POINT_ID},  " +
            "${DatabaseHelper.WIFI_POINTS_FIELD_LONGITUDE}, " +
            "${DatabaseHelper.WIFI_POINTS_FIELD_LATITUDE} \n" +
            "from ${DatabaseHelper.WIFI_POINTS_TABLE} \n" +
            "where ${DatabaseHelper.WIFI_POINTS_FIELD_LONGITUDE} " +
            "between ${min(minLongitude, maxLongitude).toSqlValue()} and ${max(minLongitude, maxLongitude).toSqlValue()} \n" +
            "and  ${DatabaseHelper.WIFI_POINTS_FIELD_LATITUDE} " +
            "between ${min(minLatitude, maxLatitude).toSqlValue()} and ${max(minLatitude, maxLatitude).toSqlValue()} \n"

    /**
     * Возвращает SQL запрос, возвращающий информацию по кластеру точек, попадающих в [MapArea]
     * Возвращает количесто точек и среднюю AVG геопозицию точек в кластере
     */
    private fun MapArea.getAreaClusterSql(): String =
        "select count(*), " +
            "avg(${DatabaseHelper.WIFI_POINTS_FIELD_LONGITUDE}) as avg_longitude, " +
            "avg(${DatabaseHelper.WIFI_POINTS_FIELD_LATITUDE}) as avg_latitude \n" +
            "from ${DatabaseHelper.WIFI_POINTS_TABLE} \n" +
            "where ${DatabaseHelper.WIFI_POINTS_FIELD_LONGITUDE} " +
            "between ${min(minLongitude, maxLongitude).toSqlValue()} and ${max(minLongitude, maxLongitude).toSqlValue()} \n" +
            "and  ${DatabaseHelper.WIFI_POINTS_FIELD_LATITUDE} " +
            "between ${min(minLatitude, maxLatitude).toSqlValue()} and ${max(minLatitude, maxLatitude).toSqlValue()} \n"

    private fun Double.toSqlValue(): String = "%.${6}f".format(Locale.ENGLISH,this)
}
