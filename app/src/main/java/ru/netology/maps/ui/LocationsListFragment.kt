package ru.netology.maps.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.maps.R
import ru.netology.maps.adapter.LocationAdapter
import ru.netology.maps.adapter.OnInteractionListener
import ru.netology.maps.databinding.FragmentLocationListBinding
import ru.netology.maps.dto.Location
import ru.netology.maps.util.StringArg

import ru.netology.maps.viewModel.LocationViewModel

@AndroidEntryPoint
class LocationsListFragment : Fragment(R.layout.fragment_location_list) {


    companion object {
        var Bundle.textArgLatitude: String? by StringArg
        var Bundle.textArgLongitude: String? by StringArg
    }

    private val viewModel: LocationViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLocationListBinding.inflate(
            inflater,
            container,
            false
        )


        var adapter = LocationAdapter(object : OnInteractionListener {
            override fun onEdit(location: Location) {
                viewModel.edit(location)
                val dialog = TitleDialog(latitude = 0.0, longitude = 0.0)
                dialog.show(childFragmentManager, "dialog")
            }

            override fun onRemove(location: Location) {
                viewModel.removeById(location.id)
            }

            override fun onClick(location: Location) {
                findNavController().navigate(R.id.action_locationsListFragment_to_mapsFragment,
                    Bundle().apply {
                        textArgLatitude = location.latitude.toString()
                        textArgLongitude = location.longitude.toString()

                    })
            }
        })

        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { location ->
            adapter.submitList(location)
        }


        return binding.root
    }


}