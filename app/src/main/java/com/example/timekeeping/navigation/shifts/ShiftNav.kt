package com.example.timekeeping.navigation.shifts

import com.example.timekeeping.ui.shifts.ShiftManagementScreen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.ui.shifts.ShiftInputForm
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.view_models.ShiftViewModel

fun NavGraphBuilder.addShiftScreen(navController: NavController) {
    composable(
        route = Screen.ShiftManagement.route,
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ) { backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        ShiftManagementScreen(
            onBackClick = { navController.popBackStack() },
            viewModel = ShiftViewModel(groupId = groupId),
            onAddShiftClick = {
                navController.navigate(Screen.ShiftInputForm.createRoute(groupId))
            },
            onEditClick = { shiftId ->
                navController.navigate(Screen.ShiftEditForm.createRoute(groupId, shiftId))
            },
            onDeleteClick = { shiftId ->
                ShiftViewModel(groupId = groupId).delete(shiftId)
            }
        )
    }

    composable(
        route = Screen.ShiftInputForm.route,
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ) { backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        ShiftInputForm(
            onSave = { shift ->
                ShiftViewModel(groupId = groupId).create(shift)
                navController.popBackStack()
            },
            groupId = groupId,
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(
        route = Screen.ShiftEditForm.route,
        arguments = listOf(
            navArgument("groupId") { type = NavType.StringType },
            navArgument("shiftId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        val shiftId = backStackEntry.arguments?.getString("shiftId") ?: ""

        ShiftInputForm(
            onSave = { shift ->
                ShiftViewModel(groupId = groupId).update(shiftId, shift)
                navController.popBackStack()
            },
            groupId = groupId,
            shiftId = shiftId,
            onBackClick = { navController.popBackStack() }
        )
    }

}