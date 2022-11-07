package com.maxssoft.wifimaptest.app

import com.maxssoft.wifimaptest.di.ApplicationModule
import com.maxssoft.wifimaptest.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

/**
 * Класс приложения для инстанцирования Dagger
 *
 * @author Сидоров Максим on 05.11.2022
 */
class WifiMapTestApplication : DaggerApplication() {

    override fun applicationInjector() = AndroidInjector<DaggerApplication> {
        DaggerAppComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
            .inject(this)
    }
}