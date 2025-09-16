package com.example.myfitnesspal.ui.screens

import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myfitnesspal.data.ProgressEntry
import com.example.myfitnesspal.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProgressScreen() {
    val context = LocalContext.current
    val repo = remember { UserRepository() }
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var weight by remember { mutableStateOf("") }
    var entries by remember { mutableStateOf(listOf<ProgressEntry>()) }

    // Load on entry
    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) {
            repo.getProgress(uid) { entries = it }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Progress 📈", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight (kg)") }
        )

        Button(
            onClick = {
                if (weight.isNotBlank()) {
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val entry = ProgressEntry(weight = weight.toDoubleOrNull() ?: 0.0, date = date)

                    repo.addProgress(uid, entry) { success ->
                        if (success) {
                            Toast.makeText(context, "Progress saved", Toast.LENGTH_SHORT).show()
                            repo.getProgress(uid) { entries = it } // refresh list
                            weight = ""
                        } else {
                            Toast.makeText(context, "Save failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Entry")
        }

        Divider()

        Text("History", style = MaterialTheme.typography.titleMedium)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(entries) { entry ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Date: ${entry.date}")
                        Text("Weight: ${entry.weight} kg")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Progress Chart", style = MaterialTheme.typography.titleMedium)

        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    layoutParams = android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        600 // px height
                    )
                    description = Description().apply { text = "Weight over time" }
                }
            },
            update = { chart ->
                val dataPoints = entries.mapIndexed { index, entry ->
                    Entry(index.toFloat(), entry.weight.toFloat())
                }
                val dataSet = LineDataSet(dataPoints, "Weight (kg)").apply {
                    setDrawValues(false)
                    setDrawCircles(true)
                    lineWidth = 2f
                    circleRadius = 4f
                }
                chart.data = LineData(dataSet)
                chart.invalidate()
            }
        )
    }
}
