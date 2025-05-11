package com.example.timekeeping.navigation.admin.request

import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import coil.size.SizeResolver
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.admin.employees.form.RequestScreen
import com.example.timekeeping.view_models.RequestViewModel

fun NavGraphBuilder.addRequestFormNav(navController: NavController) {
    composable(
        route = Screen.RequestManagement.route,
        arguments = listOf(
            navArgument("groupId"){type = NavType.StringType},
            navArgument("employeeId"){type = NavType.StringType}
        )
    ){
        backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""

        val viewModel = hiltViewModel<RequestViewModel>()

        RequestScreen(
            employeeId,
            groupId,
            onBackClick = {
                navController.popBackStack()
            },
            onAddRequestClick = {
                navController.navigate(Screen.RequestInput.createRoute(groupId, employeeId))
            },
            onDeleteRequestClick = {
                viewModel.deleteRequest(it.id, {
                    Toast.makeText(navController.context, "Xóa yêu cầu thành công", Toast.LENGTH_SHORT).show()
                }, {
                    Toast.makeText(navController.context, "Xóa yêu cầu thất bại", Toast.LENGTH_SHORT).show()
                })
            }
        )
    }
}