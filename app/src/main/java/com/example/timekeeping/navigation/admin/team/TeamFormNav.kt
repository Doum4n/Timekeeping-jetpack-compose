package com.example.timekeeping.navigation.admin.team

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.admin.teams.TeamInputFormScreen
import com.example.timekeeping.view_models.TeamViewModel

fun NavGraphBuilder.addTeamFormScreen(navController: NavController) {
    composable(
        route = Screen.TeamForm.route,
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ) {
        val groupId = it.arguments?.getString("groupId") ?: ""

        val teamViewModel: TeamViewModel = hiltViewModel()

        TeamInputFormScreen(
            onSubmit = { team ->
                teamViewModel.createTeam(team)
                navController.popBackStack()
            },
            onCancel = { navController.popBackStack() }
        )
    }

    composable(
        route = Screen.TeamEditForm.route,
        arguments = listOf(
            navArgument("groupId") { type = NavType.StringType },
            navArgument("teamId") { type = NavType.StringType }
        )
    ){
        val groupId = it.arguments?.getString("groupId") ?: ""
        val teamId = it.arguments?.getString("teamId") ?: ""

        val teamViewModel: TeamViewModel = hiltViewModel()

        TeamInputFormScreen(
            teamId = teamId,
            onSubmit = { team ->
                teamViewModel.updateTeam(team)
                navController.popBackStack()
            },
            onCancel = { navController.popBackStack() }
        )
    }
}