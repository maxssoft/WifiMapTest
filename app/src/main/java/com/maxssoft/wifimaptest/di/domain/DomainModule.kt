package com.maxssoft.wifimaptest.di.domain

import com.maxssoft.wifimaptest.di.ApplicationModule
import com.maxssoft.wifimaptest.domain.interactor.WifiPointsInteractor
import com.maxssoft.wifimaptest.domain.interactor.WifiPointsInteractorImpl
import dagger.Binds
import dagger.Module

/**
 * Модуль, предоставляющий зависимости для работы с доменным слоем
 *
 * @author Сидоров Максим on 05.11.2022
 */
@Module(includes = [ApplicationModule::class])
interface DomainModule {
    @Binds
    fun bindWifiPointsInteractor(implClass: WifiPointsInteractorImpl): WifiPointsInteractor
}