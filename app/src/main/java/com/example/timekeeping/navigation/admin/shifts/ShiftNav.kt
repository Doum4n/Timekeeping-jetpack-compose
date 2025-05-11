package com.example.timekeeping.navigation.admin.shifts

import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.ui.admin.shifts.ShiftManagementScreen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.view_models.ShiftViewModel

fun NavGraphBuilder.addShiftScreen(navController: NavController) {
    composable(
        route = Screen.ShiftManagement.route,
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ) { backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""

        val shiftViewModel: ShiftViewModel = hiltViewModel()

        ShiftManagementScreen(
            onBackClick = { navController.popBackStack() },
            onAddShiftClick = {
                navController.navigate(Screen.ShiftInputForm.createRoute(groupId))
            },
            onEditClick = { shiftId ->
                navController.navigate(Screen.ShiftEditForm.createRoute(groupId, shiftId))
            },
            onDeleteClick = { shiftId ->
                shiftViewModel.delete(shiftId)
            }
        )
    }
}