package com.maxssoft.wifimaptest.ui.splash

/**
 * Состяния экрана загрузки [SplashScreenFragment]
 *
 * @author Сидоров Максим on 07.11.2022
 */
sealed class SplashScreenState {

    object None : SplashScreenState()
    object UnpackingDataFile : SplashScreenState()
    data class LoadingData(val percent: Int) : SplashScreenState()
    object Indexing : SplashScreenState()
    data class Error(val exception: Exception) : SplashScreenState()
    object GotoMap : SplashScreenState()
}
