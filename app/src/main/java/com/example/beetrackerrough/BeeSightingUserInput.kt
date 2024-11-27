package com.example.beetrackerrough

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.GeoPoint
import java.util.Date
import java.util.Timer
import java.util.UUID
import kotlin.concurrent.schedule


private const val TAG = "BEE_SIGHTING_USER_INPUT_FRAGMENT"
private const val LAT = "latitude"
private const val LONG = "longitude"

/**
 * A simple [Fragment] subclass.
 * Use the [BeeSightingUserInput.newInstance] factory method to
 * create an instance of this fragment.
 */
class BeeSightingUserInput : Fragment() {
    private var userLocation: GeoPoint? = null


    private var beeUserSightingNumInput: Int = 0

    // need to add a bundle to persist data in this fragment while moving between fragments

    private lateinit var increaseButton: Button
    private lateinit var decreaseButton: Button
    private lateinit var beeNumber: EditText
    private lateinit var getLocationButton: Button
    private lateinit var submitButton: Button

    private val beeViewModel: BeeViewModel by lazy {
        ViewModelProvider(requireActivity()).get(BeeViewModel::class.java)
    }

    //private val beeMarkers = mutableListOf<Marker>()

    //private var beeList: listOf<Bee>()

    private fun toGeoPoint(userLocationLat: Double, userlocationLong: Double) {

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_bee_sighting_user_input, container, false)

        val userLocationLat: Double = requireArguments().getDouble(LAT)
        val userlocationLong: Double = requireArguments().getDouble(LONG)
        val newUserLocation: GeoPoint = GeoPoint(userLocationLat, userlocationLong)






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
            parentFragmentManager.beginTransaction().replace(R.id.bee_fragment_container, GetLocation.newInstance(), "GETLOCATION").commit()

        }



    // holding code to be replaced with send data to view model - linked to repository - upload to database
        submitButton.setOnClickListener {
            val bee = Bee(
                sightingID = UUID.randomUUID(),
                numberBees = beeUserSightingNumInput,
                dateSpotted = Date(),
                location = newUserLocation,
                imageRef = 0

            )
            beeViewModel.addBee(bee)

            Toast.makeText(context, "Thanks for submitting your data in the bee study!", Toast.LENGTH_LONG).show()

            Timer().schedule(2000) {
                parentFragmentManager.beginTransaction().replace(
                    R.id.bee_fragment_container,
                    BeeBootScreen.newInstance(),
                    "SubmittedData"
                ).commit()
            }

        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(latitude: Double, longitude: Double) = BeeSightingUserInput().apply {
            arguments = Bundle().apply {
                putDouble(LAT, latitude)
                putDouble(LONG, longitude)
            }
        }
    }
}