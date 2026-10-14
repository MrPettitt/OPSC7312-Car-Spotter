package com.example.carspotteropsc7312poe.ui.observation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.carspotteropsc7312poe.data.auth.SessionManager
import com.example.carspotteropsc7312poe.data.local.AppDatabase
import com.example.carspotteropsc7312poe.databinding.ActivityObservationListBinding
import com.example.carspotteropsc7312poe.util.setUpBackNavigation
import kotlinx.coroutines.launch

class ObservationListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityObservationListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityObservationListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpBackNavigation()

        binding.buttonAddObservation.setOnClickListener {
            startActivity(Intent(this, AddObservationActivity::class.java))
        }
    }

    // Reload every time the screen is shown so a newly added sighting appears on return.
    override fun onStart() {
        super.onStart()
        loadObservations()
    }

    private fun loadObservations() {
        val userId = SessionManager(this).currentUserId
        val dao = AppDatabase.getDatabase(applicationContext).observationDao()

        lifecycleScope.launch {
            val observations = dao.forUser(userId)
            binding.recyclerView.adapter = ObservationAdapter(observations)
            binding.tvEmpty.visibility = if (observations.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}
