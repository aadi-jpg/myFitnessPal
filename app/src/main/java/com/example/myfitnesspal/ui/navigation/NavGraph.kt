package com.example.myfitnesspal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myfitnesspal.ui.screens.DashboardScreen
import com.example.myfitnesspal.ui.screens.WorkoutsScreen
import com.example.myfitnesspal.ui.screens.NutritionScreen
import com.example.myfitnesspal.ui.screens.ProgressScreen
import com.example.myfitnesspal.ui.screens.PlanScreen
import com.example.myfitnesspal.ui.screens.ProfileScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Dashboard.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Dashboard.route) { DashboardScreen(navController) }
        composable(BottomNavItem.Workouts.route) { WorkoutsScreen() }
        composable(BottomNavItem.Nutrition.route) { NutritionScreen() }
        composable(BottomNavItem.Progress.route) { ProgressScreen() }
        composable(BottomNavItem.Plan.route) { PlanScreen() }
        composable(BottomNavItem.Profile.route) { ProfileScreen() }
    }
}
