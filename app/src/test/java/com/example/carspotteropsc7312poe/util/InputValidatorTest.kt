package com.example.carspotteropsc7312poe.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InputValidatorTest {

    @Test
    fun email_acceptsNormalAddresses() {
        assertTrue(InputValidator.isValidEmail("spotter@example.com"))
        assertTrue(InputValidator.isValidEmail("  first.last+tag@mail.co.za "))
    }

    @Test
    fun email_rejectsBadAddresses() {
        assertFalse(InputValidator.isValidEmail(""))
        assertFalse(InputValidator.isValidEmail("no-at-sign.com"))
        assertFalse(InputValidator.isValidEmail("missing@tld"))
    }

    @Test
    fun password_needsSixCharacters() {
        assertFalse(InputValidator.isValidPassword("12345"))
        assertTrue(InputValidator.isValidPassword("123456"))
    }

    @Test
    fun latitude_mustBeWithinRange() {
        assertTrue(InputValidator.isValidLatitude(-26.2))
        assertTrue(InputValidator.isValidLatitude(90.0))
        assertFalse(InputValidator.isValidLatitude(90.1))
        assertFalse(InputValidator.isValidLatitude(null))
    }

    @Test
    fun longitude_mustBeWithinRange() {
        assertTrue(InputValidator.isValidLongitude(28.0))
        assertTrue(InputValidator.isValidLongitude(-180.0))
        assertFalse(InputValidator.isValidLongitude(180.5))
        assertFalse(InputValidator.isValidLongitude(null))
    }
}
