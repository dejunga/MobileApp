package com.example.projekt

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DoctorDao {
    @Insert
    suspend fun insert(doctor: Doctor)

    @Query("SELECT * FROM doctors")
    suspend fun getAllDoctors(): List<Doctor>
}
