package com.example.mevodemo.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MevoPublicAPI {
    private val _tag = "Api"
    private val baseURL = "https://api.mevo.co.nz/public"
    // "https://dev.api.mevo.co.nz/public"

    suspend fun mevoPublicApiCall(endpoint: String): String? {
        return withContext(Dispatchers.IO) {
            var responseData: String? = null

            Log.d(_tag, "Opening connection")
            val connection = URL(baseURL + endpoint).openConnection() as HttpsURLConnection
            Log.d(_tag, "Connection opened")

            connection.requestMethod = "GET"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.doInput = true

            try {
                val responseCode = connection.responseCode
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val response = inputStream.bufferedReader().use { it.readText().trim() }
                    Log.d(_tag, "Success: $response")

                    responseData = response
                } else {
                    // Handle HTTP error codes here, if necessary
                    Log.e(_tag, "Error: $responseCode")
                    return@withContext null
                }
            } catch (e: Exception) {
                // Log and handle the exception, return null or an appropriate default value
                e.printStackTrace()
                Log.e(_tag, "Error retrieving data", e)
                return@withContext null
            } finally {
                connection.disconnect()
            }

            return@withContext responseData
        }
    }
}
