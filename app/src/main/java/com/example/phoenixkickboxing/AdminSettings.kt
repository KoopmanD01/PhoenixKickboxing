package com.example.phoenixkickboxing

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.example.phoenixkickboxing.databinding.ActivityAdminSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminSettings : AppCompatActivity() {

    private val userId = FirebaseAuth.getInstance()
    private val staffEmails: MutableList<String> = mutableListOf()
    private val databaseEmail: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val REQUEST_CODE_PICK_IMAGE = 100
    private lateinit var binding: ActivityAdminSettingsBinding

    val storage = FirebaseStorage.getInstance()
    val storageRef: StorageReference = storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getEmails()

        CoroutineScope(Dispatchers.Main).launch {
            checkProfileImageExistence()
        }

        val currentUser = userId.currentUser
        if (currentUser != null) {
            binding.viewFullname.text = currentUser.displayName.toString()
        }

        setClickListeners()
    }

    private fun setClickListeners() {
        binding.btnPhoto.setOnClickListener {
            openGallery()
        }
        binding.SearchSessions.setOnClickListener {
            ClassIntent(this, AdminSearchPastSessions::class.java)
        }
        binding.addAdmin.setOnClickListener {
            ClassIntent(this, AddAdmin::class.java)
        }

        binding.editAccountLayout.setOnClickListener {
            ClassIntent(this, EditAccount::class.java)
        }

        binding.backButton.setOnClickListener {
            handleBackButtonClick()
        }
    }

    private fun handleBackButtonClick() {
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

    private fun getEmails() {
        val usersRef: DatabaseReference = databaseEmail.reference.child("users").child("Staff")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val email = childSnapshot.key
                    staffEmails.add(email.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors here
            }
        })
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show()
            checkProfileImageExistence()
        }
    }

    private fun checkProfileImageExistence() {
        val user = userId.currentUser
        if (user != null) {
            val imageRef = storageRef.child("user_photos/${user.uid}/profile.jpg")
            imageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    loadImageWithPicasso(uri)
                }
                .addOnFailureListener {
                    // Handle errors or cases where the profile image doesn't exist
                }
        } else {
            // Handle the case where the user is not logged in
        }
    }

    private fun loadImageWithPicasso(uri: Uri) {
        Picasso.get().load(uri).into(binding.imgUserPhoto)
    }
}
