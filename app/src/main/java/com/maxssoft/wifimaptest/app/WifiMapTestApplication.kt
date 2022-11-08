package com.maxssoft.wifimaptest.app

import android.content.Context
import com.maxssoft.wifimaptest.di.AppComponent
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

    lateinit var appComponent: AppComponent
        private set

    override fun applicationInjector() = AndroidInjector<DaggerApplication> {
        DaggerAppComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
            .also { appComponent = it }
            .inject(this)
    }
}

fun Context.appComponent(): AppComponent = (this as WifiMapTestApplication).appComponent