package com.example.projekt

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projekt.ui.theme.ProjektTheme
import kotlinx.coroutines.launch

class AddAppointmentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjektTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AddAppointmentForm()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppointmentForm() {
    var selectedPatient by remember { mutableStateOf<Patient?>(null) }
    var selectedDoctor by remember { mutableStateOf<Doctor?>(null) }
    var appointmentTime by remember { mutableStateOf(TextFieldValue("")) }
    var patientList by remember { mutableStateOf(emptyList<Patient>()) }
    var doctorList by remember { mutableStateOf(emptyList<Doctor>()) }
    var showMessage by remember { mutableStateOf(false) }

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
            onItemSelected = { selectedPatient = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DropdownMenu(
            label = "Select Doctor",
            items = doctorList,
            selectedItem = selectedDoctor,
            onItemSelected = { selectedDoctor = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = appointmentTime,
            onValueChange = { appointmentTime = it },
            label = { Text("Appointment Time") },
            keyboardOptions = KeyboardOptions.Default,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    if (selectedPatient != null && selectedDoctor != null && appointmentTime.text.isNotEmpty()) {
                        val appointment = Appointment(
                            patientId = selectedPatient!!.id,
                            doctorId = selectedDoctor!!.id,
                            appointmentTime = appointmentTime.text
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

        // Show Snackbar for success message
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
fun <T> DropdownMenu(
    label: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit
) where T : Any {
    var expanded by remember { mutableStateOf(false) }
    var selectedItemLabel by remember { mutableStateOf(selectedItem?.toString() ?: "Select $label") }

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
                        text = { Text(text = item.toString()) }, // Adjust as needed to get the label
                        onClick = {
                            onItemSelected(item)
                            selectedItemLabel = item.toString() // Adjust as needed to get the label
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
        AddAppointmentForm()
    }
}
