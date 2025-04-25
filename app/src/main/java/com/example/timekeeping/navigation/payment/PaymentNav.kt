package com.example.timekeeping.navigation.payment

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.calender.CalendarState
import com.example.timekeeping.ui.employees.form.PaymentScreen

fun NavGraphBuilder.addPaymentNav(navController: NavHostController) {
    composable(
        route = Screen.PaymentForm.route,
        arguments = listOf(
            navArgument("groupId") { type = NavType.StringType },
            navArgument("employeeId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
        PaymentScreen(
            groupId = groupId,
            employeeId = employeeId,
            onBack = { navController.popBackStack() },
            state = CalendarState(),
            onPaymentClick = { navController.navigate(Screen.PaymentInputForm.createRoute(groupId, employeeId)) }
        )
    }
}