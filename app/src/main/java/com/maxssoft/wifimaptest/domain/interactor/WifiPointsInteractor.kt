package com.maxssoft.wifimaptest.domain.interactor

import com.maxssoft.wifimaptest.domain.model.MapArea
import com.maxssoft.wifimaptest.domain.model.WifiCluster
import com.maxssoft.wifimaptest.domain.model.WifiPoint
import com.maxssoft.wifimaptest.domain.repository.WifiPointsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Интерактор, возвращающий информацию о Wifi точках для указанной области карты
 *
 *  Умеет возвращать просто список точке, а также возвращать просчитанные кластеры точек
 *
 * @author Сидоров Максим on 08.11.2022
 */
interface WifiPointsInteractor {

    /**
     * возвращает список wifi точек, находящихся в указанной области [mapArea]
     */
    suspend fun getPoints(mapArea: MapArea): List<WifiPoint>

    /**
     * возвращает список кластеров, находящихся в указанной области [mapArea]
     * @param mapArea область карты
     * @param countSquare количество частей, на которые разбивается карта (countSquare = 10, будет 10 х 10 частей)
     */
    suspend fun getClusters(mapArea: MapArea, countSquare: Int): List<WifiCluster>
}

class WifiPointsInteractorImpl @Inject constructor(
    private val wifiPointsRepository: WifiPointsRepository,
) : WifiPointsInteractor {

    override suspend fun getPoints(mapArea: MapArea): List<WifiPoint> =
        withContext(Dispatchers.Default) { wifiPointsRepository.getPoints(mapArea)
    }

    override suspend fun getClusters(mapArea: MapArea, countSquare: Int): List<WifiCluster> =
        withContext(Dispatchers.Default) { wifiPointsRepository.getClusters(mapArea, countSquare)
    }
}
