package com.example.beetrackerrough

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.GeoPoint
import java.util.Date
import java.util.UUID
import java.util.UUID.randomUUID

data class Bee(val sightingID: UUID? = randomUUID(),
               val numberBees: Int? = null,
               val location: GeoPoint? = null,
               val dateSpotted: Date? = null,
               val imageRef: String? = null, // placeholder for {imageRef}
               @get:Exclude @set:Exclude var documentReference: DocumentReference? = null
)
