package com.maxssoft.wifimaptest.ui.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.maxssoft.wifimaptest.R
import com.maxssoft.wifimaptest.databinding.FragmentMapBinding
import com.maxssoft.wifimaptest.domain.model.MapArea
import com.maxssoft.wifimaptest.domain.model.WifiPoint
import com.maxssoft.wifimaptest.util.di.ViewModelFactory
import com.maxssoft.wifimaptest.util.di.appComponent
import com.maxssoft.wifimaptest.util.location.ActivityLocation
import com.maxssoft.wifimaptest.util.location.toLatLng
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Фрагмент, отображающий splash экран и процесс подготовки базы данных
 */
class MapFragment : DaggerFragment(), OnMapReadyCallback {

    private val logger by lazy { appComponent().loggerFactory().get("MapFragment") }

    private val viewModel by viewModels<MapViewModel> {
        ViewModelFactory { appComponent().mapViewModel() }
    }

    private lateinit var binding: FragmentMapBinding

    private lateinit var map: GoogleMap
    private lateinit var clusterManager: ClusterManager<WifiPoint>
    private var hasGoogleCluster: Boolean = false

    private val activityLocation: ActivityLocation?
        get() = (requireActivity() as? ActivityLocation)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        logger.d { "onCreateView()" }
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logger.d { "onViewCreated()" }
        super.onViewCreated(view, savedInstanceState)
        logger.d { "getMapAsync()" }
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        supportMapFragment!!.getMapAsync(this)

        viewModel.screenState
            .onEach(::handleScreenState)
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        logger.d { "onMapReady()" }
        map = googleMap
        map.isMyLocationEnabled = activityLocation?.hasPermissions ?: false

        clusterManager = ClusterManager(requireContext(), map)

        map.setOnCameraIdleListener {
            logger.d { "OnCameraIdleListener, zoom = ${map.cameraPosition.zoom}" }
            viewModel.OnCameraIdle(currentMapArea(), map.cameraPosition.zoom, algorithm)
        }
        map.setOnCameraMoveStartedListener {
            logger.d { "setOnCameraMoveStartedListener" }
            viewModel.OnCameraMoveStarted()
        }

        (requireActivity() as? ActivityLocation)?.let { activityLocation ->
            activityLocation.locationFlow
                .onEach { location ->
                    logger.d { "activityLocation.locationFlow.onEach: location = $location" }
                    map.moveCamera(CameraUpdateFactory.newLatLng(location.toLatLng()))
                }
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .launchIn(viewLifecycleOwner.lifecycleScope)
        }

        binding.rgAlgorithm.setOnCheckedChangeListener { group, checkedId ->
            viewModel.OnCameraIdle(currentMapArea(), map.cameraPosition.zoom, algorithm)
        }
    }

    private fun handleScreenState(state: MapScreenState) {
        when(state) {
            MapScreenState.Loading -> {
                logger.d { "handleScreenState(MapScreenState.Loading)" }
                binding.progressBar.isVisible = true
            }
            is MapScreenState.ClustersLoaded -> {
                logger.d { "handleScreenState(MapScreenState.ClustersLoaded)" }
                binding.progressBar.isVisible = false
                clearClusterInfo()
                state.clusters.forEach { cluster ->
                    when {
                        cluster.countPoints > 1 -> {
                            map.addMarker(
                                MarkerOptions().apply {
                                    position(LatLng(cluster.latitude, cluster.longitude))
                                    icon(BitmapDescriptorFactory.fromResource(cluster.countPoints.countToDrawable()))
                                }
                            )
                        }
                        else -> {
                            map.addMarker(
                                MarkerOptions().apply {
                                    position(LatLng(cluster.latitude, cluster.longitude))
                                    icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_wifi_x1))
                                }
                            )
                        }
                    }
                }
                hasGoogleCluster = false
            }
            is MapScreenState.PointsLoaded -> {
                logger.d { "handleScreenState(MapScreenState.PointsLoaded, recordCount = ${state.points.size})" }
                binding.progressBar.isVisible = false
                clearClusterInfo()
                clusterManager.addItems(state.points)
                clusterManager.cluster()
                hasGoogleCluster = true
            }
        }
    }

    private fun clearClusterInfo() {
        if (!hasGoogleCluster) {
             map.clear()
        }
        clusterManager.clearItems()
    }

    private val algorithm: ClusterAlgorithm
    get() = when {
        binding.rbAlgorithmSql.isChecked -> ClusterAlgorithm.Sql()
        binding.rbAlgorithmStandard.isChecked -> ClusterAlgorithm.Google
        else -> ClusterAlgorithm.SqlCombo()
    }

    private fun currentMapArea(): MapArea =
        with(map.projection.visibleRegion) {
            MapArea(
                minLatitude = farLeft.latitude,
                minLongitude = farLeft.longitude,
                maxLatitude = nearRight.latitude,
                maxLongitude = nearRight.longitude // TODO возможно здесь правильней брать farRight.longitude
            )
        }

    private fun requestLocation() {
        logger.d { "requestLocation()" }
        activityLocation?.requestLocation()
    }
}