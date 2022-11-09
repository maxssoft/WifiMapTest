package com.maxssoft.wifimaptest.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxssoft.wifimaptest.domain.database.updater.DatabaseUpdater
import com.maxssoft.wifimaptest.domain.model.DatabaseUpdateState
import com.maxssoft.wifimaptest.util.logger.LoggerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экрана загрузки [SplashScreenFragment]
 *
 * @author Сидоров Максим on 07.11.2022
 */
class SplashScreenViewModel @Inject constructor(
    loggerFactory: LoggerFactory,
    private val databaseUpdater: DatabaseUpdater,
) : ViewModel() {

    private val logger = loggerFactory.get("SplashScreenViewModel")

    private val _screenState = MutableStateFlow<SplashScreenState>(SplashScreenState.None)
    val screenState: StateFlow<SplashScreenState> = _screenState

    /**
     * Событие создания вьюшки
     */
    fun onViewCreated() {
        logger.d { "onViewCreated()" }
        // Запускаем проверку обновления данных только при первом старте вьюшки
        if (_screenState.value == SplashScreenState.None) {
            startCheckUpdate()
        }
    }

    private fun startCheckUpdate() {
        logger.d { "startCheckUpdate()" }
        viewModelScope.launch(Dispatchers.IO) {
            if (databaseUpdater.isHaveUpdates()) {
                logger.d { "databaseUpdater.isHaveUpdates() == true, start database update" }
                // подписываемся на изменение состояния обновления базы данных
                databaseUpdater.updateState
                    .onEach(::handleUpdaterState)
                    .launchIn(this)

                databaseUpdater.updateDatabase()
            } else {
                logger.d { "databaseUpdater.isHaveUpdates() == false" }
                _screenState.value = SplashScreenState.GotoMap
            }
        }
    }

    private fun handleUpdaterState(state: DatabaseUpdateState) {
        _screenState.value = when (state) {
            DatabaseUpdateState.Done -> SplashScreenState.GotoMap
            is DatabaseUpdateState.Loading -> SplashScreenState.LoadingData(state.percent)
            DatabaseUpdateState.Indexing -> SplashScreenState.Indexing
            DatabaseUpdateState.None -> _screenState.value
            DatabaseUpdateState.Unpacking -> SplashScreenState.UnpackingDataFile
            is DatabaseUpdateState.UpdateFailed -> SplashScreenState.Error(state.exception)
        }
    }
}