package com.example.smartcaneapp.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartcaneapp.LocationData
import com.example.smartcaneapp.LocationViewModel
import com.example.smartcaneapp.R
import com.example.smartcaneapp.TextToSpeechHandler
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(
    navController: NavController,
    location: LocationData,
    viewModel: LocationViewModel,
    onLocationSelected: (LocationData) -> Unit){

    Column (
        modifier = Modifier.fillMaxSize()//.verticalScroll(rememberScrollState())
    ) {
        HomeScreenMapsCard(
            navController,
            location,
            viewModel,
            onLocationSelected
        )
/*        Spacer(modifier = Modifier.height(16.dp))
        val ttsInput = viewModel.ttsInput.joinToString(" ") { it.toString() }
        Text(text = ttsInput)*/

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreenMapsCard(
    navController: NavController,
    location: LocationData,
    viewModel: LocationViewModel,
    onLocationSelected: (LocationData) -> Unit) {

    val selectedMapType by remember { viewModel.maptype }
    val selectedLocation = remember{mutableStateOf(LatLng(location.latitude, location.longitude))}
    val caneLocation by viewModel.caneLocation.collectAsState() // Observe Live Updates
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(caneLocation.latitude,caneLocation.longitude), 10f)
    }
    // Move animation logic into LaunchedEffect
    LaunchedEffect(caneLocation) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(
                LatLng(caneLocation.latitude, caneLocation.longitude),
                17f
            )
        )
    }
    val routePoints by viewModel.routePoints

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            //elevation = CardDefaults.elevatedCardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
                .padding(start = 16.dp, end = 16.dp)

        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                //elevation = CardDefaults.elevatedCardElevation(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(550.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = true,
                        isBuildingEnabled = true,  // Disable buildings for better performance
                        isIndoorEnabled = false,    // Disable indoor maps
                        mapType = selectedMapType
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        compassEnabled = true,
                        rotationGesturesEnabled = true,
                        tiltGesturesEnabled = false,
                        scrollGesturesEnabled = true // ✅ Enable scrolling with one finger
                    ),
                    onMapClick = {
                        selectedLocation.value = it
                    }
                )
 {
                    val markerstate0 = rememberMarkerState(position = selectedLocation.value)
                    Marker(
                        state = markerstate0,
                        title = "Selected Location",
                        onClick = {
                            markerstate0.showInfoWindow()
                            true
                        }
                    )
                    LaunchedEffect(selectedLocation.value) {
                        val latLng = selectedLocation.value
                        val locationData = LocationData(latLng.latitude, latLng.longitude)

                        viewModel.updateSelectedLocation(locationData)
                        markerstate0.position = selectedLocation.value
                        markerstate0.showInfoWindow()
/*                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(
                                selectedLocation.value,
                                15f
                            ), // Set zoom level here
                            durationMs = 1000
                        )*/
                    }

                    //**********************************
                    /*val markerstate1 = rememberMarkerState(
                        position = LatLng(
                            myLocation!!.latitude,
                            myLocation!!.longitude
                        )
                    )
                    Marker(
                        state = markerstate1,
                        title = "Device Location",
                        snippet = "Live Location",
                        onClick = {
                            markerstate1.showInfoWindow()
                            true
                        }
                    )
                    LaunchedEffect(myLocation) {

                        val newLatLng = LatLng(myLocation!!.latitude, myLocation!!.longitude)

                        markerstate1.position = newLatLng
                        markerstate1.showInfoWindow()

                        *//*                cameraPositionState.animate(
                                            update = CameraUpdateFactory.newLatLngZoom(newLatLng, 15f), // Set zoom level here
                                            durationMs = 1000
                                        )*//*
                    }*/

                    //**********************************
                    val markerState2 = rememberMarkerState(
                        position = LatLng(
                            caneLocation.latitude,
                            caneLocation.longitude
                        )
                    )
                    Marker(
                        state = markerState2,
                        title = "SmartCane Location",
                        snippet = "Live Location",
                        onClick = {
                            markerState2.showInfoWindow()
                            true
                        },
                        icon = getResizedBitmapDescriptor(context, R.drawable.cane_marker, 120, 120) // Adjust width & height as needed
                    )

                    // Automatically show the info window when location updates
                    LaunchedEffect(caneLocation) {
                        val newLatLng = LatLng(caneLocation.latitude, caneLocation.longitude)

                        markerState2.position = newLatLng
                        markerState2.showInfoWindow()
                        /*                cameraPositionState.animate(
                                            update = CameraUpdateFactory.newLatLngZoom(newLatLng, 10f), // Set zoom level here
                                            durationMs = 1000
                                        )*/
                    }

                    // Draw Polyline for the route
                    if (routePoints.isNotEmpty()) {
                        Polyline(
                            points = routePoints,
                            color = Color.Blue,
                            width = 20f
                        )
                    }
                }
            }
            MapButtons(viewModel, LatLng(caneLocation.latitude, caneLocation.longitude), selectedLocation)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapButtons(viewModel: LocationViewModel, caneLocation: LatLng, selectedLocation: MutableState<LatLng>) {
    val context = LocalContext.current
    val ttsHandler = remember { TextToSpeechHandler(context) }
    val textList by remember { derivedStateOf { viewModel.ttsInput } }

    val isUploading = remember { mutableStateOf(false) } // Tracks upload state
    val uploadedCount = remember { mutableStateOf(0) }   // Tracks uploaded files
    val totalFiles = textList.size // Dynamically get the number of files
    val isButtonEnabled = remember { mutableStateOf(false) } // Track "Send Instructions" button state

    Row(
        modifier = Modifier.padding(bottom = 8.dp, start = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // "Get Route" Button
        Button(
            onClick = {
                viewModel.fetchDirections(
                    caneLocation,
                    selectedLocation.value,
                    BuildConfig.MAPS_API_KEY
                )
                isButtonEnabled.value = true // Enable "Send Instructions"
            },
        ) {
            Text("Get Route")
        }

        // "Send Instructions" Button
        Button(
            onClick = {
                if (textList.isEmpty()) {
                    Toast.makeText(context, "No instructions to upload!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isUploading.value = true
                uploadedCount.value = 0 // Reset counter

                ttsHandler.saveTextListToAudioFiles(textList, "speech_output") { success, message ->
                    isUploading.value = false // Ensure UI updates when all uploads complete
                    if (success) {
                        val flagref = FirebaseDatabase.getInstance().getReference("Sensors").child("gps").child("Nav_Flag")
                        Toast.makeText(context, "Upload completed successfully!", Toast.LENGTH_SHORT).show()
                        flagref.setValue(true) // ✅ Update Nav_Flag correctly
                    } else {
                        Toast.makeText(context, "Upload failed: $message", Toast.LENGTH_LONG).show()
                    }
                }
            },
            enabled = isButtonEnabled.value && !isUploading.value
        ) {
            if (isUploading.value) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Start Navigation")
            }
        }
    }
}



fun getResizedBitmapDescriptor(context: Context, resId: Int, width: Int, height: Int): BitmapDescriptor {
    val bitmap = BitmapFactory.decodeResource(context.resources, resId)
    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
    return BitmapDescriptorFactory.fromBitmap(resizedBitmap)
}
