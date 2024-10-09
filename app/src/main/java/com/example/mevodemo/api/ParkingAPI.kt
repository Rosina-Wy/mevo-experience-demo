package com.example.mevodemo.api

import android.util.Log
import com.example.mevodemo.currentCity

class ParkingAPI {
    private val tag = "parking Api"
    val mevoPublicAPI = MevoPublicAPI()

    var wgtnParkingEndpoint: String = "/parking/wellington"
    var aklParkingEndpoint: String = "/parking/auckland"

    suspend fun retrieveParkingData(): String? {
        val parkingData = mevoPublicAPI.mevoPublicApiCall(retrieveCorrectEndpoint())
        if (parkingData != null) {
            Log.d(tag, retrieveCorrectEndpoint())
            Log.d(tag, parkingData)
        } else {
            Log.e(tag, "parking api failure")
        }
        return parkingData
    }

    private fun retrieveCorrectEndpoint(): String {
        if (currentCity.value == "Wellington") {
            return wgtnParkingEndpoint
        } else if (currentCity.value == "Auckland") {
            return aklParkingEndpoint
        } else {
            return "endpoint failing"
        }
    }
}
