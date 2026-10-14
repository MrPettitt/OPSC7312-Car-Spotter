package com.example.carspotteropsc7312poe.ui.welcome

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.carspotteropsc7312poe.R
import com.example.carspotteropsc7312poe.data.auth.SessionManager
import com.example.carspotteropsc7312poe.ui.auth.LoginActivity
import com.example.carspotteropsc7312poe.ui.dashboard.DashboardActivity

/** Launcher screen. Skips straight to the dashboard when a user is already signed in. */
class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (SessionManager(this).isSignedIn) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_welcome)
        findViewById<Button>(R.id.loginButton).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
