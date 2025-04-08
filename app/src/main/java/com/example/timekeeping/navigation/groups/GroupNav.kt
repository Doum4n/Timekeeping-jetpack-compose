package com.example.timekeeping.navigation.groups

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.ui.groups.GroupDetailScreen
import com.example.timekeeping.ui.groups.GroupSettingsScreen
import com.example.timekeeping.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

fun NavGraphBuilder.addGroupScreen(navController: NavHostController) {
    composable(
        route = Screen.GroupDetail.route,
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ) { backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        GroupDetailScreen(
            groupId = groupId,
            onEmployeeManagementClick = {
                navController.navigate(Screen.EmployeeManagement.createRoute(groupId))
            },
            onBackClick = { navController.popBackStack() },
            onShiftManagementClick = {
                navController.navigate(Screen.ShiftManagement.createRoute(groupId))
            },
            onCheckInClick = {
                navController.navigate(Screen.CheckIn.createRoute(groupId))
            },
            onSettingsClick = {
                navController.navigate(Screen.GroupSettings.createRoute(groupId))
            },
            onScheduleClick = {
                navController.navigate(Screen.Schedule.createRoute(groupId, FirebaseAuth.getInstance().currentUser?.uid ?: ""))
            }
        )
    }

    composable(
        route = Screen.GroupSettings.route,
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ) { backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        GroupSettingsScreen(
            groupId = groupId,
            onBackClick = { navController.popBackStack() }
        )
    }
}