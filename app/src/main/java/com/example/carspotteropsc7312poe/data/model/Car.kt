package com.example.carspotteropsc7312poe.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A car shown on the Knowledge screen.
 *
 * [imageName] is the *name* of a drawable (e.g. "ferrari"), not its `R.drawable` id:
 * resource ids change between builds, so persisting them breaks images after an app update.
 */
@Entity(tableName = "cars")
data class Car(
    @PrimaryKey val name: String,
    val description: String,
    val imageName: String
)
