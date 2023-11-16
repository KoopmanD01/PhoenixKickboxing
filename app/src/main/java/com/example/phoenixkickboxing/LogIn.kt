package com.example.phoenixkickboxing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.phoenixkickboxing.databinding.ActivityLogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LogIn : AppCompatActivity() {
    private lateinit var dbAuthSynergy: FirebaseAuth
    private lateinit var binding: ActivityLogInBinding
    private val staffEmails: MutableList<String> = mutableListOf()
    private val databaseEmail: FirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this, SessionCleanupService::class.java)
        startService(intent)

        dbAuthSynergy = FirebaseAuth.getInstance()

        binding.btnGoToRegister.setOnClickListener {
            // Handle navigation to the registration screen
            ClassIntent(this, Register::class.java)
        }

        getEmails()

        binding.btnLogin.setOnClickListener {
            val email = binding.txtUsername.text.toString()
            val password = binding.txtPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                dbAuthSynergy.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = task.result?.user
                            if (user != null) {
                                val userUid = user.uid

                                if (staffEmails.contains(userUid.toString())) {
                                    // User is staff
                                    ClassIntent(this, AdminHome::class.java)
                                } else {
                                    // User is not staff
                                    ClassIntent(this, Home::class.java)
                                }
                            }
                        } else {
                            // If sign-in fails, display an authentication error message
                            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                // Display a message if email or password is empty for login
                Toast.makeText(this, "Enter both email and password for login", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getEmails() {
        val usersRef: DatabaseReference = databaseEmail.reference.child("users").child("Staff")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val email = childSnapshot.key
                    email?.let {
                        staffEmails.add(it.toString())
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors here
            }
        })
    }
}
