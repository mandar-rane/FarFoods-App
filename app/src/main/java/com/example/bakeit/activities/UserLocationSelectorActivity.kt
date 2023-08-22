package com.example.bakeit.activities

import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.bakeit.API.ApiInterface
import com.example.bakeit.LocationServices.LocationService
import com.example.bakeit.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.bakeit.databinding.ActivityUserLocationSelectorBinding
import com.example.bakeit.models.StandardResponse
import com.example.bakeit.models.UserAddress
import com.example.bakeit.models.loginApiResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class UserLocationSelectorActivity : AppCompatActivity(), OnMapReadyCallback,
    LocationService.LocationCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityUserLocationSelectorBinding
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserLocationSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()

        binding.enterCompleteAddressBtn.setOnClickListener {
            openCompleteAddressFormBottomSheet()
        }

    }

    private fun openCompleteAddressFormBottomSheet() {
        val bottomSheetView =
            LayoutInflater.from(this).inflate(R.layout.complete_address_form_bottom_sheet, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

        val addrTag = bottomSheetDialog.findViewById<EditText>(R.id.address_tag_et)
        val premisesAddress = bottomSheetDialog.findViewById<EditText>(R.id.premises_address_et)
        val localityAddress = bottomSheetDialog.findViewById<EditText>(R.id.locality_address_et)
        val landmarkAddress = bottomSheetDialog.findViewById<EditText>(R.id.nearby_landmark_address_et)

        val compAddress = premisesAddress?.text.toString() + localityAddress?.text.toString() + "(Landmark: ${landmarkAddress?.text.toString()}) "

        val addressToAdd = UserAddress(addrTag?.text.toString(), compAddress, 12345678.0, 12345678.0)

        bottomSheetDialog.findViewById<Button>(R.id.save_complete_address_btn)?.setOnClickListener {
            updateUserAddress(addressToAdd)
        }

    }

    private fun updateUserAddress(addressToAdd: UserAddress) {
        val userID = UserPreferences(this@UserLocationSelectorActivity).getUser()!!._id
        ////
        Log.d("prefcheck", userID.toString())
        val updateAddressResponse = ApiInterface.ApiService.ApiInstance.addAddress(JwtManager.JwtManager.getToken(this).toString(),userID,addressToAdd)

        updateAddressResponse.enqueue(object: Callback<StandardResponse> {
            override fun onResponse(call: Call<StandardResponse>, response: Response<StandardResponse>) {
                val updateAddressResp: StandardResponse? = response.body()
                if (updateAddressResp != null){
                    if (updateAddressResp.success){
                        Toast.makeText(this@UserLocationSelectorActivity, updateAddressResp.message, Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Log.d("updateddresslog","null for ${userID}")
                }
            }
            override fun onFailure(call: Call<StandardResponse>, t: Throwable) {
                Log.d("updateddresslog","api error")
            }
        })

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionCode ->
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    fetchLocation()
                }
        }
    }

    private fun fetchLocation() {
        LocationService().locationFetcher(this, this)

    }

    private fun Geocoder.getAddress(
        latitude: Double,
        longitude: Double,
        address: (Address?) -> Unit
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getFromLocation(latitude, longitude, 1) { address(it.firstOrNull()) }
            return
        }

        try {
            address(getFromLocation(latitude, longitude, 1)?.firstOrNull())
        } catch (e: Exception) {
            //will catch if there is an internet problem
            address(null)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("I am here!")
        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19f))
        val marker = googleMap?.addMarker(markerOptions)
        if (marker != null) {
            marker.isDraggable = true
        }

        LocationService.GeocoderUtil.getAddress(
            this,
            latLng.latitude,
            latLng.longitude
        ) { address: Address? ->

            runOnUiThread {
                if (address != null) {
                    val locality = address.locality ?: ""
                    val thoroughfare = address.thoroughfare ?: ""
                    val subAdmin = address.subAdminArea ?: ""
                    val subLocality = address.subLocality ?: ""

                    val formattedAddress = "$thoroughfare, $locality"
                    binding.subTownAddressTv.text = subLocality
                    binding.formattedAddressTv.text = address.getAddressLine(0)

                } else {
                    Log.d("geocheck", "null")
                }
            }
        }

        googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(p0: Marker) {
                Log.d("geocheck", "dragging")
            }

            override fun onMarkerDragEnd(p0: Marker) {
                binding.subTownAddressTv.visibility = View.VISIBLE
                binding.formattedAddressTv.visibility = View.VISIBLE
                val newLatLng = p0.position
                Log.d("geocheck", newLatLng.toString())
                Geocoder(this@UserLocationSelectorActivity, Locale("in"))
                    .getAddress(
                        newLatLng.latitude,
                        newLatLng.longitude
                    ) { address: Address? ->

                        runOnUiThread {
                            if (address != null) {
                                val locality = address.locality ?: ""
                                val thoroughfare = address.thoroughfare ?: ""
                                val subAdmin = address.subAdminArea ?: ""
                                val subLocality = address.subLocality ?: ""
                                val formattedAddress = "$thoroughfare, $locality"
                                binding.subTownAddressTv.text = subLocality
                                binding.formattedAddressTv.text = address.getAddressLine(0)

                            } else {

                                Log.d("geocheck", "null")
                            }
                        }
                    }

            }

            override fun onMarkerDragStart(p0: Marker) {
                binding.subTownAddressTv.visibility = View.INVISIBLE
                binding.formattedAddressTv.visibility = View.INVISIBLE
            }

        })


    }

    override fun onLocationReceived(location: Location) {
        currentLocation = location
        val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.map) as
                SupportMapFragment?)!!
        supportMapFragment.getMapAsync(this)
    }

    override fun onLocationError(errorMessage: String) {
        Log.d("locerr", errorMessage)
    }

}