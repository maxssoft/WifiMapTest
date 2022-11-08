package com.maxssoft.wifimaptest.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.maxssoft.wifimaptest.R
import com.maxssoft.wifimaptest.databinding.FragmentSplashBinding
import com.maxssoft.wifimaptest.ui.error.ErrorFragment
import com.maxssoft.wifimaptest.util.di.ViewModelFactory
import com.maxssoft.wifimaptest.util.di.appComponent
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Фрагмент, отображающий splash экран и процесс подготовки базы данных
 */
class SplashScreenFragment : DaggerFragment() {

    private val logger by lazy { appComponent().loggerFactory().get("SplashFragment") }

    private lateinit var binding: FragmentSplashBinding

    private val viewModel by viewModels<SplashScreenViewModel> {
        ViewModelFactory { appComponent().splashScreenViewModel() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        logger.d { "onCreateView()" }
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.screenState
            .onEach(::handleViewState)
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleViewState(viewState: SplashScreenState) {
        logger.d { "handleViewState(viewState = $viewState)" }
        when(viewState) {
            is SplashScreenState.Error -> {
                val bundle = bundleOf(ErrorFragment.ERROR_TEXT_ARG to viewState.exception.localizedMessage)
                findNavController().navigate(R.id.action_splash_to_error, bundle)
            }
            SplashScreenState.GotoMap -> {
                findNavController().navigate(R.id.action_splash_to_map)
            }
            is SplashScreenState.LoadingData -> {
                showUpdateViews()
                with(binding) {
                    updateSubtitle.setText(R.string.database_update_loading)
                    progressBar.isIndeterminate = false
                    progressBar.progress = viewState.percent
                }
            }
            SplashScreenState.Indexing -> {
                showUpdateViews()
                with(binding) {
                    updateSubtitle.setText(R.string.database_update_indexing)
                    progressBar.isIndeterminate = true
                }
            }
            SplashScreenState.None -> {
                hideUpdateViews()
            }
            SplashScreenState.UnpackingDataFile -> {
                showUpdateViews()
                with(binding) {
                    updateSubtitle.setText(R.string.database_update_unpacking)
                    progressBar.isIndeterminate = true
                }
            }
        }
    }

    private fun showUpdateViews() {
        with(binding) {
            if (!progressBar.isVisible) {
                progressBar.isVisible = true
                updateTitle.isVisible = true
                updateSubtitle.isVisible = true
            }
        }
    }

    private fun hideUpdateViews() {
        with(binding) {
            if (progressBar.isVisible) {
                progressBar.isVisible = false
                updateTitle.isVisible = false
                updateSubtitle.isVisible = false
            }
        }
    }
}