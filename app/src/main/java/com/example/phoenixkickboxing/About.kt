package com.example.phoenixkickboxing

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.phoenixkickboxing.databinding.ActivityAboutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.URL

class About : AppCompatActivity() {
    private val userId = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val staffEmails: MutableList<String> = mutableListOf()

    private lateinit var binding: ActivityAboutBinding
    private val storage = Firebase.storage
    private val storageRef = storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getEmails()
        CoroutineScope(Dispatchers.IO).launch {
            addPhotos()
        }
        setBackButtonClickListener()
    }

    private fun setBackButtonClickListener() {
        binding.backButton.setOnClickListener {
            val currentUser = userId.currentUser
            currentUser?.let { user ->
                val userEmail = user.uid
                if (userEmail != null) {
                    val destinationClass =
                        if (staffEmails.contains(userEmail.toString())) AdminHome::class.java else Home::class.java
                    ClassIntent(this, destinationClass)
                } else {
                    // Handle the case where user email is null
                }
            }
        }
    }

    private fun addPhotos() {
        val imagesRef = storageRef.child("images")
        imagesRef.listAll()
            .addOnSuccessListener { listResult ->
                for (item in listResult.items) {
                    item.downloadUrl
                        .addOnSuccessListener { uri ->
                            loadImageFromUrl(uri.toString())
                        }
                        .addOnFailureListener { exception ->
                            showError("Failed to load image: ${exception.message}")
                        }
                }
            }
            .addOnFailureListener { exception ->
                showError("Failed to list images: ${exception.message}")
            }
    }

    private fun loadImageFromUrl(imageUrl: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val bitmap = getBitmapFromUrl(imageUrl)
            launch(Dispatchers.Main) {
                if (bitmap != null) {
                    displayImage(bitmap)
                } else {
                    // Handle the case where the image loading failed
                    // You can show an error image or a placeholder
                }
            }
        }
    }

    private fun displayImage(bitmap: Bitmap) {
        val imageView = ImageView(this)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, resources.getDimensionPixelSize(R.dimen.image_margin_top), 0, 0)
        imageView.layoutParams = layoutParams
        imageView.setImageBitmap(bitmap)
        findViewById<LinearLayout>(R.id.imageContainer).addView(imageView)
    }

    private fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        try {
            val inputStream = URL(imageUrl).openStream()
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getEmails() {
        val usersRef: DatabaseReference = database.reference.child("users").child("Staff")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val email = childSnapshot.key
                    email?.let { staffEmails.add(it) }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors here
            }
        })
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
