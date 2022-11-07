package com.maxssoft.wifimaptest.di

import com.maxssoft.wifimaptest.app.WifiMapTestApplication
import com.maxssoft.wifimaptest.di.activity.MapsActivityModule
import com.maxssoft.wifimaptest.di.database.DatabaseModule
import com.maxssoft.wifimaptest.di.platform.PlatformModule
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
    MapsActivityModule::class,
])
interface AppComponent : AndroidInjector<WifiMapTestApplication>