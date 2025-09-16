package com.example.myfitnesspal.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myfitnesspal.LoginActivity
import com.example.myfitnesspal.data.User
import com.example.myfitnesspal.data.UserRepository
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val repo = remember { UserRepository() }
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }

    // 🔹 Load user data when screen opens
    LaunchedEffect(uid) {
        uid?.let {
            repo.getUser(it) { user ->
                user?.let {
                    name = it.name
                    age = it.age.toString()
                    height = it.height.toString()
                    weight = it.weight.toString()
                    goal = it.goal
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Profile 👤", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") })
        OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Height (cm)") })
        OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)") })
        OutlinedTextField(value = goal, onValueChange = { goal = it }, label = { Text("Goal") })

        // ✅ Save Changes
        Button(
            onClick = {
                if (uid != null) {
                    val updatedUser = User(
                        uid = uid,
                        name = name,
                        email = auth.currentUser?.email ?: "",
                        age = age.toIntOrNull() ?: 0,
                        height = height.toDoubleOrNull() ?: 0.0,
                        weight = weight.toDoubleOrNull() ?: 0.0,
                        goal = goal
                    )

                    repo.updateUser(updatedUser) { success ->
                        Toast.makeText(
                            context,
                            if (success) "Profile updated" else "Update failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🚪 Logout button
        Button(
            onClick = {
                auth.signOut()
                Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, LoginActivity::class.java))
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log Out")
        }
    }
}
