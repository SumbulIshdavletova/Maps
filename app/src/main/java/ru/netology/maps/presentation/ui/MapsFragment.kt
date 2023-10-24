package ru.netology.maps.presentation.ui

import android.Manifest
import android.Manifest.permission
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import com.yandex.mapkit.location.Location as YLocation
import com.yandex.mapkit.location.LocationListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.map.Map as YMap
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.ui_view.ViewProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.maps.R
import ru.netology.maps.data.dto.Location
import ru.netology.maps.databinding.FragmentMapsBinding
import ru.netology.maps.presentation.ui.LocationsListFragment.Companion.textArgLatitude
import ru.netology.maps.presentation.ui.LocationsListFragment.Companion.textArgLongitude
import ru.netology.maps.presentation.viewModel.LocationViewModel


@AndroidEntryPoint
class MapsFragment : Fragment(R.layout.fragment_maps), GeoObjectTapListener, InputListener {

    private lateinit var binding: FragmentMapsBinding
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(context)
        binding = FragmentMapsBinding.inflate(layoutInflater)
        mapView = binding.mapview
    }

    private var client: FusedLocationProviderClient? =
        context?.let { LocationServices.getFusedLocationProviderClient(it) }

    private var myLatitude = 0.0
    private var tvLongitude = 0.0


    private val viewModel: LocationViewModel by activityViewModels()

    companion object {
        var point = Point(0.0, 0.0)
    }

    object MapKitInitializer {
        private var initialized = false
        fun initialize(apiKey: String, context: Context) {
            if (initialized) {
                return
            }

            MapKitFactory.setApiKey(apiKey)
            MapKitFactory.initialize(context)
            initialized = true
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //  MapKitInitializer.initialize("8174a68d-2910-48fc-b27a-abea5746916b", requireContext())

        val binding = FragmentMapsBinding.inflate(
            inflater, container, false
        )

        var mapkit: MapKit = MapKitFactory.getInstance()

        viewModel.data.observe(viewLifecycleOwner) { locations ->
            renderModel(locations)
        }
        //   initListeners()
        requestLocationPermission()

        if (arguments?.isEmpty != true) {
            zoomInSavedLocation()
        } else {
            getCurrentLocation()
        }

        binding.mapview.map?.addTapListener(this)
        binding.mapview.map?.addInputListener(this)

        //  fun initListeners() {
        with(binding) {
            allLocations.setOnClickListener {
                findNavController().navigate(R.id.action_mapsFragment_to_locationsListFragment)
            }
            addLocation.setOnClickListener {
                val dialog = TitleDialog(latitude = point.latitude, longitude = point.longitude)
                dialog.show(childFragmentManager, "dialog")
                createNewLocationMark(point.latitude, point.longitude)
            }

        }
        return binding.root
    }

    private fun renderModel(locations: List<Location>) {
        drawLocationMarks(locations)
    }

    private fun zoomInSavedLocation() {
        val longitudeBundle = arguments?.textArgLongitude?.toDouble()
        val latitudeBundle = arguments?.textArgLatitude?.toDouble()

        if (longitudeBundle != null && latitudeBundle != null) {
            mapView.map?.move(
                CameraPosition(Point(latitudeBundle, longitudeBundle), 17.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 8F),
                null
            )
        }
    }

    private fun zoomInMyLocation(latitude: Double, longitude: Double) {
        mapView.map?.move(
            CameraPosition(Point(latitude, longitude), 17.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 8F),
            null
        )
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                requireActivity(),
                permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            requestPermissions(
                arrayOf(
                    permission.ACCESS_FINE_LOCATION,
                    permission.ACCESS_COARSE_LOCATION
                ),
                100
            )
        }
    }

    private fun getCurrentLocation() {
        var myPosition: com.yandex.mapkit.location.Location? = null
        val locationManager = MapKitFactory.getInstance().createLocationManager()
        locationManager.requestSingleUpdate(object : LocationListener {
            override fun onLocationUpdated(p0: com.yandex.mapkit.location.Location) {
                zoomInMyLocation(p0.position.latitude, p0.position.longitude)
                myPosition = p0
            }

            override fun onLocationStatusUpdated(p0: LocationStatus) {
                if (myPosition != null) {
                    myPosition?.position?.let {
                        zoomInMyLocation(it.latitude, myPosition?.position!!.longitude)
                    }
                }
            }
        })
    }

//            //  activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//            || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//            )
//            {// When location service is enabled, Get last location
//                if (context?.let {
//                        ActivityCompat.checkSelfPermission(it, permission.ACCESS_FINE_LOCATION)
//                    } != PackageManager.PERMISSION_GRANTED && context?.let {
//                        ActivityCompat.checkSelfPermission(it, permission.ACCESS_COARSE_LOCATION)
//                    } != PackageManager.PERMISSION_GRANTED
//                ) {
//                    requestLocationPermission()
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission.
//                    return
//                }
//                client?.lastLocation?.addOnCompleteListener { task ->
//                    // Initialize location
//                    val location = task.result
//                    // Check condition
//                    if (location != null) {
//                        // When location result is not null set latitude
//                        getMyLocationMark(location.latitude, location.longitude)
//                    } else {
//                        // When location result is null, initialize location request
//                        val locationRequest: LocationRequest = LocationRequest()
//                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                            .setInterval(10000)
//                            .setFastestInterval(1000)
//                            .setNumUpdates(1)
//
//                        // Initialize location call back
//                        val locationCallback: LocationCallback = object : LocationCallback() {
//                            override fun onLocationResult(
//                                locationResult: LocationResult
//                            ) {// Initialize location
//                                val location1: android.location.Location? =
//                                    locationResult.lastLocation
//                                // Set latitude
//                                if (location1 != null) {
//                                    myLatitude =
//                                        java.lang.String.valueOf(location1.latitude).toDouble()
//                                }
//                                // Set longitude
//                                if (location1 != null) {
//                                    tvLongitude =
//                                        java.lang.String.valueOf(location1.longitude).toDouble()
//                                }
//                                if (location1 != null) {
//                                    getMyLocationMark(location1.latitude, location1.longitude)
//                                }
//                            }
//                        }
//                        // Request location updates
//                        client?.requestLocationUpdates(
//                            locationRequest,
//                            locationCallback,
//                            Looper.myLooper()
//                        )
//                    }
//                }
//            } else
//            { // When location service is not enabled open location setting
//                startActivity(
//                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                )
//            }
//        }
//    }

    override fun onObjectTap(geoObjectTapEvent: GeoObjectTapEvent): Boolean {
        val selectionMetadata = geoObjectTapEvent
            .geoObject
            .metadataContainer
            .getItem(GeoObjectSelectionMetadata::class.java)
        if (selectionMetadata != null) {
            mapView.map?.selectGeoObject(selectionMetadata.id, selectionMetadata.layerId)
        }
        return selectionMetadata != null
    }

    override fun onMapTap(maps: YMap, point: Point) {
        mapView.map?.deselectGeoObject()
    }

    override fun onMapLongTap(maps: YMap, longTapPoint: Point) {
        val text = mapView.map?.deselectGeoObject()
        //  drawMyLocationMark(longTapPoint.latitude, longTapPoint.longitude)
        point = Point(longTapPoint.latitude, longTapPoint.longitude)
    }

    private fun getMyLocationMark(latitude: Double, longitude: Double) {
        val view = View(requireContext()).apply {
            background = requireContext().getDrawable(R.drawable.blue_location_dot)
        }

        mapView.map?.mapObjects?.addPlacemark(
            Point(myLatitude, tvLongitude),
            ViewProvider(view)
        )
        mapView.map?.move(
            CameraPosition(Point(latitude, longitude), 17.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 8F),
            null
        )
    }

    private fun createNewLocationMark(latitude: Double, longitude: Double) {
        val view = View(requireContext()).apply {
            background = requireContext().getDrawable(R.drawable.ic_baseline_adjust_24)
        }

        mapView.map?.mapObjects?.addPlacemark(
            Point(latitude, longitude),
            ViewProvider(view)
        )

//        val imageProvider = ImageProvider.fromResource(this, R.drawable.ic_pin)
//        val placemark = mapView.map.mapObjects.addPlacemark().apply {
//            geometry = Point(59.935493, 30.327392)
//            setIcon(imageProvider)
//        }
    }

    private fun drawLocationMarks(locations: List<Location>) {
        val view = View(requireContext()).apply {
            background = requireContext().getDrawable(R.drawable.ic_baseline_adjust_24)
        }
        for (location in locations) {
            mapView.map?.mapObjects?.addPlacemark(
                Point(location.latitude, location.longitude),
                ViewProvider(view)
            )
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
        //   requestLocationPermission()
    }

    override fun onStop() {
       mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}