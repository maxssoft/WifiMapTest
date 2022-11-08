package com.maxssoft.wifimaptest.util.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.maxssoft.wifimaptest.util.logger.LoggerFactory
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Вспомогательный класс, отвечающий за работу с геолокацией
 *
 * - Проверка и запрос разрешений
 * - Получение текущих координат
 *
 * @author Сидоров Максим on 05.11.2022
 */

interface ActivityLocationHelper {
    /**
     * Флоу, возвращающее изменение геопозиции
     */
    val locationFlow: Flow<Location>

    /**
     * Проверяет наличие разрешений на определение координат
     */
    fun hasPermissions(): Boolean

    /**
     * Проверяет, можно ли запросить разрешения на геолокацию (если пользователь явно не запретил это)
     */
    fun canRequestPermissions(): Boolean

    /**
     * Запрашивает разрешение на геолокацию
     */
    fun requestPermissions()

    /**
     * Запрос геолокации, результат вернется через флоу [locationFlow]
     */
    fun requestLocation()
}

class ActivityLocationHelperImpl(
    private val activity: AppCompatActivity,
    loggerFactory: LoggerFactory
) : ActivityLocationHelper {

    private val logger = loggerFactory.get("ActivityLocationHelper")

    private val _locationFlow = MutableSharedFlow<Location>(
        replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val locationPermissionRequest = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                logger.d { "locationPermissionRequest, ACCESS_FINE_LOCATION granted" }
                requestLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                logger.d { "locationPermissionRequest, ACCESS_COARSE_LOCATION granted" }
                requestLocation()
            } else -> {
            logger.d { "locationPermissionRequest, don't granted permissions" }
            }
        }
    }

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

    override val locationFlow: Flow<Location> = _locationFlow.distinctUntilChanged()

    override fun hasPermissions(): Boolean =
        when {
            ContextCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> true
            ContextCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED -> true
            else -> false
        }.also {
            logger.d { "hasPermissions() == $it" }
        }

    override fun canRequestPermissions(): Boolean =
        when {
            !activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> true
            !activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) -> true
            else -> false
        }.also {
            logger.d { "canRequestPermissions() == $it" }
        }

    override fun requestPermissions() {
        logger.d { "requestPermissions()" }
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    @SuppressLint("MissingPermission")
    override fun requestLocation() {
        logger.d { "requestLocation()" }
        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            logger.d { "requestLocation(), result location = $location" }
            location?.let { _locationFlow.tryEmit(it) }
        }
    }
}

fun Location.toLatLng() = LatLng(latitude, longitude)
