package com.example.phoenixkickboxing

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.example.phoenixkickboxing.databinding.ActivityUploadImageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.*

class UploadImage : AppCompatActivity() {
    private val userId = FirebaseAuth.getInstance()
    private lateinit var storageRef: StorageReference
    private var userID: String = ""
    private lateinit var binding: ActivityUploadImageBinding
    private val staffEmails: MutableList<String> = mutableListOf()
    private val databaseEmail: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var PhotoExist: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityUploadImageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        storageRef = FirebaseStorage.getInstance().reference.child("images")

        val getResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentData: Intent? = result.data
                if (intentData != null) {
                    val bitmap = intentData.extras?.get("data") as? Bitmap
                    if (bitmap != null) {
                        binding.uploadImage.setImageBitmap(bitmap)
                        PhotoExist = true
                    }
                }
            }
        }
        binding.backButton.setOnClickListener()
        {

          ClassIntent(this, AdminHome::class.java)

        }
        binding.uploadImage.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            getResult.launch(intent)
        }

        binding.btnUploadPhoto.setOnClickListener {
            val bitmap = (binding.uploadImage.drawable).toBitmap()
            if (bitmap != null && PhotoExist == true) {
                // Upload the image to Firebase Storage
                GlobalScope.launch(Dispatchers.IO) {
                    uploadImageToStorage(bitmap)
                }
            }
            else{
                Toast.makeText(this,"Please select a photo",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun uploadImageToStorage(bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            // Convert bitmap to a byte array
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            // Create a unique file name for the image
            val fileName = "${UUID.randomUUID()}.jpg"

            val imageRef = storageRef.child(fileName)

            // Upload image to Firebase Storage
            imageRef.putBytes(data)
                .addOnSuccessListener {
                    // Image uploaded successfully

                        Toast.makeText(this@UploadImage, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                        binding.uploadImage.setImageResource(R.drawable.upload)
                    PhotoExist = false

                }
                .addOnFailureListener {
                    // Handle errors during image upload

                        Toast.makeText(this@UploadImage, "Failed to upload image: ${it.message}", Toast.LENGTH_SHORT).show()

                }
        }
    }
    //------------------------------------------------------------------------------------------------------//


}

