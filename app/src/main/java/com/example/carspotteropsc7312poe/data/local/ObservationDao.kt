package com.example.carspotteropsc7312poe.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.carspotteropsc7312poe.data.model.Observation

@Dao
interface ObservationDao {
    @Insert
    suspend fun insert(observation: Observation): Long

    /** The user's sightings, newest first (the saved date text sorts chronologically). */
    @Query("SELECT * FROM observations WHERE userId = :userId ORDER BY dateTime DESC, id DESC")
    suspend fun forUser(userId: Long): List<Observation>
}
