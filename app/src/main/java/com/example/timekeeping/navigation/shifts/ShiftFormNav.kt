package com.example.timekeeping.navigation.shifts

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.ui.shifts.ShiftInputForm
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.view_models.ShiftViewModel

fun NavGraphBuilder.addShiftFormScreen(navController: NavController) {

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