package com.example.phoenixkickboxing

import android.app.IntentService
import android.content.Intent
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class SessionCleanupService : IntentService("SessionCleanupService") {
    private val handler = android.os.Handler()
    private val cleanupIntervalMillis = 7 * 24 * 60 * 60 * 1000 // 2 minutes
    private val oneDaysInMillis = 1 * 24 * 60 * 60 * 1000


    // Define a Runnable to perform the cleanupSessions
    private val cleanupRunnable = object : Runnable {
        override fun run() {
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val sessionsRef: DatabaseReference = database.getReference("Sessions")
            val pastSessionsRef: DatabaseReference = database.getReference("PastSessions")
            val currentTimeInMillis = System.currentTimeMillis()

            sessionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (sessionSnapshot in dataSnapshot.children) {
                        val sessionDate = sessionSnapshot.key
                        if (!sessionDate.isNullOrEmpty()) {
                            try {
                                val dateFormat =
                                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val creationDate = dateFormat.parse(sessionDate)
                                val creationTimeInMillis = creationDate?.time ?: 0

                                if (creationTimeInMillis < (currentTimeInMillis - oneDaysInMillis)) {
                                    // Move the session to "PastSessions" reference
                                    val movedSession = sessionSnapshot.value
                                    pastSessionsRef.child(sessionDate).setValue(movedSession)

                                    // Remove the session from "Sessions" reference
                                    sessionSnapshot.ref.removeValue()
                                }
                            } catch (e: Exception) {
                                // Handle parsing errors or invalid date format
                                // You can log the error or take appropriate action
                            }
                        }

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                }
            })

            // Schedule the next cleanup after 2 minutes
            handler.postDelayed(this, cleanupIntervalMillis.toLong())
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        // Start the initial cleanup and schedule subsequent cleanups
        handler.post(cleanupRunnable)
    }
}

