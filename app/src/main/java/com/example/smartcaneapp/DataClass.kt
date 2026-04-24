package com.example.smartcaneapp

import com.google.gson.annotations.SerializedName

data class LocationData(
    val latitude: Double = 80.0,
    val longitude: Double = 12.0
)

data class GeocodingResponse(
    val results: List<GeocodingResult>,
    val status: String
)

data class GeocodingResult(
    val formatted_address: String
)
data class CaneLocation(val latitude: Double, val longitude: Double)


data class DirectionsResponse(
    @SerializedName("routes") val routes: List<Route>
)

data class Route(
    @SerializedName("overview_polyline") val overviewPolyline: OverviewPolyline,
    @SerializedName("legs") val legs: List<Leg> // ✅ Add this line
)
data class Leg(
    val steps: List<Step>
)


data class Step(
    @SerializedName("html_instructions") val htmlInstructions: String,
    val distance: Distance,
    @SerializedName("start_location") val startLocation: StepLocation // Rename from Location
)

data class Distance(
    val text: String
)

data class OverviewPolyline(
    @SerializedName("points") val points: String
)
data class NavigationStep(
    val stepNumber: Int = 0,
    val instruction: String = "",
    val distance: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val audioContent: String = ""
)
data class StepLocation( // Renamed from Location to StepLocation
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double
)

