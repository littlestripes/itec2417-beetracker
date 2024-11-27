package com.example.beetrackerrough

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


/**
 * A simple [Fragment] subclass.
 * Use the [BeeBootScreen.newInstance] factory method to
 * create an instance of this fragment.
 */
class BeeBootScreen : Fragment() {

    private lateinit var submitButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bee_boot_screen, container, false)

        submitButton = view.findViewById(R.id.button)

        submitButton.setOnClickListener {
            //val fragment = BeeSightingUserInput()
            parentFragmentManager.beginTransaction().replace(R.id.bee_fragment_container, BeeSightingUserInput.newInstance(0.0, 0.0), "BEEUSERINPUT").addToBackStack("Beeuserinput").commit()

        }
        return view

    }




    companion object {
        @JvmStatic
        fun newInstance() = BeeBootScreen()
            }
    }
