package com.example.projekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projekt.ui.theme.ProjektTheme
import kotlinx.coroutines.launch

class PatientListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjektTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PatientListScreen()
                }
            }
        }
    }
}

@Composable
fun PatientListScreen() {
    var patientList by remember { mutableStateOf(emptyList<Patient>()) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val database = remember { DatabaseProvider.getDatabase(context) }
    val patientDao = remember { database.patientDao() }

    LaunchedEffect(Unit) {
        scope.launch {
            patientList = patientDao.getAllPatients()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "List of Patients",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        patientList.forEach { patient ->
            Text(text = "${patient.firstName} ${patient.lastName} - ${patient.phone} - ${patient.email}")
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PatientListScreenPreview() {
    ProjektTheme {
        PatientListScreen()
    }
}
