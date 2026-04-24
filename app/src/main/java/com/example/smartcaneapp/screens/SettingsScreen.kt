package com.example.smartcaneapp.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartcaneapp.FallDetectionService
import com.example.smartcaneapp.LocationViewModel
import com.example.smartcaneapp.ui.theme.SettingsCard
import com.google.maps.android.compose.MapType

@Composable
fun SettingsScreen(viewModel: LocationViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Optional padding for better spacing
        verticalArrangement = Arrangement.Top, // Aligns content to the top
        horizontalAlignment = Alignment.Start // Aligns content to start
    ) {
        SettingsCard("Notifications") { EnableBGServices() }
        Spacer(modifier = Modifier.height(16.dp))
        SettingsCard("Map Type") { MapTypeDropdown(viewModel) }
    }
}


@Composable
fun EnableBGServices() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    var isServiceRunning by remember {
        mutableStateOf(sharedPreferences.getBoolean("FallDetectionEnabled", false))
    }

    Switch(
        checked = isServiceRunning,
        onCheckedChange = { isChecked ->
            isServiceRunning = isChecked
            sharedPreferences.edit().putBoolean("FallDetectionEnabled", isChecked).apply()

            val intent = Intent(context, FallDetectionService::class.java)
            if (isChecked) {
                intent.action = "START_FOREGROUND"
                ContextCompat.startForegroundService(context, intent) // Start service
            } else {
                intent.action = "STOP_FOREGROUND"
                context.stopService(intent) // Stop service
            }
        },
        modifier = Modifier.padding(start = 8.dp),
        colors = SwitchDefaults.colors(
            checkedTrackColor = MaterialTheme.colorScheme.onSurface,
            checkedThumbColor = MaterialTheme.colorScheme.surface,

            uncheckedTrackColor = MaterialTheme.colorScheme.surface,
            uncheckedThumbColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    )
}

@Composable
fun MapTypeDropdown(viewModel: LocationViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val mapTypes = listOf("Normal", "Satellite", "Terrain", "Hybrid")
    val mapTypeValues = listOf(MapType.NORMAL, MapType.SATELLITE, MapType.TERRAIN, MapType.HYBRID)

    Box {
        Button(onClick = { expanded = true }) {
            Text("${viewModel.maptype.value}")
        }

        DropdownMenu(expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.size(200.dp)
        ) {
            mapTypes.forEachIndexed { index, type ->
                DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                        viewModel.maptype.value = mapTypeValues[index]
                        expanded = false
                    }
                )
            }
        }
    }
}
