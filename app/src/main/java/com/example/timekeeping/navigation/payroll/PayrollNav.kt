package com.example.timekeeping.navigation.payroll

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.groups.MonthlyPayrollScreen

fun NavGraphBuilder.addPayrollNav(navController: NavController) {
    composable(
        route = Screen.MonthlyPayroll.route,
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ){
        backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        MonthlyPayrollScreen(
            groupId = groupId,
            onBackClick = { navController.popBackStack() }
        )
    }
}