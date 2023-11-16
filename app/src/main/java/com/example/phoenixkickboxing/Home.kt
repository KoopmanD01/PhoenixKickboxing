package com.example.phoenixkickboxing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.phoenixkickboxing.databinding.ActivityHomeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Runs when the BOOK SESSION LAYOUT is clicked
        binding.bookSessionLayout.setOnClickListener()
        {
            // Intent to take the user to the Book Session activity (From IntentHelper)
            ClassIntent(this, BookSession::class.java)
        }

        // Runs when the VIEW SESSION LAYOUT is clicked
        binding.viewSessionLayout.setOnClickListener()
        {
            // Intent to take the user to the View Session activity (From IntentHelper)
            ClassIntent(this, ViewSession::class.java)
        }

        // Runs when the SETTINGS LAYOUT is clicked
        binding.settingsLayout.setOnClickListener()
        {
            // Intent to take the user to the Settings activity (From IntentHelper)
            ClassIntent(this, Settings::class.java)
        }

        // Runs when the ABOUT US LAYOUT is clicked
        binding.aboutUsLayout.setOnClickListener()
        {
            // Intent to take the user to the About Us activity (From IntentHelper)
            ClassIntent(this, About::class.java)
        }

        // Runs when the VIEW WEBSITE LAYOUT is clicked
        binding.viewWebsiteLayout.setOnClickListener()
        {
            // Taking the user to the website
        }

        // Runs when the LOGOUT LAYOUT is clicked
        binding.logOutLayout.setOnClickListener()
        {
            // Intent to take the user to the Login activity (From IntentHelper)
            ClassIntent(this, LogIn::class.java)
            Firebase.auth.signOut()
        }
    }
}