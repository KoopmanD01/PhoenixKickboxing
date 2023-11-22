package com.example.phoenixkickboxing

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.example.phoenixkickboxing.databinding.ActivityViewAllSessionsBinding
import com.example.phoenixkickboxing.databinding.ActivityViewSessionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ViewSession : AppCompatActivity() {

    private lateinit var fireauth: FirebaseAuth
    private val database = Firebase.database
    private lateinit var binding: ActivityViewSessionBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myRef = database.getReference("Sessions")
        fireauth = FirebaseAuth.getInstance()
        val currentuser = fireauth.currentUser
        if(currentuser != null){

            val currentUserUid = currentuser.uid.toString()


            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (childSnapshot in dataSnapshot.children) {
                            val key = childSnapshot.key
                            val sessionData = childSnapshot.value as Map<String, Any>?
                            val uid = sessionData?.keys
                            val values = sessionData?.values
                            val uid2 = uid?.firstOrNull() // Assuming there's only one UID per session

                            if (uid2 != null && currentUserUid == uid2.toString()) {
                                val name = values?.firstOrNull() // Assuming there's only one name per session
                                val rowData = arrayOf(key, name.toString())

                                // Display the name using a Toast
                                val row = TableRow(this@ViewSession)
                                val layoutParams = TableLayout.LayoutParams(
                                    TableLayout.LayoutParams.MATCH_PARENT,
                                    TableLayout.LayoutParams.MATCH_PARENT
                                )
                                row.layoutParams = layoutParams

                                // Create a LocalDate instance
                                val localDate = LocalDate.now() // Current local date

                                val pattern = "yyyy-MM-dd"
                                val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())

                                val formattedDate = localDate.format(formatter)
                                readTimeSlotsForDate(formattedDate)

                                for (i in rowData.indices) {
                                    val cell = TextView(this@ViewSession)
                                    cell.text = rowData[i]

                                    if (key.toString() == formattedDate) {
                                        cell.setBackgroundColor(Color.parseColor("#00FF00"))
                                    }

                                    cell.setPadding(50, 16, 50, 16)
                                    cell.gravity = Gravity.CENTER
                                    row.addView(cell)
                                }

                                binding.tableLayoutView.addView(row)
                            }
                        }
                    } else {
                        Toast.makeText(this@ViewSession, "No bookings", Toast.LENGTH_SHORT).show()
                    }
                }


                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@ViewSession,"Error",Toast.LENGTH_SHORT).show()
                }
            })
        }


        // Runs when the BACK BUTTON is clicked
        binding.backButton.setOnClickListener()
        {

            ClassIntent(this, Home::class.java)

        }
    }
    //------------------------------------------------------------------------------------------------------//
    private fun readTimeSlotsForDate(selectedDate: String) {
        val timeSlotsRef: DatabaseReference = database.getReference("timeslot").child(selectedDate)

        val timeSlotsList = mutableListOf<String>()

        timeSlotsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (timeSlotSnapshot in dataSnapshot.children) {
                    val timeSlot = timeSlotSnapshot.value.toString()
                    timeSlotsList.add(timeSlot)
                }

                if (timeSlotsList.isNotEmpty()) {
                    // Assuming you want to display all time slots, you can join them into a single string
                    val allTimes = timeSlotsList.joinToString(", ")
                    binding.timeLabel.text = "Todays session times: $allTimes"
                } else {
                    binding.timeLabel.text = "Todays session times: 19:00"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors here
            }
        })
    }



}