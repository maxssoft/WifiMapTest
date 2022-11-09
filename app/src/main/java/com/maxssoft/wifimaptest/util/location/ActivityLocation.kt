package com.maxssoft.wifimaptest.util.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

/**
 *  Интерфейс активити, поддерживающей работу с [ActivityLocationHelper]
 *
 * @author Сидоров Максим on 07.11.2022
 */
interface ActivityLocation {

    /**
     * Флоу, возвращающее изменение геопозиции
     */
    val locationFlow: Flow<Location>

    /**
     * Есть разрешение на геолокацию
     */
    val hasPermissions: Boolean

    /**
     * Запрос геолокации, результат вернется через флоу [locationFlow]
     */
    fun requestLocation()
}