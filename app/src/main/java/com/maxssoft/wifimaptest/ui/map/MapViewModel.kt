package com.maxssoft.wifimaptest.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxssoft.wifimaptest.domain.interactor.WifiPointsInteractor
import com.maxssoft.wifimaptest.domain.model.MapArea
import com.maxssoft.wifimaptest.util.logger.LoggerFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экрана с картой [MapFragment]
 *
 * @author Сидоров Максим on 07.11.2022
 */
class MapViewModel @Inject constructor(
    loggerFactory: LoggerFactory,
    private val wifiPointsInteractor: WifiPointsInteractor,
) : ViewModel() {

    private val logger = loggerFactory.get("MapViewModel")

    private val _screenState = MutableSharedFlow<MapScreenState>(
        replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val screenState: SharedFlow<MapScreenState> = _screenState

    private var loadJob: Job? = null

    /**
     * View нужно загрузить новые данные по точкам
     * Результат возвращается через подписку на [screenState]
     *
     * @param mapArea видимая область карты
     * @param zoom параметр зума
     * @param algorithm способ загрузки и отображения кластеров [ClusterAlgorithm]
     */
    fun OnCameraIdle(mapArea: MapArea, zoom: Float, algorithm: ClusterAlgorithm) {
        logger.d { "onNeedRefreshData(mapArea = $mapArea, zoom = $zoom, algorithm = $algorithm)" }

        loadJob?.cancel()
        _screenState.tryEmit(MapScreenState.Loading)

        loadJob = viewModelScope.launch {
            // делаем задержку загрузки, чтобы не нагружать базу лишними запросами, пока пользователь перемещается по карте
            delay(400)

            val newState = when (algorithm) {
                is ClusterAlgorithm.Sql -> MapScreenState.ClustersLoaded(
                    wifiPointsInteractor.getClusters(mapArea, algorithm.countSquare)
                )
                is ClusterAlgorithm.SqlCombo -> {
                    if (zoom > algorithm.googleActivateZoom) {
                        MapScreenState.PointsLoaded(
                            wifiPointsInteractor.getPoints(mapArea)
                        )
                    } else {
                        MapScreenState.ClustersLoaded(
                            wifiPointsInteractor.getClusters(mapArea, algorithm.countSquare)
                        )
                    }
                }
                ClusterAlgorithm.Google -> MapScreenState.PointsLoaded(
                    wifiPointsInteractor.getPoints(mapArea)
                )
            }
            if (isActive) {
                _screenState.tryEmit(newState)
            }
        }
    }

    fun OnCameraMoveStarted() {
        loadJob?.cancel()
        loadJob = null
    }
}