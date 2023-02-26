package ru.netology.maps.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.MapKitFactory
import ru.netology.maps.R
import ru.netology.maps.adapter.LocationAdapter
import ru.netology.maps.adapter.OnInteractionListener
import ru.netology.maps.databinding.FragmentLocationListBinding
import ru.netology.maps.dto.Location
import ru.netology.maps.util.StringArg

import ru.netology.maps.viewModel.LocationViewModel

class LocationsListFragment : Fragment(R.layout.fragment_location_list) {


    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: LocationViewModel by viewModels()


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
                showCustomDialog()
                //         binding.titleAndSave.visibility = View.VISIBLE
//                val text = binding.title.text.toString()
//                val save = binding.save
//
//                save.setOnClickListener {
//                    viewModel.changeTitle(
//                        text
//                    )
//                    binding.titleAndSave.visibility = View.GONE
//                }
            }

            override fun onRemove(location: Location) {
                viewModel.removeById(location.id)
            }

            override fun onClick(location: Location) {
                findNavController().navigate(R.id.action_locationsListFragment_to_mapsFragment,
                    Bundle().apply {
                        textArg = location.id.toString()
                    })
            }
        })

        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { location ->
            adapter.submitList(location)
        }




        return binding.root
    }

    fun showCustomDialog() {
        val dialog = context?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setContentView(R.layout.title_dialog)
        val save = dialog?.findViewById<Button>(R.id.save)
        val text = dialog?.findViewById<EditText>(R.id.title)
        //   text?.text.toString()

        save?.setOnClickListener {
            viewModel.changeTitle(text?.text.toString())
            dialog.dismiss()
        }
        dialog?.show()
    }
}