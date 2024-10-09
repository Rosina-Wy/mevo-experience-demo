package com.example.mevodemo

import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.mevodemo.ui.theme.MevoDemoTheme
import com.example.mevodemo.viewModules.MapView
import com.example.mevodemo.viewModules.MapViewModel
import com.mapbox.maps.MapView
import kotlinx.coroutines.flow.MutableStateFlow

var currentCity = MutableStateFlow("Wellington")
var mapViewModel = MapViewModel()

// This will allow us to control the map lifecycle in Compose
lateinit var mapView: MapView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MyAppContent()
            MevoDemoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { MenuView() },
                    floatingActionButton = {
                        ChangeCitiesButton {
                            changeCity() // Now calling ChangeCity from the correct context
                        }
                    },
                    contentColor = Color.White,
                ) { innerPadding ->
                    val citiesState by currentCity.collectAsState()
                    MapView(viewModel = mapViewModel, currentCity = citiesState)
                }
            }
        }
    }

    private fun changeCity() {
        Log.d("current City", currentCity.value)
        if (currentCity.value == "Wellington") {
            currentCity.value = "Auckland"
        } else {
            currentCity.value = "Wellington"
        }
        mapViewModel.setCurrentPoint()
        mapViewModel.fetchParkingZone()
        mapViewModel.fetchVehicleData()
        Log.d("new City", currentCity.value)
    }

    @Suppress("ktlint:standard:function-naming")
    @Composable
    fun ChangeCitiesButton(changeCity: () -> Unit) {
        Row {
            FloatingActionButton(
                onClick = { changeCity() },
                modifier =
                    Modifier
                        .height(48.dp)
                        .width(150.dp)
                        .padding(0.dp)
                        .zIndex(1f),
                containerColor = Color(0xFF00afdd),
                contentColor = Color.White,
            ) {
                Text(
                    text = "Change Cities",
                    color = Color.White,
                    style =
                        TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                )
            }
        }
    }

    @Suppress("ktlint:standard:function-naming")
    @Composable
    fun MenuView() {
        Column(
            modifier =
                Modifier
                    .zIndex(1f)
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        Color(0xFF00afdd),
                    ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(
                modifier =
                    Modifier
                        .height(16.dp),
            )
            Image(
                modifier = Modifier,
                painter = painterResource(id = R.drawable.logo_light),
                contentDescription = null,
                contentScale = ContentScale.Fit,
            )
        }
    }

    @Suppress("ktlint:standard:function-naming")
    @Composable
    fun MyAppContent() {
        // Get the current window and WindowInsetsController
        val view = LocalView.current
        val window = (view.context as? ComponentActivity)?.window

        window?.let {
            // Set the status bar color
            it.statusBarColor = android.graphics.Color.TRANSPARENT

            // Set the status bar icons to light (white)
            val insetsController = it.insetsController
            Log.d("status bar", insetsController.toString())
            insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            )

            // Optionally set the navigation bar icons to light (white)
            insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
            )
        }
    }
}
