package com.example.timekeeping.navigation.groups

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.ui.groups.GroupDetailScreen
import com.example.timekeeping.ui.groups.GroupSettingsScreen
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.view_models.GroupViewModel
import com.google.firebase.auth.FirebaseAuth

fun NavGraphBuilder.addGroupScreen(navController: NavHostController) {
    composable(
        route = Screen.GroupDetail.route,
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ) { backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""

        val groupViewModel: GroupViewModel = hiltViewModel()

        GroupDetailScreen(
            groupId = groupId,
            onEmployeeManagementClick = {
                navController.navigate(Screen.EmployeeManagement.createRoute(groupId))
            },
            onBackClick = { navController.popBackStack() },
            onShiftManagementClick = {
                navController.navigate(Screen.ShiftManagement.createRoute(groupId))
            },
            onCheckInClick = {
                navController.navigate(Screen.CheckInManagement.createRoute(groupId))
            },
            onSettingsClick = {
                navController.navigate(Screen.EditGroup.createRoute(groupId))
            },
            onScheduleClick = {
                navController.navigate(Screen.Schedule.createRoute(groupId, FirebaseAuth.getInstance().currentUser?.uid ?: ""))
            },
            onDelete = {
                groupViewModel.deleteGroup(groupId)
            },
            onSalaryClick = {
                navController.navigate(Screen.MonthlyPayroll.createRoute(groupId))
            },
            onRuleManagementClick = {
                navController.navigate(Screen.RuleManagement.createRoute(groupId))
            },
            onApproveRequestClick = {
                navController.navigate(Screen.ApprovalRequest.createRoute(groupId))
            }
        )
    }

    composable(
        route = Screen.GroupSettings.route,
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ) { backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        GroupSettingsScreen(
            groupId = groupId,
            onBackClick = { navController.popBackStack() }
        )
    }
}