package com.example.projekt

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PatientDao {
    @Insert
    suspend fun insert(patient: Patient)

    @Query("SELECT * FROM patients")
    suspend fun getAllPatients(): List<Patient>
}
