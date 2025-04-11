package com.example.timekeeping.navigation.shifts

import androidx.hilt.navigation.compose.hiltViewModel
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

        val shiftViewModel: ShiftViewModel = hiltViewModel()

        ShiftInputForm(
            onSave = { shift ->
                shiftViewModel.create(shift)
                navController.popBackStack()
            },
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

        val shiftViewModel: ShiftViewModel = hiltViewModel()

        ShiftInputForm(
            onSave = { shift ->
                shiftViewModel.update(shift)
                navController.popBackStack()
            },
            onBackClick = { navController.popBackStack() }
        )
    }

}