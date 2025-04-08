package com.example.timekeeping.navigation.schedule

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.assignment.AssignmentScreen
import com.example.timekeeping.ui.calender.CalendarState
import com.example.timekeeping.view_models.AssignmentViewModel
import com.example.timekeeping.view_models.ShiftViewModel
import com.example.timekeeping.view_models.TeamViewModel

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
            onDone = {},
            onChooseTeamClick = { navController.navigate(Screen.TeamManagement.createRoute(groupId)) },
            shiftViewModel = ShiftViewModel(groupId = groupId),
            teamViewModel = TeamViewModel(groupId = groupId),
            viewModel = AssignmentViewModel(employeeId = employeeId),
            state = CalendarState()
        )
    }
}