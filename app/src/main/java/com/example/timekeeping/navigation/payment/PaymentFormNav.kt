package com.example.timekeeping.navigation.payment

import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.employees.form.PaymentInputForm
import com.example.timekeeping.view_models.PaymentViewModel

fun NavGraphBuilder.addPaymentFormNav(navController: NavHostController) {
    composable(
        route = Screen.PaymentInputForm.route,
        arguments = listOf(
            navArgument("groupId") { type = NavType.StringType },
            navArgument("employeeId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""

        val paymentViewModel : PaymentViewModel = hiltViewModel()

        PaymentInputForm(
            groupId = groupId,
            employeeId = employeeId,
            onBack = { navController.popBackStack() },
            onPaymentClick = { payment ->
                paymentViewModel.createPayment(groupId, employeeId, payment, {
                    Toast.makeText(navController.context, "Thanh toán thành công", Toast.LENGTH_SHORT).show()
                }, {
                    Toast.makeText(navController.context, "Thanh toán thất bại", Toast.LENGTH_SHORT).show()
                })
                navController.popBackStack()
            }
        )
    }
}