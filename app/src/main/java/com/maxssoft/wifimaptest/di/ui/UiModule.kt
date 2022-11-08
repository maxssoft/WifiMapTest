package com.maxssoft.wifimaptest.di.ui

import com.maxssoft.wifimaptest.ui.MainActivity
import com.maxssoft.wifimaptest.ui.error.ErrorFragment
import com.maxssoft.wifimaptest.ui.map.MapFragment
import com.maxssoft.wifimaptest.ui.splash.SplashScreenFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 *
 * @author Сидоров Максим on 06.11.2022
 */
@Module
abstract class UiModule {
    @ContributesAndroidInjector
    abstract fun contributeMainActivityInjector(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributeMapFragmentInjector(): MapFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorFragmentInjector(): ErrorFragment

    @ContributesAndroidInjector
    abstract fun contributeSplashFragmentInjector(): SplashScreenFragment
}