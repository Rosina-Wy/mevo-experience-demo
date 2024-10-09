package com.example.mevodemo.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.mevodemo.api.VehiclesAPI
import com.mapbox.bindgen.Value
import com.mapbox.geojson.FeatureCollection
import com.mapbox.maps.extension.style.utils.toValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class VehiclesService {
    private val tag = "Vehicles Service"
    private val vehiclesApi = VehiclesAPI()
    private var response: String? = null

    var vehiclesMap: HashMap<String, Value>? = hashMapOf()

    suspend fun setVehiclesMap() {
        getVehiclesData()
        vehiclesMap?.set("type", Value("geojson"))
        val featureCollection = makeVehiclesFeatureCollection()
        if (featureCollection != null) {
            vehiclesMap?.set("data", featureCollection.toValue())
        } else {
            Log.e(tag, "Feature Collection is Empty")
        }
    }

    suspend fun getVehiclesData() {
        response = vehiclesApi.retrieveVehicleData()
    }

    private fun makeVehiclesFeatureCollection(): FeatureCollection? {
        var featureCollection: FeatureCollection? = null
        if (response != null) {
            val jsonObject = JSONObject(response!!)
            val geoJsonString = jsonObject.getJSONObject("data").toString()
            featureCollection = FeatureCollection.fromJson(geoJsonString)
            Log.d(tag, "Feature Collection Populated?: $featureCollection")
        }
        if (featureCollection != null) {
            return featureCollection
        } else {
            return null
        }
    }

    suspend fun getImageFromFeatureCollection(): Bitmap? {
        getVehiclesData()
        val featureCollection = makeVehiclesFeatureCollection()
        var bitmap: Bitmap? = null
        val feature = featureCollection?.features()?.get(1)
        val iconUrl = feature?.getStringProperty("iconUrl")
        if (iconUrl != null) {
            bitmap = downloadImage(iconUrl)
        }

        if (bitmap != null) {
            if (!bitmap.isRecycled) {
                return bitmap
            } else {
                Log.e(tag, "bitmap is recycled; can't be used")
                return null
            }
        } else {
            Log.e(tag, "bitmap is null")
            return null
        }
    }

    private suspend fun downloadImage(url: String): Bitmap? =
        withContext(Dispatchers.IO) {
            val inputStream = URL(url).openStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            if (bitmap != null) {
                return@withContext bitmap
            } else {
                return@withContext null
            }
        }
}
