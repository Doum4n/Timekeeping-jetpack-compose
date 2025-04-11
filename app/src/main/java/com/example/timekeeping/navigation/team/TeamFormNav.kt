package com.example.timekeeping.navigation.team

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.models.Team
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.teams.TeamInputFormScreen
import com.example.timekeeping.view_models.TeamViewModel

fun NavGraphBuilder.addTeamFormScreen(navController: NavController) {
    composable(
        route = Screen.TeamForm.route,
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ) {
        val groupId = it.arguments?.getString("groupId") ?: ""

        val teamViewModel: TeamViewModel = hiltViewModel()

        TeamInputFormScreen(
            onSubmit = { name, description ->
                teamViewModel.createTeam(Team(
                    name = name,
                    description = description,
                    groupId = groupId
                ))
                navController.popBackStack()
            },
            onCancel = { navController.popBackStack() }
        )
    }
}