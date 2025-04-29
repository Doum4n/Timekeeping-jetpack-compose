package com.example.timekeeping.navigation.team

import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
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

        val teamViewModel: TeamViewModel = hiltViewModel()

        TeamManagementScreen(
            groupId = groupId,
            onBackClick = { navController.popBackStack() },
            onAddTeamClick = { navController.navigate(Screen.TeamForm.createRoute(groupId)) },
            onEditTeamClick = { teamId ->
                navController.navigate(Screen.TeamEditForm.createRoute(groupId, teamId))
            },
            onDeleteTeamClick = { teamId ->
                teamViewModel.deleteTeam(teamId){
                    Toast.makeText(navController.context, "Xóa tổ thành công", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}