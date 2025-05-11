package com.example.timekeeping.navigation.admin.payment

import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.admin.calender.CalendarState
import com.example.timekeeping.ui.admin.employees.form.PaymentScreen
import com.example.timekeeping.view_models.PaymentViewModel

fun NavGraphBuilder.addPaymentNav(navController: NavHostController) {
    composable(
        route = Screen.PaymentForm.route,
        arguments = listOf(
            navArgument("groupId") { type = NavType.StringType },
            navArgument("employeeId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""

        val paymentViewModel: PaymentViewModel = hiltViewModel()

        PaymentScreen(
            groupId = groupId,
            employeeId = employeeId,
            onBack = { navController.popBackStack() },
            state = CalendarState(),
            onPaymentClick = { navController.navigate(Screen.PaymentInputForm.createRoute(groupId, employeeId)) },
            onEditClick = {
                paymentId -> navController.navigate(Screen.PaymentEditForm.createRoute(groupId, employeeId, paymentId))
            },
            // Lúc này cần chuyền payment thay vì id, vì thiếu ngày tháng
            // Một cách đơn giản hơn là xóa ngay trong "PaymentScreen"
            onDeleteClick = {
                payment -> paymentViewModel.deletePayment(
                    groupId,
                    employeeId,
                    payment,
                    onSuccess = {
                        Toast.makeText(navController.context, "Xóa thành công", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = { exception ->
                        Toast.makeText(navController.context, "Xóa thất bại: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )
    }
}