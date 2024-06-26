package com.example.projekt

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(var title: String, var icon: ImageVector, var screen_route: String) {
    object Patients : NavItem("Patients", Icons.Default.Person, "patients")
    object Doctors : NavItem("Doctors", Icons.Default.Person, "doctors") // Replace with available icon
    object Appointments : NavItem("Appointments", Icons.Default.DateRange, "appointments") // Replace with available icon
    object Settings : NavItem("Settings", Icons.Default.Settings, "settings")
}
