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

class DoctorListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjektTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DoctorListScreen()
                }
            }
        }
    }
}

@Composable
fun DoctorListScreen() {
    var doctorList by remember { mutableStateOf(emptyList<Doctor>()) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val database = remember { DatabaseProvider.getDatabase(context) }
    val doctorDao = remember { database.doctorDao() }

    LaunchedEffect(Unit) {
        scope.launch {
            doctorList = doctorDao.getAllDoctors()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "List of Doctors",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        doctorList.forEach { doctor ->
            Text(text = "${doctor.firstName} ${doctor.lastName}")
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DoctorListScreenPreview() {
    ProjektTheme {
        DoctorListScreen()
    }
}
