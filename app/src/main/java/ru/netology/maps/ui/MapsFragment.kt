package ru.netology.maps.ui


import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.Group
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import ru.netology.maps.R
import ru.netology.maps.dto.Location
import ru.netology.maps.ui.LocationsListFragment.Companion.textArg
import ru.netology.maps.ui.LocationsListFragment.Companion.textArg2
import ru.netology.maps.viewModel.LocationViewModel


class MapsFragment : Fragment(R.layout.fragment_maps), GeoObjectTapListener, InputListener {

    private lateinit var mapView: MapView
    private val viewModel: LocationViewModel by viewModels()

    private val PERMISSIONS_REQUEST_FINE_LOCATION = 1

    private val userLocationLayer: UserLocationLayer? = null

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

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                "android.permission.ACCESS_FINE_LOCATION"
            )
            != PackageManager.PERMISSION_GRANTED

        ) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it.parent, arrayOf("android.permission.ACCESS_FINE_LOCATION"),
                    PERMISSIONS_REQUEST_FINE_LOCATION
                )
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        MapKitInitializer.initialize(MAPKIT_API_KEY, requireContext())

        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        super.onCreate(savedInstanceState)
        mapView = view.findViewById(R.id.mapview) as MapView

        var mapkit: MapKit = MapKitFactory.getInstance()

        mapView.map?.addTapListener(this)
        mapView.map?.addInputListener(this)

        val addLocation = view.findViewById(R.id.add_location) as FloatingActionButton

        addLocation.setOnClickListener {
            showCustomDialog()
            drawMyLocationMark(point.latitude, point.longitude)

        }


        val allList = view.findViewById(R.id.all_locations) as FloatingActionButton
        allList.setOnClickListener {
            findNavController().navigate(R.id.action_mapsFragment_to_locationsListFragment)
        }



        viewModel.data.observe(viewLifecycleOwner) { locations ->
            drawMyLocationMarks(locations)

        }

        val longitudeBundle = arguments?.textArg2?.toDouble()
        val latitudeBundle = arguments?.textArg?.toDouble()

        if (longitudeBundle != null && latitudeBundle != null) {
            mapView.map?.move(
                CameraPosition(Point(latitudeBundle, longitudeBundle), 17.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 8F),
                null
            )
        }


        return view
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

    override fun onMapTap(p0: com.yandex.mapkit.map.Map, p1: Point) {
        //     point = Point(p1.latitude, p1.longitude)
        mapView.map?.deselectGeoObject()
    }

    override fun onMapLongTap(p0: com.yandex.mapkit.map.Map, p1: Point) {
        val text = mapView.map?.deselectGeoObject()
        drawMyLocationMark(p1.latitude, p1.longitude)

        point = Point(p1.latitude, p1.longitude)

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

    private fun showCustomDialog() {
        val dialog = context?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setContentView(R.layout.title_dialog)
        val save = dialog?.findViewById<Button>(R.id.save)
        val text = dialog?.findViewById<EditText>(R.id.title)
        text?.text.toString()
        save?.setOnClickListener {
            viewModel.saveLocation(point.latitude, point.longitude, text?.text.toString())
            dialog.dismiss()
        }

        dialog?.show()

    }


}