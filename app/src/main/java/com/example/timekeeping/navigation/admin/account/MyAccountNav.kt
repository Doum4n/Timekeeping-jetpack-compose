package com.example.timekeeping.navigation.admin.account

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.admin.account.MyAccountInputScreen
import com.example.timekeeping.ui.admin.account.MyQRCodeScreen
import com.example.timekeeping.view_models.EmployeeViewModel

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

        val employeeViewModel = hiltViewModel<EmployeeViewModel>()

        MyAccountInputScreen(
            employeeId = employeeId,
            onBackClick = { navController.popBackStack() },
            onUpdate = { employee ->
                employeeViewModel.updateEmployee(employee)
            }
        )
    }
}