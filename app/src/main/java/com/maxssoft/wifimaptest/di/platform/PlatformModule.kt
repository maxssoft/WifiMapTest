package com.maxssoft.wifimaptest.di.platform

import com.maxssoft.wifimaptest.di.ApplicationModule
import com.maxssoft.wifimaptest.util.location.LocationHelperFactory
import com.maxssoft.wifimaptest.util.location.LocationHelperFactoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * Модуль, предоставляющий платформенные зависимости
 *
 * @author Сидоров Максим on 05.11.2022
 */
@Module(includes = [ApplicationModule::class])
interface PlatformModule {

    @Binds
    @Singleton
    fun bindLocationHelperFactory(implClass: LocationHelperFactoryImpl): LocationHelperFactory
}