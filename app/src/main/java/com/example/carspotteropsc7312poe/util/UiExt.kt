package com.example.carspotteropsc7312poe.util

import android.app.Activity
import android.widget.Toast
import androidx.annotation.StringRes
import com.example.carspotteropsc7312poe.R
import com.google.android.material.appbar.MaterialToolbar

/** Makes the back arrow in the screen's toolbar (id `toolbar`) close the screen. */
fun Activity.setUpBackNavigation() {
    findViewById<MaterialToolbar>(R.id.toolbar)?.setNavigationOnClickListener { finish() }
}

fun Activity.toast(@StringRes message: Int, vararg args: Any) {
    Toast.makeText(this, getString(message, *args), Toast.LENGTH_SHORT).show()
}
