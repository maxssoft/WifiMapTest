package com.maxssoft.wifimaptest.ui.error

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.maxssoft.wifimaptest.databinding.FragmentErrorBinding
import com.maxssoft.wifimaptest.util.di.appComponent
import com.maxssoft.wifimaptest.util.logger.Logger
import com.maxssoft.wifimaptest.util.logger.LoggerFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

/**
 * Фрагмент, отображающий ошибку
 */
class ErrorFragment : DaggerFragment() {

    private val logger by lazy { appComponent().loggerFactory().get("ErrorFragment") }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentErrorBinding.inflate(inflater, container, false).initView().root
    }

    private fun FragmentErrorBinding.initView(): FragmentErrorBinding {
        val errorMessage = arguments?.getString(ERROR_TEXT_ARG)
        logger.d { "initView(): errorMessage = $errorMessage" }

        detailsButton.setOnClickListener { errorText.isVisible = true }
        errorText.text = errorMessage
        return this
    }

    companion object {
        const val ERROR_TEXT_ARG = "errorText"
    }
}