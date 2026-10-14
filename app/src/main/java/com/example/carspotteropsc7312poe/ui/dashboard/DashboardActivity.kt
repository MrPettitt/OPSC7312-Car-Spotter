package com.example.carspotteropsc7312poe.ui.dashboard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.carspotteropsc7312poe.R
import com.example.carspotteropsc7312poe.data.SampleCars
import com.example.carspotteropsc7312poe.data.auth.AccountManager
import com.example.carspotteropsc7312poe.data.model.Car
import com.example.carspotteropsc7312poe.ui.knowledge.KnowledgeActivity
import com.example.carspotteropsc7312poe.ui.map.MapActivity
import com.example.carspotteropsc7312poe.ui.observation.ObservationListActivity
import com.example.carspotteropsc7312poe.ui.settings.SettingsActivity
import com.example.carspotteropsc7312poe.ui.welcome.WelcomeActivity
import com.example.carspotteropsc7312poe.util.toast
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private lateinit var accounts: AccountManager

    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* optional */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accounts = AccountManager(this)

        // A stale session (e.g. the account was removed) must not open the dashboard.
        if (!accounts.session.isSignedIn) {
            goToWelcome()
            return
        }
        setContentView(R.layout.activity_dashboard)

        findViewById<Button>(R.id.btnLogout).setOnClickListener { signOut() }
        findViewById<Button>(R.id.btnLocateCars).setOnClickListener { open(MapActivity::class.java) }
        findViewById<Button>(R.id.btnObservations).setOnClickListener { open(ObservationListActivity::class.java) }
        findViewById<Button>(R.id.btnSettings).setOnClickListener { open(SettingsActivity::class.java) }
        findViewById<Button>(R.id.btnKnowledge).setOnClickListener { open(KnowledgeActivity::class.java) }

        showGreeting()
        displayRandomCar()
        requestNotificationPermissionIfNeeded()
    }

    private fun open(target: Class<*>) = startActivity(Intent(this, target))

    private fun showGreeting() {
        lifecycleScope.launch {
            val name = accounts.currentUser()?.email?.substringBefore('@')
            findViewById<TextView>(R.id.welcometxt).text =
                if (name.isNullOrBlank()) getString(R.string.welcome) else getString(R.string.welcome_user, name)
        }
    }

    private fun displayRandomCar() {
        val car: Car = SampleCars.all.random()
        val imageRes = resources.getIdentifier(car.imageName, "drawable", packageName)

        if (imageRes != 0) findViewById<ImageView>(R.id.carImageView).setImageResource(imageRes)
        findViewById<TextView>(R.id.carNameTextView).text = car.name
        findViewById<TextView>(R.id.carDescriptionTextView).text = car.description
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (!granted) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun signOut() {
        accounts.signOut()
        toast(R.string.signed_out)
        goToWelcome()
    }

    private fun goToWelcome() {
        // Clear the back stack so "Back" cannot return to a signed-in screen.
        startActivity(
            Intent(this, WelcomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }
}
