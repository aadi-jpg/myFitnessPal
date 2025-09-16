package com.example.myfitnesspal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myfitnesspal.data.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegistrationActivity : AppCompatActivity() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val emailInput = findViewById<EditText>(R.id.emailEditText)
        val passInput = findViewById<EditText>(R.id.passwordEditText)
        val nameInput = findViewById<EditText>(R.id.nameEditText)
        val ageInput = findViewById<EditText>(R.id.ageEditText)
        val heightInput = findViewById<EditText>(R.id.heightEditText)
        val weightInput = findViewById<EditText>(R.id.weightEditText)
        val goalInput = findViewById<EditText>(R.id.goalEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passInput.text.toString().trim()
            val name = nameInput.text.toString().trim()
            val age = ageInput.text.toString().trim()
            val height = heightInput.text.toString().trim()
            val weight = weightInput.text.toString().trim()
            val goal = goalInput.text.toString().trim()

            // Basic validation
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable button to avoid duplicate taps
            registerButton.isEnabled = false

            // Create auth user
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = task.result?.user
                        val uid = firebaseUser?.uid

                        if (uid == null) {
                            Toast.makeText(this, "Registration succeeded but uid is null.", Toast.LENGTH_LONG).show()
                            registerButton.isEnabled = true
                            return@addOnCompleteListener
                        }

                        // Parse numeric fields safely
                        val ageInt = age.toIntOrNull() ?: 0
                        val heightDouble = height.toDoubleOrNull() ?: 0.0
                        val weightDouble = weight.toDoubleOrNull() ?: 0.0

                        // Create a User data object (matches Firestore structure)
                        val user = User(
                            uid = uid,
                            name = name,
                            email = email,
                            age = ageInt,
                            height = heightDouble,
                            weight = weightDouble,
                            goal = goal
                        )

                        // Save to Firestore under users/{uid}
                        db.collection("users").document(uid)
                            .set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registered! Redirecting...", Toast.LENGTH_SHORT).show()
                                // Go to MainActivity
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { ex ->
                                Toast.makeText(this, "Profile save failed: ${ex.message}", Toast.LENGTH_LONG).show()
                                // keep user logged in (optional), re-enable button so they can retry
                                registerButton.isEnabled = true
                            }
                    } else {
                        // Registration failed
                        Toast.makeText(this, task.exception?.message ?: "Registration failed", Toast.LENGTH_LONG).show()
                        registerButton.isEnabled = true
                    }
                }
        }
    }
}
