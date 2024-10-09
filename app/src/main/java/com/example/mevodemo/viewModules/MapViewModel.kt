package com.example.mevodemo.viewModules

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mevodemo.currentCity
import com.example.mevodemo.services.ParkingService
import com.example.mevodemo.services.VehiclesService
import com.mapbox.bindgen.Value
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val vehiclesService = VehiclesService()
    private val parkingService = ParkingService()

    val wellingtonPoint = Point.fromLngLat(174.789, -41.2924)
    val aklPoint = Point.fromLngLat(174.75333, -36.85)

    // Mutable state for the vehicle data Map
    private val _vehicleData = MutableStateFlow<HashMap<String, Value>?>(null)
    val vehicleData: StateFlow<HashMap<String, Value>?> = _vehicleData

    private val _iconBitmap = MutableStateFlow<Bitmap?>(null)
    val iconBitmap: StateFlow<Bitmap?> = _iconBitmap

    private val _parkingZone = MutableStateFlow<HashMap<String, Value>?>(null)
    val parkingZone: StateFlow<HashMap<String, Value>?> = _parkingZone

    private val _currentPoint = MutableStateFlow<Point?>(null)
    val currentPoint: StateFlow<Point?> = _currentPoint

    fun setCurrentPoint() {
        viewModelScope.launch {
            if (currentCity.value == "Wellington") {
                _currentPoint.value = wellingtonPoint
            } else {
                _currentPoint.value = aklPoint
            }
        }
    }

    // fetch hashMap with feature collection
    fun fetchVehicleData() {
        viewModelScope.launch {
            vehiclesService.setVehiclesMap()
            val vehiclesMap = vehiclesService.vehiclesMap
            _vehicleData.value = vehiclesMap
        }
    }

    fun fetchIconBitmap() {
        viewModelScope.launch {
            val bitmapMap = vehiclesService.getImageFromFeatureCollection()
            _iconBitmap.value = bitmapMap
        }
    }

    fun fetchParkingZone() {
        viewModelScope.launch {
            parkingService.setParkingMap()
            val parkingGeoJson = parkingService.parkingMap
            _parkingZone.value = parkingGeoJson
        }
    }
}
