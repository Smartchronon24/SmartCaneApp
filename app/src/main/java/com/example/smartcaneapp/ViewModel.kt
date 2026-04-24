package com.example.smartcaneapp

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.MapType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.awaitResponse

class LocationViewModel: ViewModel() {

    private  val _selectedLocation= mutableStateOf<LocationData?>(null)
    val selectedLocation : MutableState<LocationData?> = _selectedLocation

/*    private  val _myLocation= mutableStateOf<LocationData?>(null)
    val myLocation : MutableState<LocationData?> = _myLocation*/

    private  val _maptype = mutableStateOf<MapType>(MapType.NORMAL)
    val maptype : MutableState<MapType> = _maptype

    private val _caneLocation = MutableStateFlow(CaneLocation(0.0, 0.0))
    val caneLocation = _caneLocation.asStateFlow()

    private val _ttsInput = mutableStateListOf<String>() // ✅ State-aware list
    val ttsInput: List<String> = _ttsInput // ✅ Expose as an immutable list

    private  val _isUploading= mutableStateOf<Boolean?>(false)
    val isUploading : MutableState<Boolean?> = _isUploading

    fun updateCaneLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            _caneLocation.value = CaneLocation(lat, lon)
        }
    }

    private val _address = mutableStateOf(listOf<GeocodingResult>())
    val address: State<List<GeocodingResult>> = _address

    fun updateSelectedLocation(newLocation: LocationData) {
        _selectedLocation.value = newLocation
    }

/*    fun updateMyLocation(newLocation: LocationData){
        _myLocation.value = newLocation
    }*/

    fun fetchAddress(latlng: String){
        try{
            viewModelScope.launch {
                val result = RetrofitClient.create().getAddressFromCoordinates(
                    latlng,
                    BuildConfig.MAPS_API_KEY
                )
                _address.value = result.results
            }
        }catch(e:Exception) {
            Log.d("res1", "${e.cause} ${e.message}")
        }
    }

    var routePoints = mutableStateOf<List<LatLng>>(emptyList())

    fun fetchDirections(caneLocation: LatLng, selectedLocation: LatLng, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getDirections(
                    "${caneLocation.latitude},${caneLocation.longitude}",
                    "${selectedLocation.latitude},${selectedLocation.longitude}",
                    apiKey
                ).awaitResponse()

                if (response.isSuccessful) {
                    val route = response.body()?.routes?.firstOrNull()
                    val polyline = route?.overviewPolyline?.points
                    val leg = route?.legs?.firstOrNull()

                    if (!polyline.isNullOrEmpty()) {
                        routePoints.value = PolyUtil.decode(polyline)
                    }

                    if (leg != null && leg.steps.isNotEmpty()) {
                        val stepsList = mutableListOf<NavigationStep>()
                        val newInstructions = mutableListOf<String>() // Temporary list for new instructions

                        leg.steps.forEachIndexed { index, step ->
                            val instruction = step.htmlInstructions.replace(Regex("<.*?>"), "")
                            newInstructions.add(instruction) // Collect new instructions

                            val navStep = NavigationStep(
                                stepNumber = index + 1,
                                instruction = instruction,
                                distance = step.distance.text,
                                latitude = step.startLocation.lat,
                                longitude = step.startLocation.lng,
                                audioContent = ""
                            )
                            stepsList.add(navStep)
                        }

                        // Update the ttsInput **after** collecting all new instructions
                        _ttsInput.clear()
                        _ttsInput.addAll(newInstructions)

                        uploadToFirestore(stepsList)
                    }
                }
            } catch (e: Exception) {
                Log.e("DirectionsAPI", "Error fetching directions: ${e.message}")
            }
        }
    }


    fun uploadToFirestore(steps: List<NavigationStep>, userId: String = "", caneId: String = "") {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Users").document("user001") // Use a specific document name or auto-generate

        viewModelScope.launch {
            try {
                docRef.update(mapOf("Routedetails" to steps)).await() // ✅ Ensure proper structure
                Log.d("Firestore", "Navigation steps uploaded successfully!")
            } catch (e: Exception) {
                Log.e("Firestore", "Error uploading navigation steps: ${e.message}")
            }
        }
    }

}