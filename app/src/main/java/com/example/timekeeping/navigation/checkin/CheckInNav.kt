package com.example.timekeeping.navigation.checkin

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.calender.CalendarState
import com.example.timekeeping.ui.check_in.CheckInManagementScreen


fun NavGraphBuilder.addCheckInNav(navController: NavController) {
    composable(
        route = Screen.CheckInManagement.route,
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ){
        backStackEntry -> val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        CheckInManagementScreen(
            groupId = groupId,
            onBackClick = { navController.popBackStack() },
            state = CalendarState()
        )
    }
}