package com.maxssoft.wifimaptest.ui

import android.location.Location
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.maxssoft.wifimaptest.R
import com.maxssoft.wifimaptest.databinding.ActivityMainBinding
import com.maxssoft.wifimaptest.util.di.appComponent
import com.maxssoft.wifimaptest.util.location.ActivityLocation
import com.maxssoft.wifimaptest.util.location.ActivityLocationHelper
import com.maxssoft.wifimaptest.util.location.LocationHelperFactory
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), ActivityLocation {

    @Inject
    lateinit var locationHelperFactory: LocationHelperFactory

    private val logger by lazy { appComponent().loggerFactory().get("MapsActivity") }
    private val locationHelper: ActivityLocationHelper by lazy { locationHelperFactory.locationHelper(this) }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        logger.d { "onCreate()" }

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        findNavController(R.id.nav_host_fragment_content_main).run {
            appBarConfiguration = AppBarConfiguration(graph)
            setupActionBarWithNavController(this@run, appBarConfiguration)
        }

        checkCurrentLocation()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment_content_main).navigateUp(appBarConfiguration) ||
            super.onSupportNavigateUp()
    }

    private fun checkCurrentLocation() {
        logger.d { "checkCurrentLocation()" }
        when {
            locationHelper.hasPermissions() -> locationHelper.requestLocation()
            locationHelper.canRequestPermissions() -> locationHelper.requestPermissions()
        }
    }

    override val locationFlow: Flow<Location> get() = locationHelper.locationFlow

    override fun requestLocation() {
        locationHelper.requestLocation()
    }
}