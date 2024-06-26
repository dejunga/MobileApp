package com.example.projekt

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.projekt.ui.theme.ProjektTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class AddAppointmentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjektTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AddAppointmentForm(navController = rememberNavController()) // Pass a default NavController
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppointmentForm(navController: NavHostController) {
    var selectedPatient by remember { mutableStateOf<Patient?>(null) }
    var selectedDoctor by remember { mutableStateOf<Doctor?>(null) }
    var patientList by remember { mutableStateOf(emptyList<Patient>()) }
    var doctorList by remember { mutableStateOf(emptyList<Doctor>()) }
    var showMessage by remember { mutableStateOf(false) }

    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val database = remember { DatabaseProvider.getDatabase(context) }
    val patientDao = remember { database.patientDao() }
    val doctorDao = remember { database.doctorDao() }
    val appointmentDao = remember { database.appointmentDao() }

    LaunchedEffect(Unit) {
        scope.launch {
            patientList = patientDao.getAllPatients()
            doctorList = doctorDao.getAllDoctors()
        }
    }

    // DatePickerDialog
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // TimePickerDialog
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            selectedTime = String.format("%02d:%02d", hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create an Appointment:",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        DropdownMenu(
            label = "Select Patient",
            items = patientList,
            selectedItem = selectedPatient,
            onItemSelected = { selectedPatient = it },
            itemLabel = { patient -> "${patient.firstName} ${patient.lastName}" }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DropdownMenu(
            label = "Select Doctor",
            items = doctorList,
            selectedItem = selectedDoctor,
            onItemSelected = { selectedDoctor = it },
            itemLabel = { doctor -> "${doctor.firstName} ${doctor.lastName}" }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { datePickerDialog.show() }) {
                Text(text = "Select Date")
            }
            Text(text = selectedDate, modifier = Modifier.align(Alignment.CenterVertically))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { timePickerDialog.show() }) {
                Text(text = "Select Time")
            }
            Text(text = selectedTime, modifier = Modifier.align(Alignment.CenterVertically))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    if (selectedPatient != null && selectedDoctor != null && selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                        val appointment = Appointment(
                            patientId = selectedPatient!!.id,
                            doctorId = selectedDoctor!!.id,
                            appointmentTime = "$selectedDate $selectedTime"
                        )
                        appointmentDao.insert(appointment)
                        showMessage = true
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Nastavi")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("view_appointments") // Navigate to view appointments screen
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("View Appointments")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showMessage) {
            Snackbar(
                action = {
                    Button(onClick = { showMessage = false }) {
                        Text("Dismiss")
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Appointment created successfully!")
            }
        }
    }
}









@Composable
fun ViewAppointmentsScreen() {
    val context = LocalContext.current
    val database = remember { DatabaseProvider.getDatabase(context) }
    val appointmentDao = remember { database.appointmentDao() }
    var appointments by remember { mutableStateOf(emptyList<Appointment>()) }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            appointments = appointmentDao.getAllAppointments()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Appointments",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        appointments.forEach { appointment ->
            Text(text = "Appointment: ${appointment.appointmentTime}")
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}












@Composable
fun <T> DropdownMenu(
    label: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemLabel: (T) -> String // Add this parameter
) where T : Any {
    var expanded by remember { mutableStateOf(false) }
    var selectedItemLabel by remember { mutableStateOf(selectedItem?.let { itemLabel(it) } ?: "Select $label") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { expanded = true }) {
                Text(text = selectedItemLabel)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = itemLabel(item)) }, // Use the lambda to get the label
                        onClick = {
                            onItemSelected(item)
                            selectedItemLabel = itemLabel(item) // Use the lambda to get the label
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddAppointmentFormPreview() {
    ProjektTheme {
        AddAppointmentForm(navController = rememberNavController()) // Pass a default NavController
    }
}
