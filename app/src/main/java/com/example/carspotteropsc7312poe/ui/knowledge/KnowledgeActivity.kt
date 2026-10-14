package com.example.carspotteropsc7312poe.ui.knowledge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carspotteropsc7312poe.R
import com.example.carspotteropsc7312poe.data.SampleCars
import com.example.carspotteropsc7312poe.data.local.AppDatabase
import com.example.carspotteropsc7312poe.data.model.Observation
import com.example.carspotteropsc7312poe.ui.observation.ObservationAdapter
import com.example.carspotteropsc7312poe.util.setUpBackNavigation
import kotlinx.coroutines.launch

/** "Knowledge" screen: a list of cars kept in the local Room database. */
class KnowledgeActivity : AppCompatActivity() {

    private val items = mutableListOf<Observation>()
    private lateinit var adapter: ObservationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_knowledge)
        setUpBackNavigation()

        adapter = ObservationAdapter(items)
        findViewById<RecyclerView>(R.id.recyclerViewObservations).apply {
            layoutManager = LinearLayoutManager(this@KnowledgeActivity)
            adapter = this@KnowledgeActivity.adapter
        }

        loadCars()
    }

    private fun loadCars() {
        val carDao = AppDatabase.getDatabase(applicationContext).carDao()

        lifecycleScope.launch {
            if (carDao.getAllCars().isEmpty()) carDao.insertAll(SampleCars.all)

            // Cars share the sighting card layout; they are display-only and never saved as sightings.
            val loaded = carDao.getAllCars().map { car ->
                Observation(
                    carName = car.name,
                    carDescription = car.description,
                    imageUrl = "android.resource://$packageName/drawable/${car.imageName}"
                )
            }
            items.clear()
            items.addAll(loaded)
            adapter.notifyDataSetChanged()
        }
    }
}
