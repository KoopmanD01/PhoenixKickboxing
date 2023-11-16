package com.example.phoenixkickboxing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.phoenixkickboxing.databinding.ActivityAdminHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AdminHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClickListeners(binding)
    }

    private fun setClickListeners(binding: ActivityAdminHomeBinding) {
        binding.viewAllSessionsLayout.setOnClickListener {
            ClassIntent(this, ViewAllSessions::class.java)
        }

        binding.uploadImagesLayout.setOnClickListener {
            ClassIntent(this, UploadImage::class.java)
        }

        binding.settingsBox.setOnClickListener {
            ClassIntent(this, AdminSettings::class.java)
        }

        binding.viewWebsiteBox.setOnClickListener {
            // Handle opening the website (if applicable)
        }

        binding.logoutBox.setOnClickListener {
            logoutAndNavigateToLogin()
        }
    }

    private fun logoutAndNavigateToLogin() {
        Firebase.auth.signOut()
        ClassIntent(this, LogIn::class.java)
    }
}
