package com.maxssoft.wifimaptest.di.data

import com.maxssoft.wifimaptest.data.repository.WifiPointsRepositoryImpl
import com.maxssoft.wifimaptest.di.ApplicationModule
import com.maxssoft.wifimaptest.domain.repository.WifiPointsRepository
import dagger.Binds
import dagger.Module

/**
 * Модуль, предоставляющий зависимости для репозиториев
 *
 * @author Сидоров Максим on 05.11.2022
 */
@Module(includes = [ApplicationModule::class])
interface RepositoryModule {
    @Binds
    fun bindWifiPointsRepository(implClass: WifiPointsRepositoryImpl): WifiPointsRepository
}