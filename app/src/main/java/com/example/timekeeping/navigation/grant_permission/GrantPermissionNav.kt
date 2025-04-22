package com.example.timekeeping.navigation.grant_permission

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.check_in.grant.GrantPermissionScreen

fun NavGraphBuilder.addGrantPermissionNav(navController: NavController) {
    composable(
        route = Screen.GrantPermission.route,
        arguments = listOf(
            navArgument("employeeId") { type = NavType.StringType },
            navArgument("groupId") { type = NavType.StringType }
        )
    ){
        backStackEntry ->
        val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        GrantPermissionScreen(
            groupId = groupId,
            employeeId = employeeId,
            onBackClick = { navController.popBackStack() },
            onPermissionGranted = {},
            onPermissionDenied = {}
        )
    }
}