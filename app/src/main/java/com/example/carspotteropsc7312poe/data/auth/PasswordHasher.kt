package com.example.carspotteropsc7312poe.data.auth

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/** Salted PBKDF2 password hashing. Plain JVM code so it can be unit tested. */
object PasswordHasher {

    private const val ITERATIONS = 120_000
    private const val KEY_BITS = 256
    private const val SALT_BYTES = 16

    // PBKDF2WithHmacSHA256 needs API 26, but the app supports API 25.
    private const val ALGORITHM = "PBKDF2WithHmacSHA1"

    fun newSalt(): String = ByteArray(SALT_BYTES).also { SecureRandom().nextBytes(it) }.toHex()

    fun hash(password: String, saltHex: String): String {
        val spec = PBEKeySpec(password.toCharArray(), saltHex.hexToBytes(), ITERATIONS, KEY_BITS)
        return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).encoded.toHex()
    }

    fun verify(password: String, saltHex: String, expectedHash: String): Boolean =
        // Constant-time comparison, so timing doesn't reveal how much of the hash matched.
        MessageDigest.isEqual(hash(password, saltHex).toByteArray(), expectedHash.toByteArray())

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

    private fun String.hexToBytes(): ByteArray = chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
