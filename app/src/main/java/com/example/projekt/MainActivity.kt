package com.example.projekt

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projekt.ui.theme.ProjektTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjektTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    InputForm()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputForm() {
    var firstName by remember { mutableStateOf(TextFieldValue("")) }
    var lastName by remember { mutableStateOf(TextFieldValue("")) }
    var phone by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var officeId by remember { mutableStateOf(TextFieldValue("")) }
    var showMessage by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val database = remember { DatabaseProvider.getDatabase(context) }
    val patientDao = remember { database.patientDao() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Upiši svoje podatke:",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = officeId,
            onValueChange = { officeId = it },
            label = { Text("Office ID") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                scope.launch {
                    val patient = Patient(
                        firstName = firstName.text,
                        lastName = lastName.text,
                        phone = phone.text,
                        email = email.text,
                        officeId = officeId.text.toInt()
                    )
                    patientDao.insert(patient)
                    showMessage = true
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Nastavi")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Greeting(firstName.text, lastName.text)

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
                Text(text = "Uspješno su uneseni vaši podatci!")
            }
        }

        // Button to navigate to patient list screen
        Button(
            onClick = {
                // Navigate to PatientListActivity
                context.startActivity(
                    Intent(context, PatientListActivity::class.java)
                )
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("View Patients")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to navigate to add doctor screen
        Button(
            onClick = {
                // Navigate to AddDoctorActivity
                context.startActivity(
                    Intent(context, AddDoctorActivity::class.java)
                )
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add Doctor")
        }
    }
}


@Composable
fun Greeting(firstName: String, lastName: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $firstName $lastName!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun InputFormPreview() {
    ProjektTheme {
        InputForm()
    }
}
