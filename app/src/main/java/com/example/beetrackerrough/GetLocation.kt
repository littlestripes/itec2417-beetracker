package com.example.beetrackerrough

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.GeoPoint
import java.util.Timer
import java.util.UUID
import java.util.UUID.randomUUID
import kotlin.concurrent.schedule

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [GetLocation.newInstance] factory method to
 * create an instance of this fragment.
 */

private const val TAG = "GET_LOCATION_FRAGMENT"

class GetLocation : Fragment() {

    private var locationPermissionGranted = false

    private var moveMapToUsersLocation = false

    private var fusedLocationProvider: FusedLocationProviderClient? = null

    private var map: GoogleMap? = null

    //private var beeUserSightingNumInput: Int = 0

    //private var userLocationGeoPoint: GeoPoint? = GeoPoint(40.7218, -74.0060)

    var latitude: Double? = null

    var longitude: Double? = null

    private lateinit var instructionsTextView: TextView

    private lateinit var setLocationButton: FloatingActionButton

    private val beeViewModel: BeeViewModel by lazy {
        ViewModelProvider(requireActivity()).get(BeeViewModel::class.java)
    }

    private val mapReadyCallback = OnMapReadyCallback { googleMap ->
        Log.d(TAG, "Google map ready")
        map = googleMap

        updateMap()
    }

    @SuppressLint("MissingPermission")
    private fun moveMapToUserLocation() {
        if (map == null) {
            return
        }

        if (locationPermissionGranted) {
            map?.isMyLocationEnabled = true
            map?.uiSettings?.isMyLocationButtonEnabled = true
            map?.uiSettings?.isZoomControlsEnabled = true

            fusedLocationProvider?.lastLocation?.addOnCompleteListener { getLocationTask ->
                val location = getLocationTask.result
                if (location != null) {
                    Log.d(TAG, "User's location $location")
                    val center = LatLng(location.latitude, location.longitude)
                    val zoomLevel = 8f
                    map?.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoomLevel))
                    moveMapToUsersLocation = true
                } else {
                    showSnackbar(getString(R.string.no_location))
                }
            }
        }

    }

    private fun updateMap() {
        if (locationPermissionGranted) {
            if (!moveMapToUsersLocation) {
                moveMapToUserLocation()
            }
        }
    }

    private fun requestLocationPermission() {
        //if (isAdded()) {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
            Log.d(TAG, "permission already granted")
            updateMap()
            fusedLocationProvider =
                LocationServices.getFusedLocationProviderClient(requireActivity())
        } else {
            val requestLocationPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                    if (granted) {
                        Log.d(TAG, "User granted permission")
                        locationPermissionGranted = true
                        fusedLocationProvider =
                            LocationServices.getFusedLocationProviderClient(requireActivity())
                    } else {
                        Log.d(TAG, "User did not grant permission")
                        locationPermissionGranted = false
                        showSnackbar(getString(R.string.give_permission))
                    }
                    updateMap()
                }
            requestLocationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    @SuppressLint("MissingPermission")
    private fun addLocation() {
        if (map == null) { return }
        if (fusedLocationProvider == null) { return }
        if (!locationPermissionGranted) {
            showSnackbar(getString(R.string.no_location))
            return
        }
        fusedLocationProvider?.lastLocation?.addOnCompleteListener(requireActivity()) { locationRequestTask ->
            val location = locationRequestTask.result
            if (location != null) {
                //userLocationGeoPoint = GeoPoint(location.latitude, location.longitude)
                latitude = location.latitude
                longitude = location.longitude

                Toast.makeText(context, "Latitude $latitude, Longitude: $longitude", Toast.LENGTH_LONG).show()

            }
        }
    }

    /*private fun geopointLatitude(): Double?  {
        val latitude= userLocationGeoPoint?.latitude
        return  latitude
    }

    private fun geopointLongitude(): Double? {
        val longitude = userLocationGeoPoint?.longitude
        return longitude
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.get_location_screen, container, false)

        setLocationButton = view.findViewById(R.id.setLocationButton)
        instructionsTextView = view.findViewById(R.id.clickInstructions)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment?.getMapAsync(mapReadyCallback)

        requestLocationPermission()

        setLocationButton.setOnClickListener {
            moveMapToUserLocation()
            addLocation()
            Timer().schedule(3000) {
                parentFragmentManager.beginTransaction().replace(
                    R.id.bee_fragment_container,
                    BeeSightingUserInput.newInstance(latitude!!, longitude!!),
                    "USERLOCATION"
                ).commit()
            }
        }



        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment GetLocation.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = GetLocation()
    }
}