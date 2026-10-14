package com.example.carspotteropsc7312poe.ui.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import com.example.carspotteropsc7312poe.R
import com.example.carspotteropsc7312poe.notifications.NotificationScheduler
import com.example.carspotteropsc7312poe.util.setUpBackNavigation
import com.example.carspotteropsc7312poe.util.toast
import com.google.android.material.materialswitch.MaterialSwitch

class SettingsActivity : AppCompatActivity() {

    // Order must match R.array.language_options.
    private val languageTags = listOf("en", "zu", "af")

    private lateinit var languageSpinner: Spinner
    private lateinit var notificationSwitch: MaterialSwitch

    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                NotificationScheduler.setEnabled(this, true)
            } else {
                notificationSwitch.isChecked = false
                toast(R.string.notifications_blocked)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setUpBackNavigation()

        languageSpinner = findViewById(R.id.spinner_language)
        setupLanguageSpinner()
        setupNotificationSwitch()

        findViewById<Button>(R.id.btn_map_settings).setOnClickListener {
            startActivity(Intent(this, MapSettingsActivity::class.java))
        }
    }

    private fun setupNotificationSwitch() {
        notificationSwitch = findViewById(R.id.switch_notifications)
        notificationSwitch.isChecked = NotificationScheduler.isEnabled(this) && notificationsAllowed()

        notificationSwitch.setOnCheckedChangeListener { button, isChecked ->
            if (!button.isPressed) return@setOnCheckedChangeListener // ignore programmatic changes
            when {
                !isChecked -> NotificationScheduler.setEnabled(this, false)
                notificationsAllowed() -> NotificationScheduler.setEnabled(this, true)
                else -> notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun notificationsAllowed(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    private fun setupLanguageSpinner() {
        val options = resources.getStringArray(R.array.language_options)
        languageSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val current = currentLanguageTag()
        languageSpinner.setSelection(languageTags.indexOf(current).coerceAtLeast(0))

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = languageTags[position]
                if (selected != currentLanguageTag()) {
                    // AppCompat saves the choice, applies it app-wide and recreates this screen.
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(selected))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun currentLanguageTag(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        return if (locales.isEmpty) "en" else locales[0]?.language ?: "en"
    }
}
