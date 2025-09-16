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
import com.example.myfitnesspal.data.PlanItem
import com.example.myfitnesspal.data.UserRepository
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PlanScreen() {
    val context = LocalContext.current
    val repo = remember { UserRepository() }
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var plans by remember { mutableStateOf(listOf<PlanItem>()) }

    // Load plans on entry
    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) {
            repo.getPlans(uid) { plans = it }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Workout Plans 📝", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (title.isNotBlank() && description.isNotBlank()) {
                    val plan = PlanItem(title = title, description = description)
                    repo.addPlan(uid, plan) { success ->
                        if (success) {
                            Toast.makeText(context, "Plan saved", Toast.LENGTH_SHORT).show()
                            repo.getPlans(uid) { plans = it } // refresh list
                            title = ""
                            description = ""
                        } else {
                            Toast.makeText(context, "Save failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Plan")
        }

        Divider()

        Text("Your Plans", style = MaterialTheme.typography.titleMedium)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(plans) { plan ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Title: ${plan.title}", style = MaterialTheme.typography.titleMedium)
                        Text("Description: ${plan.description}")
                    }
                }
            }
        }
    }
}
