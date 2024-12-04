package com.example.beetrackerrough

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.telephony.MbmsDownloadSession.RESULT_CANCELLED
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

const val TAG = "BeeSightingUserInputFragment"

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
    private lateinit var cameraButton: ImageButton

    // image handling
    private var newPhotoPath: String? = null
    private var visibleImagePath: String? = null
    private var imageFilename: String? = null
    private var photoUri: Uri? = null

    // bundle keys
    private val NEW_PHOTO_PATH_KEY = "new photo path key"
    private val VISIBLE_IMAGE_PATH_KEY = "visible image path key"

    private val cameraActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result -> handleImage(result)
    }

    private lateinit var storage: FirebaseStorage

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
        cameraButton = view.findViewById(R.id.cameraButton)

        storage = Firebase.storage

        // where the taken photo is stored on the device
        newPhotoPath = savedInstanceState?.getString(NEW_PHOTO_PATH_KEY)
        // where the photo in cameraButton is stored on the device
        visibleImagePath = savedInstanceState?.getString(VISIBLE_IMAGE_PATH_KEY)

        cameraButton.setOnClickListener {
            takePicture()
        }

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

        // after the user takes a picture, it should appear in cameraButton
        view?.viewTreeObserver?.addOnWindowFocusChangeListener { hasFocus ->
            if (hasFocus) {
                visibleImagePath?.let { loadImage(cameraButton, it) }
            }
        }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // ensure photo remains visible when device is rotated
        outState.putString(NEW_PHOTO_PATH_KEY, newPhotoPath)
        outState.putString(VISIBLE_IMAGE_PATH_KEY, visibleImagePath)
    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val (photoFile, photoFilePath) = createImageFile()

        if (photoFile != null) {
            newPhotoPath = photoFilePath
            photoUri = FileProvider.getUriForFile(
                requireActivity(),
                "com.example.beetrackerrough.fileprovider",
                photoFile
            )
            Log.d(TAG, "$photoUri - $photoFilePath")
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            cameraActivityLauncher.launch(takePictureIntent)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): Pair<File?, String?> {
        return try {
            val dateTime = SimpleDateFormat("yyyMMdd__HHmmss").format(Date())
            imageFilename = "COLLAGE_$dateTime"
            val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File.createTempFile(imageFilename.toString(), ".jpg", storageDir)
            Log.d(TAG, "Image file created $imageFilename.jpg")

            file to file.absolutePath
        } catch (ex: IOException) {
            Log.e(TAG, "Error creating image file", ex)
            null to null
        }
    }

    private fun handleImage(result: ActivityResult) {
        when (result.resultCode) {
            RESULT_OK -> {
                Log.d(TAG, "Image at $newPhotoPath")
                visibleImagePath = newPhotoPath
            }
            RESULT_CANCELLED -> {
                Log.d(TAG, "No picture taken")
            }
        }
    }

    private fun loadImage(imageButton: ImageButton, photoFilePath: String) {
        Picasso.get()
            .load(File(photoFilePath))
            .error(android.R.drawable.stat_notify_error)
            .fit()
            .centerCrop()
            .into(imageButton, object: Callback {
                override fun onSuccess() {
                    Log.d(TAG, "Successfully loaded $photoFilePath")
                }
                override fun onError(e: Exception?) {
                    Log.e(TAG, "Error loading $photoFilePath", e)
                }
            })
    }

    companion object {
        @JvmStatic
        fun newInstance() = BeeSightingUserInput()
    }
}