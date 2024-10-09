package com.example.mevodemo.api

import android.util.Log
import com.example.mevodemo.currentCity

class VehiclesAPI {
    private val tag = "vehicle Api"
    val mevoPublicAPI = MevoPublicAPI()
    val wgtnVehiclesEndpoint = "/vehicles/wellington"
    val aklVehiclesEndpoint = "/vehicles/auckland"

    suspend fun retrieveVehicleData(): String? {
        val vehiclesData = mevoPublicAPI.mevoPublicApiCall(retrieveCorrectEndpoint(currentCity.value))
        if (vehiclesData != null) {
            Log.d(tag, vehiclesData)
        }
        return vehiclesData
    }

    private fun retrieveCorrectEndpoint(currentCity: String): String {
        if (currentCity == "Wellington") {
            return wgtnVehiclesEndpoint
        } else if (currentCity == "Auckland") {
            return aklVehiclesEndpoint
        } else {
            return "endpoint failing"
        }
    }
}
