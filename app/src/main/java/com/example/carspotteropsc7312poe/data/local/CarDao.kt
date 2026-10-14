package com.example.carspotteropsc7312poe.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.carspotteropsc7312poe.data.model.Car

@Dao
interface CarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cars: List<Car>)

    @Query("SELECT * FROM cars ORDER BY name")
    suspend fun getAllCars(): List<Car>
}
