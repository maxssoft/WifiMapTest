package com.maxssoft.wifimaptest.util.di

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.maxssoft.wifimaptest.app.appComponent

/**
 * @author Сидоров Максим on 07.11.2022
 */

@Suppress("UNCHECKED_CAST")
class ViewModelFactory<T: ViewModel>(private val create: () -> T) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return create.invoke() as T
    }
}

fun Activity.appComponent() = applicationContext.appComponent()
fun Fragment.appComponent() = requireContext().applicationContext.appComponent()
