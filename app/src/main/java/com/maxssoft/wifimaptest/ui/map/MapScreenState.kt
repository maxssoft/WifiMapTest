package com.maxssoft.wifimaptest.ui.map

import com.maxssoft.wifimaptest.domain.model.WifiCluster
import com.maxssoft.wifimaptest.domain.model.WifiPoint

/**
 * Сосотояния экрана с картами
 *
 * @author Сидоров Максим on 09.11.2022
 */
sealed class MapScreenState {
    object Loading : MapScreenState()
    data class ClustersLoaded(val clusters: List<WifiCluster>) : MapScreenState()
    data class PointsLoaded(val points: List<WifiPoint>) : MapScreenState()
}
