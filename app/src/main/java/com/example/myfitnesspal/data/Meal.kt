package com.example.myfitnesspal.data

data class Meal(
    val id: String = "",
    val food: String = "",
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fats: Int = 0,
    val date: String = "" // yyyy-MM-dd
)
