package ru.netology.maps.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.maps.R
import ru.netology.maps.databinding.LocationCardBinding
import ru.netology.maps.data.dto.Location


interface OnInteractionListener {
    fun onRemove(location: Location) {}
    fun onEdit(location: Location) {}
    fun onClick(location: Location) {}
}

class LocationAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Location, LocationViewHolder>(LocationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding =
            LocationCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class LocationViewHolder(
    private val binding: LocationCardBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(location: Location) {
        binding.apply {
            title.text = location.title
            latitude.text = location.latitude.toString()
            longitude.text = location.longitude.toString()


            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.location_options)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(location)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(location)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            itemView.setOnClickListener {
                onInteractionListener.onClick(location)
            }
        }
    }
}


class LocationDiffCallback : DiffUtil.ItemCallback<Location>() {
    override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
        return oldItem == newItem
    }

}