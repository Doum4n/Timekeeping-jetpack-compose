package com.example.timekeeping.navigation.employee

import android.util.Log
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.employees.EmployeeManagementScreen
import com.example.timekeeping.ui.employees.MenuItem
import com.example.timekeeping.view_models.EmployeeViewModel

fun NavGraphBuilder.addEmployeeScreen(navController: NavHostController) {
    composable(
        route = Screen.EmployeeManagement.route,
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ) { backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        EmployeeManagementScreen(
            onBackClick = { navController.popBackStack() },
            onMenuItemClick = { menuItem ->
                when (menuItem) {
                    MenuItem.ADD -> {
                        navController.navigate(Screen.EmployeeForm.createRoute(groupId))
                    }
                }
            },
        )
    }
}