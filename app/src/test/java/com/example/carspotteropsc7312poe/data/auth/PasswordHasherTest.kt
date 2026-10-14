package com.example.carspotteropsc7312poe.data.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {

    @Test
    fun correctPassword_verifies() {
        val salt = PasswordHasher.newSalt()
        val hash = PasswordHasher.hash("s3cret-pass", salt)
        assertTrue(PasswordHasher.verify("s3cret-pass", salt, hash))
    }

    @Test
    fun wrongPassword_isRejected() {
        val salt = PasswordHasher.newSalt()
        val hash = PasswordHasher.hash("s3cret-pass", salt)
        assertFalse(PasswordHasher.verify("S3cret-pass", salt, hash))
    }

    @Test
    fun hash_doesNotContainThePassword() {
        val salt = PasswordHasher.newSalt()
        assertFalse(PasswordHasher.hash("hunter22", salt).contains("hunter22"))
    }

    @Test
    fun samePassword_getsDifferentHashesWithDifferentSalts() {
        val one = PasswordHasher.hash("same-password", PasswordHasher.newSalt())
        val two = PasswordHasher.hash("same-password", PasswordHasher.newSalt())
        assertNotEquals(one, two)
    }

    @Test
    fun sameSalt_isDeterministic() {
        val salt = PasswordHasher.newSalt()
        assertEquals(PasswordHasher.hash("abc123", salt), PasswordHasher.hash("abc123", salt))
    }
}
