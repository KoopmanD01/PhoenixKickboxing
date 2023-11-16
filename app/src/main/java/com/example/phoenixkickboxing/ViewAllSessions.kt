package com.example.phoenixkickboxing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.example.phoenixkickboxing.databinding.ActivityViewAllSessionsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ViewAllSessions : AppCompatActivity() {

    private val database = Firebase.database
    private lateinit var binding: ActivityViewAllSessionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityViewAllSessionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myRef = database.getReference("Sessions")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val key = childSnapshot.key // This retrieves the key
                    // Get the session data from the childSnapshot
                    val sessionData = childSnapshot.value as Map<String, Any>?
                    val name = sessionData?.keys


                    val rowData = arrayOf(
                        key.toString(),
                        name.toString()
                    )

                    if (name != null) {
                        // Display the name using a Toast
                        val row = TableRow(this@ViewAllSessions)
                        val layoutParams = TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT
                        )
                        row.layoutParams = layoutParams

                        for (i in rowData.indices) {
                            val cell = TextView(this@ViewAllSessions)
                            cell.text = rowData[i]
                            cell.setPadding(55, 16, 55, 16)
                            row.addView(cell)
                        }

                        binding.tableLayoutView.addView(row)
                    } else {
                        // Handle cases where "name" is null or missing
                        Toast.makeText(this@ViewAllSessions, "Name is missing for key $key", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error: $databaseError")
            }
        })


        //----------------------------------------------------------------------------------------------------//
        // Runs when the BACK BUTTON is clicked
        binding.backButton.setOnClickListener()
        {
            ClassIntent(this, AdminHome::class.java)

        }
    }
    //-----------------------------------End of onc reate-------------------------------------------------------------------//

}