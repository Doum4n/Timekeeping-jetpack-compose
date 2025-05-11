package com.example.timekeeping.navigation.admin.request

import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.admin.employees.form.RequestInputScreen
import com.example.timekeeping.view_models.RequestViewModel

fun NavGraphBuilder.addRequestAdvanceNav(navController: NavController) {
    composable(
        route = Screen.RequestInput.route,
        arguments = listOf(
            navArgument("employeeId"){type = NavType.StringType},
            navArgument("groupId"){type = NavType.StringType}
        )
    ){
        backStackEntry ->
        val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""

        val requestAdvanceViewModel = hiltViewModel<RequestViewModel>()

        RequestInputScreen(
            employeeId = employeeId,
            groupId = groupId,
            onBackClick = { navController.popBackStack() },
            onSendRequestAdvance = {
                requestAdvanceViewModel.createRequest(it){
                    Toast.makeText(navController.context, "Gửi yêu cầu thành công", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            }
        )
    }
}