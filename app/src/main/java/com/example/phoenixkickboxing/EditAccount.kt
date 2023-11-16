package com.example.phoenixkickboxing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.phoenixkickboxing.databinding.ActivityEditAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EditAccount : AppCompatActivity() {

    private val staffEmails: MutableList<String> = mutableListOf()
    private val databaseEmail: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val userId = FirebaseAuth.getInstance()
    private val database = Firebase.database
    private val myRef = database.getReference("users")
    private var currentUserEmail: String = ""
    private lateinit var binding: ActivityEditAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getEmails()

        val currentuser = userId.currentUser
        currentUserEmail = currentuser?.email.toString()

        binding.btnConfirmPassword.setOnClickListener {
            handlePasswordUpdate(binding.txtPassword.text.toString(), binding.txtConfirmPassword.text.toString())
        }

        binding.btnConfirmUsername.setOnClickListener {
            handleUsernameUpdate(binding.txtUsername.text.toString(), binding.txtConfirmUsername.text.toString())
        }

        binding.btnConfirmDetails.setOnClickListener {
            handleDetailsUpdate(
                binding.txtFullname.text.toString(),
                binding.txtCellNumber.text.toString(),
                binding.txtDOB.text.toString()
            )
        }

        binding.backButton.setOnClickListener {
            handleBackButtonClick()
        }
    }

    private fun handlePasswordUpdate(password: String, confirmPassword: String) {
        if (password == confirmPassword) {
            val currentUser = userId.currentUser
            currentUser?.updatePassword(confirmPassword)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Update Success", Toast.LENGTH_SHORT).show()
                        handleLogout()
                    } else {
                        Toast.makeText(this, "Error Update", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleUsernameUpdate(newUsername: String, confirmUsername: String) {
        if (newUsername == confirmUsername) {
            val currentUser = userId.currentUser
            currentUser?.updateEmail(confirmUsername)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        handleUsernameUpdateInDatabase(newUsername, currentUser)
                    } else {
                        Toast.makeText(this, "Error Update", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Usernames do not match", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleUsernameUpdateInDatabase(newUsername: String, currentUser: FirebaseUser?) {
        currentUserEmail = currentUser?.email ?: ""
        val userId = currentUser?.uid
        val userReference = if (staffEmails.contains(currentUserEmail)) {
            database.reference.child("users").child("Staff").child(userId.toString()).child(currentUser?.displayName.toString())
        } else {
            database.reference.child("users").child("Members").child(userId.toString()).child(currentUser?.displayName.toString())
        }
        userReference.child("email").setValue(newUsername)
        clearTextFields()
        Toast.makeText(this, "Update Success", Toast.LENGTH_SHORT).show()
    }

    private fun handleDetailsUpdate(fullName: String, cellNumber: String, DoB: String) {
        if (fullName.isNotBlank() && cellNumber.isNotBlank() && DoB.isNotBlank()) {
            val currentUser = userId.currentUser
            currentUser?.let { user ->
                val userEmail = user.email
                val userReference = if (staffEmails.contains(currentUserEmail)) {
                    database.reference.child("users").child("Staff").child(user.uid).child(user.displayName.toString())
                } else {
                    database.reference.child("users").child("Members").child(user.uid).child(user.displayName.toString())
                }
                userReference.apply {
                    child("fullName").setValue(fullName)
                    child("cellphone").setValue(cellNumber)
                    child("dateOB").setValue(DoB)
                }
                updateUserProfile(user, fullName)
                clearTextFields()
                Toast.makeText(this, "Update Success", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please fill in all details.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleBackButtonClick() {
        val currentUser = userId.currentUser
        currentUser?.let { user ->
            val userEmail = user.uid
            val destinationClass = if (staffEmails.contains(userEmail)) AdminSettings::class.java else Settings::class.java
            ClassIntent(this, destinationClass)
        }
    }

    private fun updateUserProfile(user: FirebaseUser, fullName: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(fullName)
            .build()

        user.updateProfile(profileUpdates)
    }

    private fun clearTextFields() {
        binding.txtFullname.text?.clear()
        binding.txtCellNumber.text?.clear()
        binding.txtDOB.text?.clear()
    }

    private fun handleLogout() {
        ClassIntent(this, LogIn::class.java)
        Firebase.auth.signOut()
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
}
