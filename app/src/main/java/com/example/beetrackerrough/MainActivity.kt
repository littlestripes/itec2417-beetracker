package com.example.beetrackerrough

import android.os.Bundle
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

//    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.bee_fragment_container)

        if (currentFragment == null) {
            val fragment = BeeBootScreen()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.bee_fragment_container, fragment)
                .commit()
        }


        }
    }
