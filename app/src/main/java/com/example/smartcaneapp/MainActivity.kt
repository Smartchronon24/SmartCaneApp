package com.example.smartcaneapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.smartcaneapp.ui.theme.SmartCaneAppTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : ComponentActivity() {
    private val firebase_latitude = mutableStateOf<Double?>(null)
    private val firebase_longitude = mutableStateOf<Double?>(null)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, FallDetectionService::class.java))
        }

        FirebaseApp.initializeApp(this)
        val database = FirebaseDatabase.getInstance()
        val gpsRef = database.getReference("Sensors") // Single reference for both latitude & longitude
        val viewModel: LocationViewModel by viewModels()
        val dataviewmodel : DataViewModel by viewModels()

        gpsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lat = snapshot.child("gps").child("latitude").getValue(String::class.java)?.toDoubleOrNull()
                val lon = snapshot.child("gps").child("longitude").getValue(String::class.java)?.toDoubleOrNull()
                val espstatus = snapshot.child("ESP32").getValue(Boolean::class.java)?:false

                if (lat != null && lon != null) {
                    firebase_latitude.value = lat
                    firebase_longitude.value = lon
                    viewModel.updateCaneLocation(lat, lon) // Update both at the same time

                }

                if (espstatus != null) {
                    dataviewmodel.updateESPStatus(espstatus)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Failed to read GPS data.", error.toException())
            }
        })

        enableEdgeToEdge()
        setContent {
            SmartCaneAppTheme {
                MainScreen(firebase_latitude, firebase_longitude, viewModel, dataviewmodel)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(db_latitude: MutableState<Double?>, db_longitude: MutableState<Double?>, viewModel: LocationViewModel, dataViewModel: DataViewModel) {

    val context = LocalContext.current
    val locationUtils = LocationUtils(context)  // Ensure an instance exists

    LaunchedEffect(Unit) {
        if (locationUtils.hasLocationPermission(context) && locationUtils.isLocationEnabled(context)) {
            locationUtils.requestLiveUpdates(viewModel)  // Use the instance instead of calling it directly
        }
    }

    val navController = rememberNavController() // Single instance of NavController

    val items = listOf("Home", "Settings")

    val titleFont = FontFamily(
        Font(R.font.oserif)  // Ensure the file is in res/font/
    )
    val bodyFont = FontFamily(
        Font(R.font.juliettbold)  // Ensure the file is in res/font/
    )


    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp, start = 4.dp),
                        color = Color.Transparent
                    ) {
                        Text(
                            text = "CaneConnect",
                            fontSize = 36.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 20.dp),
                            style = MaterialTheme.typography.displaySmall,
                            fontFamily = titleFont,
                            color = colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            NavigationGraph(navController, Modifier.padding(paddingValues), viewModel, dataViewModel)
        }
    }

}

