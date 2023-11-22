package com.example.phoenixkickboxing

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.phoenixkickboxing.databinding.ActivityBookSessionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class BookSession : AppCompatActivity() {

    private val userId = FirebaseAuth.getInstance()
    private val staffEmails: MutableList<String> = mutableListOf()
    private val databaseEmail: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val bookedDates: MutableList<String> = mutableListOf()

    private var timeSlot: String =""
    private var day: String = ""
    private var date: String = ""
    private val database = Firebase.database
    private var myRef1 = database.getReference("Sessions")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBookSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getEmails()
        getDates()

        binding.Calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }

            val currentDate = Calendar.getInstance()

            if (selectedDate.timeInMillis >= currentDate.timeInMillis) {
                val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                val dayOfWeek = dayOfWeekFormat.format(selectedDate.time)
                day = dayOfWeek.toString()

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                date = formattedDate.toString()

                val displayText = "$dayOfWeek\n $formattedDate"
                binding.dateLabel.text = displayText

                // Call readTimeSlotsForDate here after setting the date

            } else {
                Toast.makeText(this, "Date has already passed", Toast.LENGTH_SHORT).show()
            }
        }


        binding.backButton.setOnClickListener {
            handleBackButtonClick()
        }

        binding.btnBook.setOnClickListener {
            if (date.isNotEmpty()) {
                bookedDates.clear()
                getDates()

                val selectedDate = binding.dateLabel.text.toString()

                val currentUser = userId.currentUser
                currentUser?.let { user ->
                    val userEmail = user.email
                    if (bookedDates.contains(date)) {
                        myRef1 = myRef1.child(date)
                    } else {
                        myRef1 = myRef1.child(date)
                    }
                    if (userEmail != null) {


                        val newChildRef = myRef1.child(user.uid.toString())
                        newChildRef.setValue(user.displayName.toString())
                            .addOnSuccessListener {
                                Toast.makeText(this, "Session booked successfully", Toast.LENGTH_SHORT).show()
                                binding.dateLabel.text = ""
                                date =""
                                myRef1 = database.getReference("Sessions")
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                            }
                    }
                }
            } else {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_LONG).show()
            }
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

    private fun getDates() {
        val usersRef: DatabaseReference = databaseEmail.reference.child("Sessions")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val sessionKey = childSnapshot.key
                    if (sessionKey != null) {
                        bookedDates.add(sessionKey.toString())
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors here
            }
        })
    }
}
