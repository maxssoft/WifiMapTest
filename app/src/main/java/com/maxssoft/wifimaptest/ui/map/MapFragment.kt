package com.maxssoft.wifimaptest.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.maxssoft.wifimaptest.databinding.FragmentMapBinding
import com.maxssoft.wifimaptest.util.di.appComponent
import com.maxssoft.wifimaptest.util.location.ActivityLocation
import com.maxssoft.wifimaptest.util.location.toLatLng
import com.maxssoft.wifimaptest.util.logger.LoggerFactory
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Фрагмент, отображающий splash экран и процесс подготовки базы данных
 */
class MapFragment : DaggerFragment(), OnMapReadyCallback {

    private val logger by lazy { appComponent().loggerFactory().get("MapFragment") }

    private lateinit var binding: FragmentMapBinding

    private lateinit var map: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        logger.d { "onCreateView()" }
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logger.d { "onViewCreated()" }
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        logger.d { "onMapReady()" }
        map = googleMap

        (requireActivity() as? ActivityLocation)?.let { activityLocation ->
            activityLocation.locationFlow
                .onEach { location -> map.moveCamera(CameraUpdateFactory.newLatLng(location.toLatLng())) }
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    private fun requestLocation() {
        (requireActivity() as? ActivityLocation)?.requestLocation()
    }
}