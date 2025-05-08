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

            val salaryViewModel : SalaryViewModel = hiltViewModel()

            BonusScreen(
                groupId = groupId,
                employeeId = employeeId,
                onBackClick = { navController.popBackStack() },
                state = CalendarState(),
                onAddBonus = { navController.navigate(Screen.BonusInputForm.createRoute(groupId, employeeId)) },
                onEditClick = { _employeeId, adjustmentId ->
                    navController.navigate(Screen.BonusEditForm.createRoute(groupId, _employeeId, adjustmentId))
                },
                onDeleteClick = { _employeeId, adjustment ->
                    salaryViewModel.deleteAdjustSalary(
                        adjustment,
                        onSuccess = {
                            Toast.makeText(navController.context, "Xóa thành công!", Toast.LENGTH_SHORT).show()
                        },
                        onFailure = { exception ->
                            Toast.makeText(navController.context, "Xóa thất bại: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
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
            onBackClick = { navController.popBackStack() },
            onSave = { adjustments ->
                salaryViewModel.createAdjustSalary(
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
        route = Screen.BonusEditForm.route,
        arguments = listOf(
            navArgument("groupId") { type = NavType.StringType },
            navArgument("employeeId") { type = NavType.StringType },
            navArgument("adjustmentId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
        val adjustmentId = backStackEntry.arguments?.getString("adjustmentId") ?: ""
        val salaryViewModel: SalaryViewModel = hiltViewModel()
        BonusInputForm(
            onBackClick = { navController.popBackStack() },
            onSave = { adjustments ->
                salaryViewModel.updateAdjustSalary(
                    adjustments,
                    onSuccess = {
                        Toast.makeText(
                            navController.context,
                            "Cập nhật thành công!",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.popBackStack()
                    },
                    onFailure = { exception ->
                        Toast.makeText(
                            navController.context,
                            "Cập nhật thất bại: ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            },
            state = CalendarState(),
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

        val salaryViewModel : SalaryViewModel = hiltViewModel()

        DeductMoneyScreen(
                groupId = groupId,
                employeeId = employeeId,
                onBackClick = { navController.popBackStack() },
                state = CalendarState(),
                onMinusMoney = { navController.navigate(Screen.MinusMoneyInputForm.createRoute(groupId, employeeId)) },
                onEditClick = { _employeeId, adjustmentId ->
                    navController.navigate(Screen.MinusMoneyEditForm.createRoute(groupId, _employeeId, adjustmentId))
                },
                onDeleteClick = { _employeeId, adjustment ->
                    salaryViewModel.deleteAdjustSalary(
                        adjustment,
                        onSuccess = {
                            Toast.makeText(navController.context, "Xóa thành công!", Toast.LENGTH_SHORT).show()
                        },
                        onFailure = { exception ->
                            Toast.makeText(navController.context, "Xóa thất bại: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
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
        route = Screen.MinusMoneyEditForm.route,
        arguments = listOf(
            navArgument("groupId") { type = NavType.StringType },
            navArgument("employeeId") { type = NavType.StringType },
            navArgument("adjustmentId") { type = NavType.StringType }
        )
    ){
        backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
        val adjustmentId = backStackEntry.arguments?.getString("adjustmentId") ?: ""
        val salaryViewModel : SalaryViewModel = hiltViewModel()

        DeductMoneyInputScreen(
            groupId = groupId,
            employeeId = employeeId,
            adjustmentId = adjustmentId,
            onBackClick = { navController.popBackStack() },
            onSave = { adjustments ->
                salaryViewModel.updateAdjustSalary(
                    adjustments,
                    onSuccess = {
                        Toast.makeText(navController.context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onFailure = { exception ->
                        Toast.makeText(navController.context, "Cập nhâtj thất bại: ${exception.message}", Toast.LENGTH_SHORT).show()
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

        val salaryViewModel : SalaryViewModel = hiltViewModel()

        SalaryAdvanceScreen(
            groupId = groupId,
            employeeId = employeeId,
            onBackClick = { navController.popBackStack() },
            state = CalendarState(),
            onAddSalaryAdvance = { navController.navigate(Screen.SalaryAdvanceInputForm.createRoute(groupId, employeeId)) },
            onEditClick = { _employeeId, adjustmentId ->
                navController.navigate(Screen.SalaryAdvanceEditForm.createRoute(groupId, _employeeId, adjustmentId))
             },
            onDeleteClick = {
                _employeeId, adjustmentId ->
                salaryViewModel.deleteAdjustSalary(
                    adjustmentId,
                    onSuccess = {
                        Toast.makeText(navController.context, "Xóa thành công!", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = { exception ->
                        Toast.makeText(navController.context, "Xóa thất bại: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
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
        route = Screen.SalaryAdvanceEditForm.route,
        arguments = listOf(
            navArgument("groupId") { type = NavType.StringType },
            navArgument("employeeId") { type = NavType.StringType },
            navArgument("adjustmentId") { type = NavType.StringType }
        )
    ){
        backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
        val adjustmentId = backStackEntry.arguments?.getString("adjustmentId") ?: ""
        val salaryViewModel : SalaryViewModel = hiltViewModel()
        SalaryAdvanceInputForm(
            groupId = groupId,
            employeeId = employeeId,
            adjustmentId = adjustmentId,
            onBackClick = { navController.popBackStack() },
            onSave = { adjustments ->
                salaryViewModel.updateAdjustSalary(
//                    groupId,
//                    employeeId,
//                    adjustmentId,
                    adjustments,
                    onSuccess = {
                        Toast.makeText(
                            navController.context,
                            "Cập nhật thành công!",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.popBackStack()
                    },
                    onFailure = { exception ->
                        Toast.makeText(
                            navController.context,
                            "Cập nhật thất bại: ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            },
            state = CalendarState()
        )
    }

}