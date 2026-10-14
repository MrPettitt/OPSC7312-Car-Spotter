package com.example.carspotteropsc7312poe.util

/** Pure input checks, kept free of Android classes so they can be unit tested on the JVM. */
object InputValidator {

    const val MIN_PASSWORD_LENGTH = 6

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(email.trim())

    fun isValidPassword(password: String): Boolean = password.length >= MIN_PASSWORD_LENGTH

    fun isValidLatitude(value: Double?): Boolean = value != null && value in -90.0..90.0

    fun isValidLongitude(value: Double?): Boolean = value != null && value in -180.0..180.0
}
