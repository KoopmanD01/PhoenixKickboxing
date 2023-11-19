package com.example.phoenixkickboxing

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TimePicker
import android.widget.Toast
import com.example.phoenixkickboxing.databinding.ActivityAdminChangeTimeBinding
import java.text.SimpleDateFormat
import java.util.*

class AdminChangeTime : AppCompatActivity() {

    private lateinit var binding: ActivityAdminChangeTimeBinding
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
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(year - 1900, month, dayOfMonth))

                // Query the past sessions for the selected date
                binding.btnSelectDate.text =  selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}