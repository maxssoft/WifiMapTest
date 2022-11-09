package com.maxssoft.wifimaptest.domain.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/**
 * Модель, содержащая информацию о Wifi точке
 *
 * @author Сидоров Максим on 06.11.2022
 */
data class WifiPoint(val pointId: Long, val latitude: Double, val longitude: Double) : ClusterItem {
    override fun getPosition(): LatLng = LatLng(latitude, longitude)

    override fun getTitle(): String? = null

    override fun getSnippet(): String? = null
}
