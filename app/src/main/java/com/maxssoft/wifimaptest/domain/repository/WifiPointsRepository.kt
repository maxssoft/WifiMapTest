package com.maxssoft.wifimaptest.domain.repository

import com.maxssoft.wifimaptest.domain.model.MapArea
import com.maxssoft.wifimaptest.domain.model.WifiCluster
import com.maxssoft.wifimaptest.domain.model.WifiPoint

/**
 * Репозиторий для получения списка точек по области координат
 *
 * @author Сидоров Максим on 08.11.2022
 */
interface WifiPointsRepository {

    /**
     * возвращает список wifi точек, находящихся в указанной области [mapArea]
     *
     * @param mapArea область карты
     */
    suspend fun getPoints(mapArea: MapArea): List<WifiPoint>

    /**
     * возвращает список кластеров, находящихся в указанной области [mapArea]
     *
     * @param mapArea область карты
     * @param countSquare количество частей, на которые разбивается карта (countSquare = 10, будет 10 х 10 частей)
     */
    suspend fun getClusters(mapArea: MapArea, countSquare: Int): List<WifiCluster>
}

