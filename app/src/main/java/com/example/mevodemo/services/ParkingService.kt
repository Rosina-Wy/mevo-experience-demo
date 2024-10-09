package com.example.mevodemo.services

import android.util.Log
import com.example.mevodemo.api.ParkingAPI
import com.mapbox.bindgen.Value
import com.mapbox.geojson.Feature
import com.mapbox.maps.extension.style.utils.toValue
import org.json.JSONObject

class ParkingService {
    private val tag = "Parking Service"
    private val parkingApi = ParkingAPI()
    private var response: String? = null

    var parkingMap: HashMap<String, Value>? = hashMapOf()

    suspend fun setParkingMap() {
        getParkingData()
        parkingMap?.set("type", Value("geojson"))

        val parkingFeature = makeParkingFeature()
        if (parkingFeature != null) {
            parkingMap?.set("data", parkingFeature.toValue())
        } else {
            Log.e(tag, "Feature is null")
        }
    }

    private suspend fun getParkingData() {
        response = parkingApi.retrieveParkingData()
    }

    private suspend fun makeParkingFeature(): Feature? {
        getParkingData()
        var parkingFeature: Feature? = null
        if (response != null) {
            val jsonObject = JSONObject(response!!)
            val geoJsonString = jsonObject.getJSONObject("data").toString()
            parkingFeature = Feature.fromJson(geoJsonString)
            Log.d(tag, "Feature Collection Populated?: $parkingFeature")
        }
        if (parkingFeature != null) {
            return parkingFeature
        } else {
            return null
        }
    }
}
