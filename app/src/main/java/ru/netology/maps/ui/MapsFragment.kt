package ru.netology.maps.ui


import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.*
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.*
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.ui_view.ViewProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.maps.R
import ru.netology.maps.dto.Location
import ru.netology.maps.ui.LocationsListFragment.Companion.textArgLatitude
import ru.netology.maps.ui.LocationsListFragment.Companion.textArgLongitude
import ru.netology.maps.viewModel.LocationViewModel

@AndroidEntryPoint
class MapsFragment : Fragment(R.layout.fragment_maps), GeoObjectTapListener, InputListener {

    private lateinit var mapView: MapView
    private val viewModel: LocationViewModel by activityViewModels()


    private val MAPKIT_API_KEY = "8174a68d-2910-48fc-b27a-abea5746916b"

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

        MapKitInitializer.initialize(MAPKIT_API_KEY, requireContext())
        val binding = ru.netology.maps.databinding.FragmentMapsBinding.inflate(
            inflater, container, false
        )

        mapView = binding.mapview


        var mapkit: MapKit = MapKitFactory.getInstance()

        mapView.map?.addTapListener(this)
        mapView.map?.addInputListener(this)

        val addLocation = binding.addLocation
        addLocation.setOnClickListener {
            showCustomDialog()
            drawMyLocationMark(point.latitude, point.longitude)

        }

        val allList = binding.allLocations
        allList.setOnClickListener {
            findNavController().navigate(R.id.action_mapsFragment_to_locationsListFragment)
        }


        viewModel.data.observe(viewLifecycleOwner) { locations ->
            drawMyLocationMarks(locations)

        }

        val longitudeBundle = arguments?.textArgLongitude?.toDouble()
        val latitudeBundle = arguments?.textArgLatitude?.toDouble()

        if (longitudeBundle != null && latitudeBundle != null) {
            mapView.map?.move(
                CameraPosition(Point(latitudeBundle, longitudeBundle), 17.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 8F),
                null
            )
        }

        return binding.root
    }


    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

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

    override fun onMapTap(maps: com.yandex.mapkit.map.Map, point: Point) {
        mapView.map?.deselectGeoObject()
    }

    override fun onMapLongTap(maps: com.yandex.mapkit.map.Map, longTapPoint: Point) {
        val text = mapView.map?.deselectGeoObject()
        drawMyLocationMark(longTapPoint.latitude, longTapPoint.longitude)

        point = Point(longTapPoint.latitude, longTapPoint.longitude)

    }

    private fun drawMyLocationMark(latitude: Double, longitude: Double) {
        val view = View(requireContext()).apply {
            background = requireContext().getDrawable(R.drawable.ic_baseline_adjust_24)
        }

        mapView.map?.mapObjects?.addPlacemark(
            Point(latitude, longitude),
            ViewProvider(view)
        )
    }

    private fun drawMyLocationMarks(locations: List<Location>) {
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

    fun showCustomDialog() {
        val dialog = TitleDialog(latitude = point.latitude, longitude = point.longitude)
        dialog.show(childFragmentManager, "dialog")

    }

}

