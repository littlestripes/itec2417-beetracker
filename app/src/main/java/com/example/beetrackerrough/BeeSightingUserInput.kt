package com.example.beetrackerrough

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText


/**
 * A simple [Fragment] subclass.
 * Use the [BeeSightingUserInput.newInstance] factory method to
 * create an instance of this fragment.
 */
class BeeSightingUserInput : Fragment() {

    var beeUserSightingNumInput: Int = 0

    private lateinit var increaseButton: Button
    private lateinit var decreaseButton: Button
    private lateinit var beeNumber: EditText
    private lateinit var submitButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_bee_sighting_user_input, container, false)

        increaseButton = view.findViewById(R.id.increaseButton)
        decreaseButton = view.findViewById(R.id.decreaseButton)
        beeNumber = view.findViewById(R.id.beeNumber)
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


    // holding code to be replaced with send data to view model - linked to repository - upload to database
        submitButton.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.bee_fragment_container, BeeBootScreen.newInstance(), "BEEBOOTSCREEN").addToBackStack("Beebootscreen")
                .commit()

        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = BeeSightingUserInput()
    }
}