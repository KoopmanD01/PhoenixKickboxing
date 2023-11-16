package com.example.phoenixkickboxing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.phoenixkickboxing.databinding.ActivityAddAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddAdmin : AppCompatActivity() {

    private lateinit var dbAuthSynergy: FirebaseAuth
    private val database = Firebase.database
    private val myRef1 = database.getReference("users").child("Staff")
    private lateinit var binding: ActivityAddAdminBinding

    private var fullname: String = ""
    private var email: String = ""
    private var number: String = ""
    private var dateofbirth: String = ""
    private var pass: String = ""
    private var conpass: String = ""
    private val datePattern = """^\d{2}/\d{2}/\d{4}$""".toRegex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbAuthSynergy = FirebaseAuth.getInstance()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            ClassIntent(this, AdminSettings::class.java)
        }
        binding.btnRegister.setOnClickListener {
            retrieveInputValues()
        }
    }

    private fun retrieveInputValues() {
        fullname = binding.txtFullname.text.toString()
        email = binding.txtEmail.text.toString()
        number = binding.txtCellNumber.text.toString()
        dateofbirth = binding.txtDOB.text.toString()
        pass = binding.txtPassword.text.toString()
        conpass = binding.txtConfirmPassword.text.toString()
        validateInputs()
    }

    private fun validateInputs() {
        val errors = mutableListOf<String>()
        if (fullname.isEmpty() ||
            email.isEmpty() ||
            number.isEmpty() ||
            dateofbirth.isEmpty() ||
            pass.isEmpty() ||
            conpass.isEmpty()
        ) {
            errors.add("All fields are required")
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errors.add("Invalid email address")
        }
        if (!datePattern.matches(dateofbirth)) {
            errors.add("Date not in correct format")
        }
        if (pass != conpass) {
            errors.add("Passwords do not match")
        }

        if (errors.isNotEmpty()) {
            val errorMessage = errors.joinToString("\n")
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        } else {
            createUser()
        }
    }

    private fun createUser() {
        dbAuthSynergy.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val newUser = task.result?.user
                    if (newUser != null) {
                        updateUserDisplayName(newUser)
                    } else {
                        showError("Failed to create user.")
                    }
                } else {
                    showError("Authentication failed.")
                }
            }
    }

    private fun updateUserDisplayName(user: FirebaseUser) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(fullname)
            .build()
        user.updateProfile(profileUpdates)
            .addOnCompleteListener { profileUpdateTask ->
                if (profileUpdateTask.isSuccessful) {
                    addUserData(user.uid)
                } else {
                    showError("Failed to update display name.")
                }
            }
    }

    private fun addUserData(uid: String) {
        val data = UserData(uid, fullname, email, number, dateofbirth)
        val childRef = myRef1.child(uid).child(fullname)
        childRef.setValue(data).addOnSuccessListener {
            Toast.makeText(this, "User Created", Toast.LENGTH_SHORT).show()
            ClassIntent(this, LogIn::class.java)
            Firebase.auth.signOut()
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
