package com.example.timekeeping.navigation.team

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.teams.TeamManagementScreen
import com.example.timekeeping.view_models.TeamViewModel

fun NavGraphBuilder.addTeamScreen(navController: NavController) {
    composable(
        route = Screen.TeamManagement.route,
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ) {
        val groupId = it.arguments?.getString("groupId") ?: ""
        TeamManagementScreen(
            groupId = groupId,
            viewModel = TeamViewModel(groupId),
            onBackClick = { navController.popBackStack() },
            onAddTeamClick = { navController.navigate(Screen.TeamForm.createRoute(groupId)) }
        )
    }
}