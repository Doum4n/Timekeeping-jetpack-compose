package com.example.timekeeping.navigation.employee

import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.calender.CalendarState
import com.example.timekeeping.ui.employees.EmployeeInputForm
import com.example.timekeeping.ui.employees.form.BonusInputForm
import com.example.timekeeping.ui.employees.form.BonusScreen
import com.example.timekeeping.ui.employees.form.DeductMoneyInputScreen
import com.example.timekeeping.ui.employees.form.DeductMoneyScreen
import com.example.timekeeping.ui.employees.form.PaymentScreen
import com.example.timekeeping.ui.employees.form.SalaryAdvanceInputForm
import com.example.timekeeping.ui.employees.form.SalaryAdvanceScreen
import com.example.timekeeping.view_models.EmployeeViewModel
import com.example.timekeeping.view_models.SalaryViewModel

fun NavGraphBuilder.addEmployeeFormScreen(navController: NavHostController) {
    composable(
        route = Screen.EmployeeForm.route,
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ) {
        val groupId = it.arguments?.getString("groupId") ?: ""

        val employeeViewModel: EmployeeViewModel = hiltViewModel()

        EmployeeInputForm(
            onBackClick = { navController.popBackStack() },
            onSave = {
                employees ->
                employeeViewModel.saveEmployees(employees,
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

    composable(
        route = Screen.BonusForm.route,
        arguments = listOf(
            navArgument("groupId") { type = NavType.StringType },
            navArgument("employeeId") { type = NavType.StringType }
        )
        ){
            backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
            BonusScreen(
                groupId = groupId,
                employeeId = employeeId,
                onBackClick = { navController.popBackStack() },
                onAddBonus = { navController.navigate(Screen.BonusInputForm.createRoute(groupId, employeeId)) }
            )
        }

    composable(
        route = Screen.BonusInputForm.route,
        arguments = listOf(
            navArgument("groupId") { type = NavType.StringType },
            navArgument("employeeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""

        val salaryViewModel : SalaryViewModel = hiltViewModel()

        BonusInputForm(
            groupId = groupId,
            employeeId = employeeId,
            onBackClick = { navController.popBackStack() },
            onSave = { adjustments ->
                salaryViewModel.createAdjustSalary(
                    groupId,
                    employeeId,
                    adjustments,
                    onSuccess = {
                        Toast.makeText(navController.context, "Salary saved successfully", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onFailure = { exception ->
                        Toast.makeText(navController.context, "Failed to save salary: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            state = CalendarState()
        )
    }

    composable(
        route = Screen.MinusMoneyForm.route,
        arguments = listOf(
            navArgument("groupId") { type = NavType.StringType },
            navArgument("employeeId") { type = NavType.StringType }
            )
        ) {
        backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""

        DeductMoneyScreen(
                groupId = groupId,
                employeeId = employeeId,
                onBackClick = { navController.popBackStack() },
                state = CalendarState(),
                onMinusMoney = { navController.navigate(Screen.MinusMoneyInputForm.createRoute(groupId, employeeId)) }
            )
        }

    composable(
        route = Screen.MinusMoneyInputForm.route,
        arguments = listOf(
            navArgument("groupId") { type = NavType.StringType },
            navArgument("employeeId") { type = NavType.StringType }
        )
    ){ backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""

        val salaryViewModel : SalaryViewModel = hiltViewModel()

        DeductMoneyInputScreen(
            groupId = groupId,
            employeeId = employeeId,
            onBackClick = { navController.popBackStack() },
            onSave = { adjustments ->
                salaryViewModel.createAdjustSalary(
                    groupId,
                    employeeId,
                    adjustments,
                    onSuccess = {
                        Toast.makeText(navController.context, "Salary saved successfully", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onFailure = { exception ->
                        Toast.makeText(navController.context, "Failed to save salary: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            state = CalendarState()
        )
    }

    composable(
        route = Screen.SalaryAdvanceForm.route,
        arguments = listOf(
            navArgument("groupId") { type = NavType.StringType },
            navArgument("employeeId") { type = NavType.StringType }
        )
    ){ backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""

        SalaryAdvanceScreen(
            groupId = groupId,
            employeeId = employeeId,
            onBackClick = { navController.popBackStack() },
            onAddSalaryAdvance = { navController.navigate(Screen.SalaryAdvanceInputForm.createRoute(groupId, employeeId)) }
        )
    }

    composable(
        route = Screen.SalaryAdvanceInputForm.route,
        arguments = listOf(
            navArgument("groupId") { type = NavType.StringType },
            navArgument("employeeId") { type = NavType.StringType }
        )
    ){
        backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""

        val salaryViewModel : SalaryViewModel = hiltViewModel()

        SalaryAdvanceInputForm(
            groupId = groupId,
            employeeId = employeeId,
            onBackClick = { navController.popBackStack() },
            onSave = { adjustments ->
                salaryViewModel.createAdjustSalary(
                    groupId,
                    employeeId,
                    adjustments,
                    onSuccess = {
                        Toast.makeText(navController.context, "Salary saved successfully", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onFailure = { exception ->
                        Toast.makeText(navController.context, "Failed to save salary: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            state = CalendarState()
        )
    }

    composable(
        route = Screen.PaymentForm.route,
        arguments = listOf(
            navArgument("groupId") { type = NavType.StringType },
            navArgument("employeeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
        PaymentScreen(
            groupId = groupId,
            employeeId = employeeId,
            onBack = { navController.popBackStack() },
            state = CalendarState()
        )
    }
}