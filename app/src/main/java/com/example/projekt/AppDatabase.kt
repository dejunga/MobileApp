package com.example.projekt

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Patient::class, Doctor::class, Appointment::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun doctorDao(): DoctorDao
    abstract fun appointmentDao(): AppointmentDao
}
