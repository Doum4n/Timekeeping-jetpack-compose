package com.example.timekeeping.navigation.admin.schedule

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.admin.assignment.AssignmentScreen
import com.example.timekeeping.ui.admin.calender.CalendarState

fun NavGraphBuilder.addScheduleScreen(navController: NavHostController) {
    composable(
        route = Screen.Schedule.route,
        arguments = listOf(
            navArgument("groupId") { type = NavType.StringType },
            navArgument("employeeId") { type = NavType.StringType }
        )
    ) {
        val groupId = it.arguments?.getString("groupId") ?: ""
        val employeeId = it.arguments?.getString("employeeId") ?: ""
        AssignmentScreen(
            onBackClick = { navController.popBackStack() },
            onChooseTeamClick = { navController.navigate(Screen.TeamManagement.createRoute(groupId)) },
            state = CalendarState(),
        )
    }
}