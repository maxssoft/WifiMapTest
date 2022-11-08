package com.maxssoft.wifimaptest.di

import android.content.Context
import com.maxssoft.wifimaptest.util.logger.LoggerFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Модуль, предоставляющий applicationContext
 *
 * @author Сидоров Максим on 05.11.2022
 */
@Module
class ApplicationModule(context: Context) {

    private val applicationContext = context.applicationContext

    @Provides
    @Singleton
    fun provideApplicationContext() = applicationContext

    @Provides
    @Singleton
    fun provideLoggerFactory() = LoggerFactory()
}