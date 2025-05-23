package com.example.timekeeping.navigation.admin.employee

import android.util.Log
import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.admin.calender.rememberCalendarState
import com.example.timekeeping.ui.admin.employees.details.EmployeeDetail
import com.example.timekeeping.ui.admin.employees.employee_info.EmployeeInfoScreen
import com.example.timekeeping.ui.admin.employees.EmployeeManagementScreen
import com.example.timekeeping.ui.admin.employees.MenuItem
import com.example.timekeeping.view_models.EmployeeViewModel

fun NavGraphBuilder.addEmployeeScreen(navController: NavHostController) {
    composable(
        route = Screen.EmployeeManagement.route,
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ) { backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId")
        if (groupId.isNullOrEmpty()) {
            Log.e("Navigation", "groupId is null or empty -> abort screen")
            return@composable
        }

        val employeeViewModel: EmployeeViewModel = hiltViewModel()
        EmployeeManagementScreen(
            viewModel = employeeViewModel,
            onBackClick = { navController.popBackStack() },
            onMenuItemClick = { menuItem ->
                when (menuItem) {
                    MenuItem.ADD -> {
                        navController.navigate(Screen.EmployeeForm.createRoute(groupId))
                    }
                }
            },
            onEmployeeIdClick = { employeeId ->
                navController.navigate(Screen.EmployeeDetail.createRoute(groupId, employeeId))
            },
            onLinkClick = { employeeId ->
                navController.navigate(Screen.GrantPermission.createRoute(groupId, employeeId))
            },
            onAcceptClick = { employeeId ->
                employeeViewModel.acceptEmployee(groupId, employeeId, {
                    Toast.makeText(navController.context, "Đã chấp nhận yêu cầu", Toast.LENGTH_SHORT).show()
                }, {
                    Toast.makeText(navController.context, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                })
            },
            onRejectClick = { employeeId ->
                employeeViewModel.rejectEmployee(groupId, employeeId, {
                    Toast.makeText(navController.context, "Đã từ chối yêu cầu", Toast.LENGTH_SHORT).show()
                }, {
                    Toast.makeText(navController.context, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                })
            }
        )
    }

    composable(
        route = Screen.EmployeeInfo.route,
        arguments = listOf(
            navArgument("employeeId") { type = NavType.StringType },
            navArgument("groupId") { type = NavType.StringType }
        )
    ){
        backStackEntry ->
        val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        EmployeeInfoScreen(
            employeeId = employeeId,
            groupId = groupId,
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(
        route = Screen.EmployeeDetail.route,
        arguments = listOf(
            navArgument("employeeId") { type = NavType.StringType },
            navArgument("groupId") { type = NavType.StringType }
        )
    ){
        backStackEntry ->
        val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        EmployeeDetail(
            employeeId,
            groupId,
            onBackClick = { navController.popBackStack() },
            onEmployeeInfoClick = { navController.navigate(Screen.EmployeeInfo.createRoute(groupId, employeeId)) },
            onBonusClick = { navController.navigate(Screen.BonusForm.createRoute(groupId, employeeId)) },
            onMinusMoneyClick = { navController.navigate(Screen.MinusMoneyForm.createRoute(groupId, employeeId)) },
            onAdvanceSalaryClick = { navController.navigate(Screen.SalaryAdvanceForm.createRoute(groupId, employeeId)) },
            onPaymentClick = { navController.navigate(Screen.PaymentForm.createRoute(groupId, employeeId)) },
            onBackToEmployeeList = { navController.navigate(Screen.EmployeeManagement.createRoute(groupId)) },
            onRequestAdvanceSalaryClick = { navController.navigate(Screen.RequestManagement.createRoute(groupId, employeeId)) },
            onAttendanceClick = { navController.navigate(Screen.AttendanceForm.createRoute(groupId, employeeId)) },
            state = rememberCalendarState()
        )
    }
}