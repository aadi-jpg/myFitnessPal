package com.example.myfitnesspal.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myfitnesspal.data.UserRepository
import com.example.myfitnesspal.data.Workout
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WorkoutsScreen() {
    val context = LocalContext.current
    val repo = remember { UserRepository() }
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var type by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var workouts by remember { mutableStateOf(listOf<Workout>()) }

    // Get today's date
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // Load only today's workouts
    LaunchedEffect(uid, today) {
        if (uid.isNotEmpty()) {
            repo.getWorkoutsForDate(uid, today) { workouts = it }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Workouts 🏋️", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type (e.g., Running)") })
        OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (min)") })
        OutlinedTextField(value = calories, onValueChange = { calories = it }, label = { Text("Calories Burned") })

        Button(
            onClick = onClick@{
                if (uid.isEmpty()) {
                    Toast.makeText(context, "Please log in", Toast.LENGTH_SHORT).show()
                    return@onClick
                }
                if (type.isBlank() || duration.isBlank() || calories.isBlank()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@onClick
                }

                val workout = Workout(
                    type = type.trim(),
                    duration = duration.toIntOrNull() ?: 0,
                    calories = calories.toIntOrNull() ?: 0,
                    date = today
                )

                repo.addWorkout(uid, workout) { success ->
                    if (success) {
                        Toast.makeText(context, "Workout added!", Toast.LENGTH_SHORT).show()
                        // refresh only today's list
                        repo.getWorkoutsForDate(uid, today) { workouts = it }
                        // clear fields
                        type = ""
                        duration = ""
                        calories = ""
                    } else {
                        Toast.makeText(context, "Failed to add workout", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Workout")
        }

        Divider()

        Text("Today's Workouts", style = MaterialTheme.typography.titleMedium)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            items(workouts) { workout ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Type: ${workout.type}")
                        Text("Duration: ${workout.duration} min")
                        Text("Calories: ${workout.calories}")
                        Text("Date: ${workout.date}")
                    }
                }
            }
        }
    }
}
