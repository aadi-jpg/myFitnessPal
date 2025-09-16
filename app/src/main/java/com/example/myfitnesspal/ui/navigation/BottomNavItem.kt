package com.example.myfitnesspal.ui.navigation

import androidx.annotation.DrawableRes
import com.example.myfitnesspal.R

sealed class BottomNavItem(
    val route: String,
    val title: String,
    @DrawableRes val icon: Int
) {
    object Dashboard : BottomNavItem("dashboard", "Dashboard", R.drawable.ic_dashboard)
    object Workouts : BottomNavItem("workouts", "Workouts", R.drawable.ic_workout)

    object Nutrition : BottomNavItem("nutrition", "Nutrition", R.drawable.ic_nutrition)
    object Progress : BottomNavItem("progress", "Progress", R.drawable.ic_progress)
    object Plan : BottomNavItem("plan", "Plan", R.drawable.ic_plan)
    object Profile : BottomNavItem("profile", "Profile", R.drawable.ic_profile)
}
