package com.example.phoenixkickboxing

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phoenixkickboxing.databinding.ActivityAdminSearchPastSessionsBinding
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AdminSearchPastSessions : AppCompatActivity() {
    private lateinit var binding: ActivityAdminSearchPastSessionsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var pastSessionsAdapter: PastSessionsAdapter

    private val pastSessionsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("PastSessions")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminSearchPastSessionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = findViewById(R.id.recyclerView)
        pastSessionsAdapter = PastSessionsAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = pastSessionsAdapter

        binding.backButton.setOnClickListener(){
            ClassIntent(this, AdminSettings::class.java)
        }


        binding.btnSearch.setOnClickListener(){
            showDatePickerDialog()
        }

    }


    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(year - 1900, month, dayOfMonth))

                // Query the past sessions for the selected date
                queryPastSessions(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun queryPastSessions(selectedDate: String) {
        pastSessionsRef.child(selectedDate).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val pastSessionsList = mutableListOf<String>()

                if (dataSnapshot.exists()) {
                    // Data found for the selected date
                    for (sessionSnapshot in dataSnapshot.children) {
                        pastSessionsList.add(sessionSnapshot.key.toString())
                    }
                } else {
                    // No data found for the selected date
                    pastSessionsList.add("No data found on this date")
                }

                // Update the RecyclerView with the past sessions
                pastSessionsAdapter.submitList(pastSessionsList)

                // Log the data size to see if anything is retrieved
                Log.d("PastSessions", "Data Size: ${pastSessionsList.size}")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }


}