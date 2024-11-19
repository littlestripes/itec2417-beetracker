package com.example.beetrackerrough

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val TAG = "BEE_VIEW_MODEL"

class BeeViewModel: ViewModel() {

    // connect to firebase

    private val db = Firebase.firestore
    private val beeCollectionReference = db.collection("bees")

    val latestBees = MutableLiveData<List<Bee>>()


    fun addBee(bee: Bee) {
        beeCollectionReference.add(bee)
            .addOnSuccessListener { beeDocumentReference ->
                Log.d(TAG, "New bee sighting added at ${beeDocumentReference.path}")
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Error adding bee sighting record", error)
            }
    }
}