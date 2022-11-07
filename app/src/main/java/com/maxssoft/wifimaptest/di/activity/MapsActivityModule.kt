package com.maxssoft.wifimaptest.di.activity

import com.maxssoft.wifimaptest.ui.MapsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 *
 * @author Сидоров Максим on 06.11.2022
 */
@Module
abstract class MapsActivityModule {
    @ContributesAndroidInjector
    abstract fun contributeActivityInjector(): MapsActivity
}