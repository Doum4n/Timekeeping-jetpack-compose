package com.example.timekeeping.navigation.account

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.account.MyAccountInputScreen
import com.example.timekeeping.ui.account.MyQRCodeScreen

fun NavGraphBuilder.addMyAccountNav(navController: NavController){
    composable(
        route = Screen.MyQRCode.route,
        arguments = listOf(navArgument("employeeId") { type = NavType.StringType })
    ){
        backStackEntry ->
        val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
        MyQRCodeScreen(
            employeeId = employeeId,
            onBack = { navController.popBackStack() }
        )
    }

    composable(
        route = Screen.EditAccountInfo.route,
        arguments = listOf(navArgument("employeeId") { type = NavType.StringType })
    ){
        backStackEntry ->
        val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
        MyAccountInputScreen(
            employeeId = employeeId,
            onBackClick = { navController.popBackStack() }
        )
    }
}