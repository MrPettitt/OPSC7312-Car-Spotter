package com.example.carspotteropsc7312poe.data.auth

import android.content.Context

/**
 * Remembers who is signed in. Only a user id is stored, never a password.
 *
 * The *last* user is kept after sign-out so the biometric button can sign the device owner
 * back in without typing a password.
 */
class SessionManager(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences("session", Context.MODE_PRIVATE)

    val currentUserId: Long get() = prefs.getLong(KEY_CURRENT, NONE)
    val lastUserId: Long get() = prefs.getLong(KEY_LAST, NONE)
    val isSignedIn: Boolean get() = currentUserId != NONE

    fun signIn(userId: Long) {
        prefs.edit().putLong(KEY_CURRENT, userId).putLong(KEY_LAST, userId).apply()
    }

    fun signOut() {
        prefs.edit().remove(KEY_CURRENT).apply()
    }

    private companion object {
        const val KEY_CURRENT = "current_user_id"
        const val KEY_LAST = "last_user_id"
        const val NONE = 0L
    }
}
