package com.example.phoenixkickboxing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.phoenixkickboxing.databinding.ActivityViewAllSessionsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ViewAllSessions : AppCompatActivity() {

    private val database = Firebase.database
    private lateinit var binding: ActivityViewAllSessionsBinding
    private lateinit var sessionAdapter: SessionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAllSessionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myRef = database.getReference("Sessions")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val sessions = mutableListOf<SessionData>()

                for (childSnapshot in dataSnapshot.children) {
                    val key = childSnapshot.key
                    val sessionData = childSnapshot.value as Map<String, Any>?
                    val name = sessionData?.keys


                    if (name != null) {
                        sessions.add(SessionData(key.toString(), name.toString()))
                    } else {
                        // Handle cases where "name" is null or missing
                        Toast.makeText(
                            this@ViewAllSessions,
                            "Name is missing for key $key",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                sessionAdapter = SessionAdapter(sessions)
                binding.recyclerView.adapter = sessionAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error: $databaseError")
            }
        })

        // Set up RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)


        //----------------------------------------------------------------------------------------------------//
        // Runs when the BACK BUTTON is clicked
        binding.backButton.setOnClickListener()
        {
            ClassIntent(this, AdminHome::class.java)

        }
    }
    //-----------------------------------End of onc reate-------------------------------------------------------------------//

}