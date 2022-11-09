package com.maxssoft.wifimaptest.di

import com.maxssoft.wifimaptest.app.WifiMapTestApplication
import com.maxssoft.wifimaptest.di.ui.UiModule
import com.maxssoft.wifimaptest.di.data.DatabaseModule
import com.maxssoft.wifimaptest.di.data.RepositoryModule
import com.maxssoft.wifimaptest.di.domain.DomainModule
import com.maxssoft.wifimaptest.di.platform.PlatformModule
import com.maxssoft.wifimaptest.di.viewmodel.ViewModelModule
import com.maxssoft.wifimaptest.ui.map.MapViewModel
import com.maxssoft.wifimaptest.ui.splash.SplashScreenViewModel
import com.maxssoft.wifimaptest.util.logger.LoggerFactory
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Dagger компонент приложения
 *
 * @author Сидоров Максим on 05.11.2022
 */
@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    AndroidSupportInjectionModule::class,
    ApplicationModule::class,
    PlatformModule::class,
    DatabaseModule::class,
    RepositoryModule::class,
    DomainModule::class,
    UiModule::class,
    ViewModelModule::class,
])
interface AppComponent : AndroidInjector<WifiMapTestApplication> {

    fun loggerFactory(): LoggerFactory

    fun splashScreenViewModel(): SplashScreenViewModel
    fun mapViewModel(): MapViewModel
}