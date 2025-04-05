package com.example.timekeeping.navigation.employee

import android.widget.Toast
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.employees.EmployeeInputForm
import com.example.timekeeping.view_models.EmployeeViewModel

fun NavGraphBuilder.addEmployeeFormScreen(navController: NavHostController) {
    composable(
        route = Screen.EmployeeForm.route,
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ) {
        val groupId = it.arguments?.getString("groupId") ?: ""
        EmployeeInputForm(
            onBackClick = { navController.popBackStack() },
            onSave = {
                employees ->
                EmployeeViewModel().saveEmployees(employees, groupId,
                    onSuccess = {
                        Toast.makeText(navController.context, "Employees saved successfully", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onFailure = { exception ->
                        Toast.makeText(navController.context, "Failed to save employees: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )
    }
}