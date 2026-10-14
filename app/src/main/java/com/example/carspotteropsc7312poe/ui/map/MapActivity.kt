package com.example.carspotteropsc7312poe.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.carspotteropsc7312poe.R
import com.example.carspotteropsc7312poe.data.auth.SessionManager
import com.example.carspotteropsc7312poe.data.local.AppDatabase
import com.example.carspotteropsc7312poe.ui.observation.AddObservationActivity
import com.example.carspotteropsc7312poe.util.setUpBackNavigation
import com.example.carspotteropsc7312poe.util.toast
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/**
 * Map of car sightings, drawn with osmdroid on OpenStreetMap tiles (free, no API key).
 * Your saved sightings appear as markers; a long press on the map starts a new observation there.
 */
class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var locationOverlay: MyLocationNewOverlay
    private val observationMarkers = mutableListOf<Marker>()

    private val locationPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            if (results.values.any { it }) {
                enableMyLocation()
            } else {
                toast(R.string.location_permission_needed)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // OSM's tile servers answer HTTP 403 to generic user agents such as a bare package name
        // ("com.example..."), which is what osmdroid sends by default. Identify the app properly.
        // The tile downloader ignores userAgentValue, so the header is overridden explicitly.
        Configuration.getInstance().apply {
            load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
            userAgentValue = USER_AGENT
            additionalHttpRequestProperties["User-Agent"] = USER_AGENT
        }

        setContentView(R.layout.activity_map)
        mapView = findViewById(R.id.mapView)
        setupMap()

        setUpBackNavigation()
        findViewById<View>(R.id.centerButton).setOnClickListener { centerOnMe() }

        requestLocationIfNeeded()
    }

    private fun setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(DEFAULT_ZOOM)
        mapView.controller.setCenter(DEFAULT_CENTER)

        // OpenStreetMap's licence requires this visible credit.
        mapView.overlays.add(CopyrightOverlay(this))

        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        mapView.overlays.add(locationOverlay)

        mapView.overlays.add(MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean = false

            override fun longPressHelper(p: GeoPoint): Boolean {
                startActivity(
                    Intent(this@MapActivity, AddObservationActivity::class.java)
                        .putExtra(AddObservationActivity.EXTRA_LATITUDE, p.latitude)
                        .putExtra(AddObservationActivity.EXTRA_LONGITUDE, p.longitude)
                )
                return true
            }
        }))
    }

    private fun requestLocationIfNeeded() {
        val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        if (granted) {
            enableMyLocation()
        } else {
            locationPermission.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    private fun enableMyLocation() {
        locationOverlay.enableMyLocation()
        // Centre on the first fix only, so the user can still pan freely afterwards.
        locationOverlay.runOnFirstFix {
            runOnUiThread {
                locationOverlay.myLocation?.let { mapView.controller.animateTo(it, 15.0, null) }
            }
        }
    }

    private fun centerOnMe() {
        val here = locationOverlay.myLocation
        if (here != null) {
            mapView.controller.animateTo(here)
        } else {
            toast(R.string.location_not_available)
        }
    }

    private fun loadObservationMarkers() {
        val userId = SessionManager(this).currentUserId
        val dao = AppDatabase.getDatabase(applicationContext).observationDao()

        lifecycleScope.launch {
            val observations = dao.forUser(userId)

            mapView.overlays.removeAll(observationMarkers.toSet())
            observationMarkers.clear()

            observations.forEach { obs ->
                val marker = Marker(mapView).apply {
                    position = GeoPoint(obs.latitude, obs.longitude)
                    title = obs.carName
                    snippet = obs.model
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                observationMarkers.add(marker)
                mapView.overlays.add(marker)
            }
            mapView.invalidate()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        loadObservationMarkers()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDetach()
    }

    private companion object {
        const val USER_AGENT = "CarSpotter/1.0 (Android; +https://github.com/MrPettitt/OPSC7312-Car-Spotter)"
        const val DEFAULT_ZOOM = 12.0
        val DEFAULT_CENTER = GeoPoint(-26.2041, 28.0473) // Johannesburg, until a GPS fix arrives
    }
}
