package com.example.mevodemo.viewModules

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mevodemo.mapView
import com.mapbox.bindgen.Value
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor

val tag = "Map Screen"

@Suppress("ktlint:standard:function-naming")
@Composable
fun MapView(
    viewModel: MapViewModel,
    currentCity: String,
) {
    Log.d("Map Screen", "Recomposing with city: $currentCity")

    LaunchedEffect(Unit) {
        viewModel.fetchVehicleData()
        viewModel.fetchIconBitmap()
        viewModel.fetchParkingZone()
        viewModel.setCurrentPoint()
    }

    // Observe ViewModel data
    val vehicleData by viewModel.vehicleData.collectAsState()
    val iconBitmap by viewModel.iconBitmap.collectAsState()
    val parkingZone by viewModel.parkingZone.collectAsState()
    val currentPoint by viewModel.currentPoint.collectAsState()

    // Log the collected state
    Log.d(tag, "Observed vehicle data: $vehicleData")
    Log.d(tag, "Observed bitmap map: $iconBitmap")
    Log.d(tag, "observed parking zone: $parkingZone")
    Log.d(tag, "observed current point: $currentPoint")
    if (vehicleData != null && iconBitmap != null && currentPoint != null) {
        val context = LocalContext.current

        val map = MapView(context)
        mapView = map

        mapView.let { theMapView ->
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    theMapView.apply {
                        theMapView.mapboxMap.setCamera(
                            CameraOptions
                                .Builder()
                                .center(currentPoint)
                                .zoom(11.7)
                                .build(),
                        )
                    }
                },
                update = { map ->
                    map.mapboxMap.apply {
                        setCamera(
                            CameraOptions
                                .Builder()
                                .center(currentPoint)
                                .zoom(11.7)
                                .build(),
                        )
                        loadStyle(Style.STANDARD) {
                            Log.d(tag, "Style loaded")
                            map.mapboxMap.style?.let { style ->
                                vehicleData?.let {
                                    iconBitmap?.let {
                                        parkingZone?.let {
                                            Log.d(tag, "Adding layer")
                                            addGeoJsonSource(
                                                style,
                                                vehicleData!!,
                                                iconBitmap!!,
                                                parkingZone!!,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
            )
        }
    }
}

fun addGeoJsonSource(
    style: Style,
    vehiclesMap: HashMap<String, Value>,
    iconBitmap: Bitmap,
    parkingZone: HashMap<String, Value>,
) {
    val imageId = "mevoVehicleLogo"
    style.addImage(imageId = imageId, bitmap = iconBitmap)

    val vehiclesSourceId = "vehicles-source"
    val expected = style.addStyleSource(vehiclesSourceId, Value(vehiclesMap))
    if (expected.isError) {
        Log.e(tag, "Invalid GeoJson: ${expected.error}")
        throw RuntimeException("Invalid GeoJson:" + expected.error)
    }

    style.addLayer(
        symbolLayer("vehicles-layer", vehiclesSourceId) {
            iconImage(imageId) // Assuming your GeoJSON features have an "icon-image" property
            iconSize(0.5)
            iconAnchor(IconAnchor.BOTTOM)
            iconAllowOverlap(true)
            iconIgnorePlacement(true)
        },
    )
    val parkingSourceId = "parking-source"
    val expectedParkingSource = style.addStyleSource(parkingSourceId, Value(parkingZone))
    if (expectedParkingSource.isError) {
        Log.e(tag, "Invalid GeoJson: ${expectedParkingSource.error}")
        throw RuntimeException("Invalid GeoJson:" + expectedParkingSource.error)
    }

    style.addLayer(
        lineLayer("parking-layer", parkingSourceId) {
            lineColor("#F7590d")
            lineWidth(4.0)
        },
    )

    Log.d(tag, "Layer Added")
}
