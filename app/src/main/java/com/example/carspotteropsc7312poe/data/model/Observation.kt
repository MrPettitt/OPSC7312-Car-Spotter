package com.example.carspotteropsc7312poe.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** A car sighting saved on this device by the user with id [userId]. */
@Entity(tableName = "observations", indices = [Index("userId")])
data class Observation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long = 0,
    val carName: String = "",
    val model: String = "",
    val carDescription: String = "",
    val dateTime: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUrl: String = ""
)
