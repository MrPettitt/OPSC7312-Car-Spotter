package com.example.carspotteropsc7312poe.ui.observation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.carspotteropsc7312poe.R
import com.example.carspotteropsc7312poe.data.model.Observation
import com.example.carspotteropsc7312poe.databinding.ItemObservationBinding
import com.squareup.picasso.Picasso

class ObservationAdapter(private val observations: List<Observation>) :
    RecyclerView.Adapter<ObservationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemObservationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(observations[position])

    override fun getItemCount(): Int = observations.size

    class ViewHolder(private val binding: ItemObservationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(observation: Observation) {
            val context = binding.root.context

            binding.tvCarName.text = observation.carName
            binding.tvModel.text = observation.model
            binding.tvModel.visibility = if (observation.model.isBlank()) View.GONE else View.VISIBLE
            binding.tvCarDescription.text = observation.carDescription

            // "2026-10-14 09:30 · -26.2041, 28.0473", leaving out whatever isn't known.
            val hasLocation = observation.latitude != 0.0 || observation.longitude != 0.0
            val meta = listOfNotNull(
                observation.dateTime.takeIf { it.isNotBlank() },
                if (hasLocation) context.getString(
                    R.string.coordinates_format, observation.latitude, observation.longitude
                ) else null
            ).joinToString("  ·  ")
            binding.tvMeta.text = meta
            binding.tvMeta.visibility = if (meta.isEmpty()) View.GONE else View.VISIBLE

            if (observation.imageUrl.isNotBlank()) {
                Picasso.get()
                    .load(observation.imageUrl)
                    .placeholder(R.drawable.carspotter)
                    .error(R.drawable.carspotter)
                    .into(binding.imageView)
            } else {
                binding.imageView.setImageResource(R.drawable.carspotter)
            }
        }
    }
}
