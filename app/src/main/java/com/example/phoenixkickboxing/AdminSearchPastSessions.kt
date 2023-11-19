package com.example.phoenixkickboxing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.phoenixkickboxing.databinding.ActivityAdminSearchPastSessionsBinding

class AdminSearchPastSessions : AppCompatActivity() {
    private lateinit var binding: ActivityAdminSearchPastSessionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminSearchPastSessionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener(){

            ClassIntent(this, AdminSettings::class.java)

        }
    }
}