package com.maxssoft.wifimaptest.di.database

import com.maxssoft.wifimaptest.data.database.DatabaseHelper
import com.maxssoft.wifimaptest.data.database.updater.DataFileExtractorImpl
import com.maxssoft.wifimaptest.data.database.updater.DataFileReaderFactory
import com.maxssoft.wifimaptest.data.database.updater.DataFileReaderFactoryImpl
import com.maxssoft.wifimaptest.data.database.updater.DatabaseDataLoaderImpl
import com.maxssoft.wifimaptest.di.ApplicationModule
import com.maxssoft.wifimaptest.domain.database.updater.DataFileExtractor
import com.maxssoft.wifimaptest.domain.database.updater.DatabaseDataLoader
import com.maxssoft.wifimaptest.domain.database.updater.DatabaseUpdater
import com.maxssoft.wifimaptest.domain.database.updater.DatabaseUpdaterImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * Модуль, предоставляющий зависимости для работы с базой данных
 *
 * @author Сидоров Максим on 05.11.2022
 */
@Module(includes = [ApplicationModule::class])
interface DatabaseModule {
    @Binds
    @Singleton
    fun bindDataFileExtractor(implClass: DataFileExtractorImpl): DataFileExtractor

    @Binds
    @Singleton
    fun bindDataFileReaderFactory(implClass: DataFileReaderFactoryImpl): DataFileReaderFactory

    @Binds
    @Singleton
    fun bindDatabaseHelper(implClass: DatabaseHelper): DatabaseHelper

    @Binds
    @Singleton
    fun bindDatabaseDataLoader(implClass: DatabaseDataLoaderImpl): DatabaseDataLoader

    @Binds
    @Singleton
    fun bindDatabaseUpdater(implClass: DatabaseUpdaterImpl): DatabaseUpdater
}