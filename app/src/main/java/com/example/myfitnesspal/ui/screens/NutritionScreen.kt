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
import com.example.myfitnesspal.data.Meal
import com.example.myfitnesspal.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NutritionScreen() {
    val context = LocalContext.current
    val repo = remember { UserRepository() }
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var food by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }
    var meals by remember { mutableStateOf(listOf<Meal>()) }

    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // Load today's meals on screen entry
    LaunchedEffect(uid, today) {
        if (uid.isNotEmpty()) {
            repo.getMealsForDate(uid, today) { meals = it }
        }
    }

    // Totals
    val totalCalories = meals.sumOf { it.calories }
    val totalProtein = meals.sumOf { it.protein }
    val totalCarbs = meals.sumOf { it.carbs }
    val totalFats = meals.sumOf { it.fats }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Nutrition 🍎", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(value = food, onValueChange = { food = it }, label = { Text("Food") })
        OutlinedTextField(value = calories, onValueChange = { calories = it }, label = { Text("Calories") })
        OutlinedTextField(value = protein, onValueChange = { protein = it }, label = { Text("Protein (g)") })
        OutlinedTextField(value = carbs, onValueChange = { carbs = it }, label = { Text("Carbs (g)") })
        OutlinedTextField(value = fats, onValueChange = { fats = it }, label = { Text("Fats (g)") })

        Button(
            onClick = {
                if (uid.isEmpty()) {
                    Toast.makeText(context, "Please log in", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (food.isBlank() || calories.isBlank()) {
                    Toast.makeText(context, "Enter at least food and calories", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val meal = Meal(
                    food = food.trim(),
                    calories = calories.toIntOrNull() ?: 0,
                    protein = protein.toIntOrNull() ?: 0,
                    carbs = carbs.toIntOrNull() ?: 0,
                    fats = fats.toIntOrNull() ?: 0,
                    date = today
                )

                repo.addMeal(uid, meal) { success ->
                    if (success) {
                        Toast.makeText(context, "Saved meal", Toast.LENGTH_SHORT).show()
                        // refresh today's meals
                        repo.getMealsForDate(uid, today) { meals = it }
                        // clear inputs
                        food = ""
                        calories = ""
                        protein = ""
                        carbs = ""
                        fats = ""
                    } else {
                        Toast.makeText(context, "Save failed", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Meal")
        }

        Divider()

        // Totals summary
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Today's totals", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Calories: $totalCalories kcal")
                Text("Protein: $totalProtein g    Carbs: $totalCarbs g    Fats: $totalFats g")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Today's meals", style = MaterialTheme.typography.titleMedium)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(meals) { m ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(m.food)
                        Text("Calories: ${m.calories} kcal")
                        Text("Protein: ${m.protein} g  Carbs: ${m.carbs} g  Fats: ${m.fats} g")
                    }
                }
            }
        }
    }
}
