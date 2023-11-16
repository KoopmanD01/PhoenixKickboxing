package com.example.phoenixkickboxing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import com.example.phoenixkickboxing.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {
    private lateinit var dbAuthSynergy: FirebaseAuth
    private val database = Firebase.database
    private val myRef1 = database.getReference("users").child("Members")
    private var fullname: String = ""
    private var email: String = ""
    private var number: String = ""
    private var dateofbirth: String = ""
    private var pass: String = ""
    private var conpass: String = ""
    private val datePattern = """^\d{2}/\d{2}/\d{4}$"""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbAuthSynergy = FirebaseAuth.getInstance()

        binding.backButton.setOnClickListener {
            ClassIntent(this, LogIn::class.java)
        }

        binding.btnRegister.setOnClickListener {
            // Extract and validate user input
            fullname = binding.txtFullname.text.toString()
            email = binding.txtEmail.text.toString()
            number = binding.txtCellNumber.text.toString()
            dateofbirth = binding.txtDOB.text.toString()
            pass = binding.txtPassword.text.toString()
            conpass = binding.txtConfirmPassword.text.toString()
            validateInputs()
        }

        binding.btnClear.setOnClickListener {
            // Clear input fields
            val views = arrayOf(
                binding.txtFullname,
                binding.txtEmail,
                binding.txtCellNumber,
                binding.txtDOB,
                binding.txtPassword,
                binding.txtConfirmPassword
            )

            views.forEach { it.text?.clear() }
        }
    }

    // Validate user inputs
    fun validateInputs() {
        val errors = mutableListOf<String>()
        when {
            !fullname.isNotEmpty() -> errors.add("Full name is required")
            !email.isNotEmpty() -> errors.add("Email is required")
            !number.isNotEmpty() -> errors.add("Phone number is required")
            !dateofbirth.isNotEmpty() -> errors.add("Date of birth is required")
            !pass.isNotEmpty() -> errors.add("Password is required")
            !conpass.isNotEmpty() -> errors.add("Confirm password is required")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> errors.add("Not a valid email address")
            !dateofbirth.matches(datePattern.toRegex()) -> errors.add("Date not in correct format")
            pass != conpass -> errors.add("Passwords do not match")
        }

        if (errors.isNotEmpty()) {
            val errorMessage = errors.joinToString("\n")
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        } else {
            // All inputs are valid, proceed with user creation
            dbAuthSynergy.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = dbAuthSynergy.currentUser

                        if (user != null) {
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(fullname) // Set the display name to `fullname`
                                .build()

                            user.updateProfile(profileUpdates)
                                .addOnCompleteListener { profileUpdateTask ->
                                    if (profileUpdateTask.isSuccessful) {
                                        // Display name updated successfully
                                        // Call your AddData function
                                        AddData(user.uid.toString(), fullname, email, number, dateofbirth)
                                    } else {
                                        // Handle display name update failure
                                        Toast.makeText(this, "Failed to update display name.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    } else {
                        // If sign-in fails, display a message to the user
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    // Add user data to Realtime Database
    fun AddData(uid: String, name: String, ema: String, num: String, dob: String) {
        val data = UserData(uid, name, ema, num, dob)
        val childRef = myRef1.child(uid).child(name)

        childRef.setValue(data).addOnSuccessListener {
            Toast.makeText(this, "Registered", Toast.LENGTH_SHORT).show()
            ClassIntent(this, LogIn::class.java)
        }
    }
}
