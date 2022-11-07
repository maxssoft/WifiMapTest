package com.maxssoft.wifimaptest.ui

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.maxssoft.wifimaptest.R
import com.maxssoft.wifimaptest.databinding.ActivityMapsBinding
import com.maxssoft.wifimaptest.ui.location.ActivityLocationHelper
import com.maxssoft.wifimaptest.ui.location.LocationHelperFactory
import com.maxssoft.wifimaptest.ui.location.toLatLng
import com.maxssoft.wifimaptest.ui.logger.LoggerFactory
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class MapsActivity : DaggerAppCompatActivity(), OnMapReadyCallback {

    @Inject lateinit var locationHelperFactory: LocationHelperFactory
    @Inject lateinit var loggerFactory: LoggerFactory

    private val logger by lazy { loggerFactory.get("MapsActivity") }
    private val locationHelper: ActivityLocationHelper by lazy { locationHelperFactory.locationHelper(this) }

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        logger.d { "onCreate()" }

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)

        locationHelper.locationFlow
            .onEach { location -> map.moveCamera(CameraUpdateFactory.newLatLng(location.toLatLng())) }
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .launchIn(lifecycleScope)

        checkCurrentLocation()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        logger.d { "onMapReady()" }
        map = googleMap
/*

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
*/
    }

    private fun checkCurrentLocation() {
        logger.d { "checkCurrentLocation()" }
        when {
            locationHelper.hasPermissions() -> locationHelper.requestLocation()
            locationHelper.canRequestPermissions() -> locationHelper.requestPermissions()
        }
    }
}