package com.example.smartcaneapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults.colors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.smartcaneapp.screens.DataDisplayScreen
import com.example.smartcaneapp.screens.HomeScreen
import com.example.smartcaneapp.screens.SettingsScreen

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    data object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    data object Session : BottomNavItem("data", "Stats", Icons.Default.Menu)
    data object Settings : BottomNavItem("settings", "Settings", Icons.Default.Settings)
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(BottomNavItem.Home, BottomNavItem.Session, BottomNavItem.Settings)
    val currentRoute = currentRoute(navController)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            containerColor = colorScheme.primaryContainer,
            contentColor = colorScheme.onPrimaryContainer
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.title) },
                    label = { Text(item.title) },
                    selected = currentRoute == item.route,
                    colors = colors(
                        indicatorColor = colorScheme.secondaryContainer,
                        selectedIconColor = colorScheme.onSecondaryContainer,
                        selectedTextColor = colorScheme.onSecondaryContainer,
                        unselectedIconColor = colorScheme.onSurfaceVariant,
                        unselectedTextColor = colorScheme.onSurfaceVariant,
                    ),
                    onClick = { navController.navigate(item.route) }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NavigationGraph(navController: NavHostController ,modifier: Modifier, viewModel: LocationViewModel, dataViewModel: DataViewModel) {
    val context = LocalContext.current
    val locationUtils = LocationUtils(context = context)
    val requestPermissionLauncher = permissionsChecker(viewModel, context, locationUtils)


    NavHost(navController, startDestination = BottomNavItem.Home.route, modifier = modifier) {

        //composable(BottomNavItem.Home.route) { HomeScreen(viewModel, navController) }
        composable(BottomNavItem.Session.route) { DataDisplayScreen(dataViewModel) }
        composable(BottomNavItem.Settings.route) { SettingsScreen(viewModel) }
        composable(BottomNavItem.Home.route) {
            LaunchedEffect(Unit) { // Ensures it runs once
                if (locationUtils.hasLocationPermission(context)) {
                    if (locationUtils.isLocationEnabled(context)) {
                        locationUtils.requestCurrentLocationUpdates(viewModel)
                    } else {
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        Toast.makeText(context, "Please enable GPS", Toast.LENGTH_LONG).show()
                    }
                } else {
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }

            viewModel.selectedLocation.value?.let { location ->
                HomeScreen(
                    navController = navController,
                    location = location,
                    viewModel = viewModel,
                    onLocationSelected = { locationdata ->
                        viewModel.fetchAddress("${locationdata.latitude},${locationdata.longitude}")
                    }
                )
            } ?: Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

    }
}

@Composable
fun permissionsChecker(viewModel: LocationViewModel, context: Context, locationUtils: LocationUtils): ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>> {

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions() ,
        onResult = { permissions ->
            if(permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true){
                // I HAVE ACCESS to location

                locationUtils.requestCurrentLocationUpdates(viewModel = viewModel)  //GETS AND UPDATES LOCATION IN VIEWMODEL
            }else{
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                if(rationaleRequired){
                    Toast.makeText(context,
                        "Location Permission is required for this feature to work", Toast.LENGTH_LONG)
                        .show()
                }else{
                    Toast.makeText(context,
                        "Location Permission is required. Please enable it in the Android Settings",
                        Toast.LENGTH_LONG)
                        .show()
                }
            }
        })
    return requestPermissionLauncher
}