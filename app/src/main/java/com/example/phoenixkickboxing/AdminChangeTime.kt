package com.example.phoenixkickboxing

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TimePicker
import android.widget.Toast
import com.example.phoenixkickboxing.databinding.ActivityAdminChangeTimeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AdminChangeTime : AppCompatActivity() {

    private lateinit var binding: ActivityAdminChangeTimeBinding
    private var selectedDate: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminChangeTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val timePicker = findViewById<TimePicker>(R.id.timePicker)

        // Set 24-hour time format
        timePicker.setIs24HourView(true)

        // Set a listener to handle the time selection
        timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            // Handle the selected time (hourOfDay and minute)
            val selectedTime = String.format("%02d:%02d", hourOfDay, minute)

        }

        binding.backButton.setOnClickListener(){
            ClassIntent(this, AdminSettings::class.java)
        }
        binding.btnSelectDate.setOnClickListener(){
            showDatePickerDialog()
        }

        binding.btnUpdate.setOnClickListener {
            if (selectedDate.isNotEmpty()) {
                val selectedTime = getTimeFromTimePicker(binding.timePicker)
                saveOrUpdateTime(selectedDate, selectedTime)
            } else {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun saveOrUpdateTime(date: String, time: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->

            val timeslotRef = Firebase.database.getReference("timeslot").child(date)

            timeslotRef.setValue(time)
                .addOnSuccessListener {
                    Toast.makeText(this, "Time updated successfully", Toast.LENGTH_SHORT).show()
                    binding.btnSelectDate.text = "Select a Date"
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update time", Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(year - 1900, month, dayOfMonth))

                // Update the text on the button to reflect the selected date
                binding.btnSelectDate.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
    private fun getTimeFromTimePicker(timePicker: TimePicker): String {
        val hour = timePicker.hour
        val minute = timePicker.minute
        return String.format("%02d:%02d", hour, minute)
    }
}