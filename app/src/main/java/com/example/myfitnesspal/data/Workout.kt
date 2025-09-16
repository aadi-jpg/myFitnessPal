package com.example.myfitnesspal.data

data class Workout(
    val id: String = "",
    val type: String = "",
    val duration: Int = 0, // in minutes
    val calories: Int = 0,
    val date: String = ""  // simple string for now
)
