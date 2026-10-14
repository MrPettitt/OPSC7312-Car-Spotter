package com.example.carspotteropsc7312poe.ui.observation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.carspotteropsc7312poe.R
import com.example.carspotteropsc7312poe.data.auth.SessionManager
import com.example.carspotteropsc7312poe.data.local.AppDatabase
import com.example.carspotteropsc7312poe.data.model.Observation
import com.example.carspotteropsc7312poe.databinding.ActivityAddObservationBinding
import com.example.carspotteropsc7312poe.util.InputValidator
import com.example.carspotteropsc7312poe.util.setUpBackNavigation
import com.example.carspotteropsc7312poe.util.toast
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddObservationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddObservationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddObservationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpBackNavigation()

        // The map passes the long-pressed location so the form is pre-filled.
        if (intent.hasExtra(EXTRA_LATITUDE) && intent.hasExtra(EXTRA_LONGITUDE)) {
            binding.etLatitude.setText(intent.getDoubleExtra(EXTRA_LATITUDE, 0.0).toString())
            binding.etLongitude.setText(intent.getDoubleExtra(EXTRA_LONGITUDE, 0.0).toString())
        }

        // Preview the photo as soon as the user leaves the link field.
        binding.etImageUrl.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) previewImage() }

        binding.btnSave.setOnClickListener { saveObservation() }
    }

    private fun previewImage() {
        val url = binding.etImageUrl.text.toString().trim()
        if (url.isBlank()) {
            binding.imageView.visibility = View.GONE
            return
        }
        binding.imageView.visibility = View.VISIBLE
        Picasso.get().load(url).error(R.drawable.carspotter).into(binding.imageView)
    }

    private fun saveObservation() {
        val carName = binding.etCarName.text.toString().trim()
        val model = binding.etModel.text.toString().trim()
        val description = binding.etCarDescription.text.toString().trim()
        val imageUrl = binding.etImageUrl.text.toString().trim()
        val latitude = binding.etLatitude.text.toString().trim().toDoubleOrNull()
        val longitude = binding.etLongitude.text.toString().trim().toDoubleOrNull()

        // Validate first and keep what the user typed if something is wrong.
        if (carName.isBlank() || model.isBlank() || description.isBlank()) {
            toast(R.string.err_fill_required)
            return
        }
        if (!InputValidator.isValidLatitude(latitude) || !InputValidator.isValidLongitude(longitude)) {
            toast(R.string.err_invalid_coordinates)
            return
        }

        val observation = Observation(
            userId = SessionManager(this).currentUserId,
            carName = carName,
            model = model,
            carDescription = description,
            dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
            latitude = latitude!!,
            longitude = longitude!!,
            imageUrl = imageUrl
        )

        binding.btnSave.isEnabled = false
        val dao = AppDatabase.getDatabase(applicationContext).observationDao()
        lifecycleScope.launch {
            try {
                dao.insert(observation)
                toast(R.string.observation_saved)
                finish()
            } catch (e: Exception) {
                binding.btnSave.isEnabled = true
                toast(R.string.err_save_failed)
            }
        }
    }

    companion object {
        const val EXTRA_LATITUDE = "latitude"
        const val EXTRA_LONGITUDE = "longitude"
    }
}
