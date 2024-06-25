package com.example.projekt

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AppointmentDao {
    @Insert
    suspend fun insert(appointment: Appointment)

    @Query("SELECT * FROM appointments")
    suspend fun getAllAppointments(): List<Appointment>
}
