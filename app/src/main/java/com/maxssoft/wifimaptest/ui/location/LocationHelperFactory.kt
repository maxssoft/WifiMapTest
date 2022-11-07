package com.maxssoft.wifimaptest.ui.location

import androidx.appcompat.app.AppCompatActivity
import com.maxssoft.wifimaptest.ui.logger.LoggerFactory
import javax.inject.Inject

/**
 * Фабрика для создания экземпляров [ActivityLocationHelper]
 *
 * @author Сидоров Максим on 05.11.2022
 */
interface LocationHelperFactory {
    fun locationHelper(activity: AppCompatActivity): ActivityLocationHelper
}

class LocationHelperFactoryImpl @Inject constructor(
    private val loggerFactory: LoggerFactory,
) : LocationHelperFactory {
    override fun locationHelper(activity: AppCompatActivity) = ActivityLocationHelperImpl(activity, loggerFactory)
}