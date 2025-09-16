package com.example.myfitnesspal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myfitnesspal.data.UserRepository
import com.example.myfitnesspal.ui.navigation.BottomNavItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(navController: NavController) {
    val repo = remember { UserRepository() }
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var caloriesConsumed by remember { mutableStateOf(0) }
    var caloriesBurned by remember { mutableStateOf(0) }
    var workoutsLogged by remember { mutableStateOf(0) }

    // 🔹 Today's date string
    val today = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    var listener by remember { mutableStateOf<ListenerRegistration?>(null) }

    // ✅ Start listener for today's data
    LaunchedEffect(uid, today) {
        // Remove any existing listener before re-attaching
        listener?.remove()
        if (uid.isNotEmpty()) {
            listener = repo.listenToDashboardForDate(uid, today) { consumed, burned, workouts ->
                caloriesConsumed = consumed
                caloriesBurned = burned
                workoutsLogged = workouts
            }

            // Initial fetch in case listener has delay
            repo.getMealsForDate(uid, today) { meals ->
                caloriesConsumed = meals.sumOf { it.calories }
            }
            repo.getWorkoutsForDate(uid, today) { workouts ->
                caloriesBurned = workouts.sumOf { it.calories }
                workoutsLogged = workouts.size
            }
        }
    }

    // ✅ Clean up listener when screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            listener?.remove()
            listener = null
        }
    }

    // ✅ UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dashboard 🏠", style = MaterialTheme.typography.headlineMedium)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Calories Consumed: $caloriesConsumed kcal")
                Text("Calories Burned: $caloriesBurned kcal")
                Text("Workouts Logged: $workoutsLogged")
            }
        }

        Button(
            onClick = { navController.navigate(BottomNavItem.Workouts.route) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Add Workout") }

        Button(
            onClick = { navController.navigate(BottomNavItem.Nutrition.route) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Log Food") }

        Button(
            onClick = { navController.navigate(BottomNavItem.Progress.route) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Update Weight") }
    }
}
