package com.example.timekeeping.navigation.admin.approvalRequest

import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.models.Adjustment
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.admin.approval_request.ApproveRequest
import com.example.timekeeping.view_models.AttendanceViewModel
import com.example.timekeeping.view_models.RequestViewModel
import com.example.timekeeping.view_models.SalaryViewModel

fun NavGraphBuilder.addApprovalRequestNav(navController: NavController) {
    composable(
        route = Screen.ApprovalRequest.route,
        arguments = listOf(
            navArgument("groupId"){type = NavType.StringType}
        )
    ){
        backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""

        val requestViewModel : RequestViewModel = hiltViewModel()
        var salaryViewModel : SalaryViewModel = hiltViewModel()
        val attendanceViewModel : AttendanceViewModel = hiltViewModel()

        ApproveRequest(
            groupId = groupId,
            onBackClick = {
                navController.popBackStack()
            },
            onAccept = {
                Toast.makeText(navController.context, "Đã duyệt yêu cầu!", Toast.LENGTH_SHORT).show()
                if (it.status == "Đã duyệt") {
                    requestViewModel.updateRequest(it.id, "Đã duyệt")
                    salaryViewModel.createAdjustSalary(
                        Adjustment(
                            adjustmentType = it.type,
                            adjustmentAmount = -it.amount,
                            note = it.reason,
                            employeeId = it.employeeId,
                            groupId = groupId,
                            createdAt = it.createdAt
                        ),
                        {
                            Toast.makeText(
                                navController.context,
                                "Phê duyệt thành công!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }, {
                            Toast.makeText(
                                navController.context,
                                "Phê duyệt thất bại!, $it",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }else if (it.status == "Từ chối") {
                    requestViewModel.updateRequest(it.id, "Từ chối")
                    Toast.makeText(
                        navController.context,
                        "Từ chối thành công!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
}