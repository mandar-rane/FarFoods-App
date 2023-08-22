package com.example.bakeit.LocationServices

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.core.app.ActivityCompat
import com.example.bakeit.activities.UserLocationSelectorActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class LocationService {


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101

    interface LocationCallback {
        fun onLocationReceived(location: Location)
        fun onLocationError(errorMessage: String)
    }
    fun locationFetcher(context: Context, callback: LocationCallback) {
        if (ActivityCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), permissionCode
            )
            callback.onLocationError("Location permission not granted")
            return
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                callback.onLocationReceived(location)
            }else{
                callback.onLocationError("Location not available")
            }
        }
    }
    object GeocoderUtil {
        fun getAddress(
            context: Context, // You need a context to use Geocoder
            latitude: Double,
            longitude: Double,
            addressCallback: (Address?) -> Unit
        ) {
            val geocoder = Geocoder(context, Locale.getDefault())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    addressCallback(addresses.firstOrNull())
                }
            } else {
                try {
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    val address = addresses?.firstOrNull()
                    addressCallback(address)
                } catch (e: Exception) {
                    addressCallback(null)
                }
            }
        }
    }



}