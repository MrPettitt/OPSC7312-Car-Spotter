package com.example.carspotteropsc7312poe.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * An account stored on this device. The password is never kept: only a salted PBKDF2 hash
 * (see [com.example.carspotteropsc7312poe.data.auth.PasswordHasher]).
 */
@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String,
    val passwordHash: String,
    val salt: String,
    val createdAt: Long = System.currentTimeMillis()
)
