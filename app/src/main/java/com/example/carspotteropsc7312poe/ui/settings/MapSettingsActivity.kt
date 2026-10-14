package com.example.carspotteropsc7312poe.ui.settings

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.carspotteropsc7312poe.R
import com.example.carspotteropsc7312poe.util.setUpBackNavigation
import com.example.carspotteropsc7312poe.util.toast

/** Lets the user pick distance units and a maximum hotspot distance, saved on the device. */
class MapSettingsActivity : AppCompatActivity() {

    // Distance ranges (in the selected unit) offered in the spinner.
    private val distances = arrayOf("5-10", "10-15", "20-35", "40-45", "50-55")

    private val prefs by lazy { getSharedPreferences(PREFS, Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_settings)
        setUpBackNavigation()

        val unitGroup = findViewById<RadioGroup>(R.id.radioGroupUnitSelect)
        val spinner = findViewById<Spinner>(R.id.spinnerMaxDistance)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, distances)

        // Show what was saved last time.
        spinner.setSelection(distances.indexOf(prefs.getString(KEY_DISTANCE, null)).coerceAtLeast(0))
        unitGroup.check(
            if (prefs.getString(KEY_UNIT, UNIT_METRIC) == UNIT_IMPERIAL) R.id.radioButtonImperial
            else R.id.radioButtonMetric
        )

        findViewById<Button>(R.id.btnSaveSettings).setOnClickListener {
            // Saved as a fixed value, not the on-screen label, so it doesn't change with the language.
            val unit = if (unitGroup.checkedRadioButtonId == R.id.radioButtonImperial) UNIT_IMPERIAL else UNIT_METRIC
            prefs.edit()
                .putString(KEY_DISTANCE, distances[spinner.selectedItemPosition])
                .putString(KEY_UNIT, unit)
                .apply()
            toast(R.string.settings_saved)
        }
    }

    private companion object {
        const val PREFS = "map_settings"
        const val KEY_DISTANCE = "distance"
        const val KEY_UNIT = "unit"
        const val UNIT_METRIC = "metric"
        const val UNIT_IMPERIAL = "imperial"
    }
}
