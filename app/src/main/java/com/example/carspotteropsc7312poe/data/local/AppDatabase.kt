package com.example.carspotteropsc7312poe.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.carspotteropsc7312poe.data.model.Car
import com.example.carspotteropsc7312poe.data.model.Observation
import com.example.carspotteropsc7312poe.data.model.User

@Database(entities = [Car::class, User::class, Observation::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun carDao(): CarDao
    abstract fun userDao(): UserDao
    abstract fun observationDao(): ObservationDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "car_database"
                )
                    // Versions 1 and 2 only held seed data, so rebuilding on upgrade loses nothing.
                    // Add real migrations before shipping a version that stores data worth keeping.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
    }
}
