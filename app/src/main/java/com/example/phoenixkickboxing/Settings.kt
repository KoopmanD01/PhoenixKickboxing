package com.example.phoenixkickboxing

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.example.phoenixkickboxing.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Settings : AppCompatActivity() {

    private val userId = FirebaseAuth.getInstance()
    private val staffEmails: MutableList<String> = mutableListOf()
    private val databaseEmail: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var binding: ActivitySettingsBinding
    private val REQUEST_CODE_PICK_IMAGE = 100
    val storage = FirebaseStorage.getInstance()
    val storageRef: StorageReference = storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getEmails()
        CoroutineScope(Dispatchers.Main).launch {
            checkProfileImageExistence { exists, imageUri ->
                if (exists) {
                    if (imageUri != null) {
                        // Use Picasso to load the image
                        Picasso.get().load(imageUri).into(binding.imgUserPhoto)

                    }
                } else {
                    // Handle the case where the profile image doesn't exist or there was an error
                }
            }
        }

        val currentuser = userId.currentUser


        if(currentuser != null)
        {
            binding.viewFullname.text = currentuser.displayName.toString()
        }

        // Runs when the BACK BUTTON is clicked
        binding.backButton.setOnClickListener()
        {
            val currentUser = userId.currentUser
            currentUser?.let { user ->
                val userUid = user.uid
                if (userUid  != null) {
                    if (staffEmails.contains(userUid .toString())) {
                        // User is staff
                        ClassIntent(this, AdminHome::class.java)
                    } else {
                        // User is not staff
                        ClassIntent(this, Home::class.java)
                    }
                } else {
                    // Handle the case where user email is null
                }
            }
        }

        binding.btnPhoto.setOnClickListener(){
            openGallery()
        }
        // Runs when the MAKE A BOOKING LAYOUT is clicked
        binding.bookingLayout.setOnClickListener()
        {
            // Intent to take the user to the Book Session activity (From IntentHelper)
            ClassIntent(this, BookSession::class.java)
        }

        // Runs when the EDIT ACCOUNT LAYOUT is clicked
        binding.editAccountLayout.setOnClickListener()
        {
            // Intent to take the user to the Edit Account activity (From IntentHelper)
            ClassIntent(this, EditAccount::class.java)
        }


        // Runs when the ABOUT US LAYOUT is clicked
        binding.aboutUsLayout.setOnClickListener()
        {
            // Intent to take the user to the About Us activity (From IntentHelper)
            ClassIntent(this, About::class.java)
        }

        //send message
        binding.sendMessageLayout.setOnClickListener(){
                sendEmail()
        }

        // Runs when the LOG OUT LAYOUT is clicked
        binding.logOutLayout.setOnClickListener()
        {
            // Intent to take the user to the Log Out activity (From IntentHelper)
            ClassIntent(this, LogIn::class.java)
            Firebase.auth.signOut()
        }
    }

    //------------------------------------------------------------------------------------------------------//
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            // Check if a profile image already exists for the user
            Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show()
            checkProfileImageExistence { exists, imageUri ->
                if (exists) {

                    if (imageUri != null) {
                        updateProfileImage(selectedImageUri!!)
                    }
                } else {
                    createNewProfile(selectedImageUri!!)
                }
            }
        }
    }

    private fun checkProfileImageExistence(callback: (Boolean, Uri?) -> Unit) {
        val user = userId.currentUser
        if (user != null) {

            val imageRef = storageRef.child("user_photos/${user.uid}/profile.jpg")

            imageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    callback(true, uri)
                }
                .addOnFailureListener {
                    callback(false, null)
                }
        } else {
            callback(false, null)
        }
    }



    private fun updateProfileImage(selectedImageUri: Uri) {
        val user = userId.currentUser
        if (user != null) {
            val imageRef = storageRef.child("user_photos/${user.uid}/profile.jpg")
            imageRef.putFile(selectedImageUri)
                .addOnSuccessListener { taskSnapshot ->
                    // Image updated successfully, get the download URL
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        Picasso.get().load(selectedImageUri).into(binding.imgUserPhoto)
                        Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    // Handle errors during image update
                    Toast.makeText(this, "Image update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
        else {

        }

    }

    private fun createNewProfile(selectedImageUri: Uri) {
        val user = userId.currentUser
        if (user != null) {
            val imageRef = storageRef.child("user_photos/${user.uid}/profile.jpg")
            imageRef.putFile(selectedImageUri)
                .addOnSuccessListener { taskSnapshot ->
                    // Image updated successfully, get the download URL
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        Picasso.get().load(selectedImageUri).into(binding.imgUserPhoto)
                        Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    // Handle errors during image update
                    Toast.makeText(this, "Image update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
        else {

        }
    }
    //------------------------------------------------------------------------------------------------------//
    private fun sendEmail() {
        val recipientEmail = getString(R.string.club_email) // Replace with the recipient's email address

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$recipientEmail")
        }

        // Check if Gmail app is installed on the device
        val gmailIntent = packageManager.getLaunchIntentForPackage("com.google.android.gm")

        if (gmailIntent != null) {
            // Gmail app is installed, so open it directly
            startActivity(gmailIntent)
        } else {
            // Gmail app is not installed, create a chooser to let the user choose an email app
            val chooserIntent = Intent.createChooser(intent, "Choose an email app:")
            startActivity(chooserIntent)
        }
    }
    //------------------------------------------------------------------------------------------------------//

    fun getEmails(){
        val usersRef: DatabaseReference = databaseEmail.reference.child("users").child("Staff")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    // Assuming email is stored under a key "email" in each staff member's data
                    val email = childSnapshot.key
                    staffEmails.add(email.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors here
            }
        })
    }


}