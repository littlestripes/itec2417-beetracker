package com.example.beetrackerrough

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
import com.google.android.gms.maps.model.Marker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.GeoPoint
import java.util.UUID


private const val TAG = "BEE_SIGHTING_USER_INPUT_FRAGMENT"
private const val LATITUDE = "latitude"
private const val LONGITUDE = "longitude"
/**
 * A simple [Fragment] subclass.
 * Use the [BeeSightingUserInput.newInstance] factory method to
 * create an instance of this fragment.
 */
class BeeSightingUserInput : Fragment() {
    private var latitude: Double? = null
    private var longitude: Double? = null

    private var beeUserSightingNumInput: Int = 0

    // need to add a bundle to persist data in this fragment while moving between fragments

    private lateinit var increaseButton: Button
    private lateinit var decreaseButton: Button
    private lateinit var beeNumber: EditText
    private lateinit var getLocationButton: Button
    private lateinit var submitButton: Button

    private var locationPermissionGranted = false

    private var moveMapToUsersLocation = false

    private var fusedLocationProvider: FusedLocationProviderClient? = null

    private var map: GoogleMap? = null

    //private val beeMarkers = mutableListOf<Marker>()

    //private var beeList: listOf<Bee>()


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
//    }

    @SuppressLint("MissingPermission")
    private fun addBeeAtLocation() {
        if (map == null) { return }
        if (fusedLocationProvider == null) { return }
        if (!locationPermissionGranted) {
            showSnackbar(getString(R.string.give_permission))
            return
        }

        // implement view model and come back to line 124

        fusedLocationProvider?.lastLocation?.addOnCompleteListener(requireActivity()) { locationRequestTask ->
            val location = locationRequestTask.result
            if (location != null) {
                val bee = Bee(
                    sightingID = UUID.randomUUID(),
                    numberBees = beeUserSightingNumInput,
                    location = GeoPoint(location.latitude, location.longitude),
                    imageRef = 1
                )
                beeViewModel.addBee(bee)
                moveMapToUserLocation()
                showSnackbar(getString(R.string.bee_sighting_added))
            } else {
                showSnackbar(getString(R.string.no_location))
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_bee_sighting_user_input, container, false)

        val latitude: Double = requireArguments().getDouble(LATITUDE)
        val longitude: Double = requireArguments().getDouble(LONGITUDE)

        increaseButton = view.findViewById(R.id.increaseButton)
        decreaseButton = view.findViewById(R.id.decreaseButton)
        beeNumber = view.findViewById(R.id.beeNumber)
        getLocationButton = view.findViewById(R.id.getUserLocationButton)
        submitButton = view.findViewById(R.id.submitButton)

        increaseButton.setOnClickListener {
            beeUserSightingNumInput++
            val beeNum = beeUserSightingNumInput.toString()
            beeNumber.setText(beeNum)
        }

        decreaseButton.setOnClickListener {
            if (beeUserSightingNumInput > 0) {
                beeUserSightingNumInput--
                val beeNum = beeUserSightingNumInput.toString()
                beeNumber.setText(beeNum)
            }
        }


        getLocationButton.setOnClickListener {
            //requestLocationPermission()
            moveMapToUserLocation()

        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment?.getMapAsync(mapReadyCallback)

    // holding code to be replaced with send data to view model - linked to repository - upload to database
        submitButton.setOnClickListener {
            addBeeAtLocation()

            /*parentFragmentManager
                .beginTransaction()
                .replace(R.id.bee_fragment_container, BeeBootScreen.newInstance(), "BEEBOOTSCREEN").addToBackStack("Beebootscreen")
                .commit()*/

        requestLocationPermission()
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(latitude:Double?, longitude: Double?) = BeeSightingUserInput().apply {
            arguments = Bundle().apply {
                putDouble(LATITUDE, latitude!!)
                putDouble(LONGITUDE, longitude!!)
            }
        }
    }
}