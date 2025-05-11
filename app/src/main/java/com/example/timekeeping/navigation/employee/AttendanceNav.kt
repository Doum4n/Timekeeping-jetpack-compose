package com.example.timekeeping.navigation.employee

import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.models.Attendance
import com.example.timekeeping.models.Time
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.employee.AttendaceSceen
import com.example.timekeeping.utils.DateTimeMap
import com.example.timekeeping.utils.convertToReference
import com.example.timekeeping.view_models.AttendanceViewModel
import java.time.LocalDate
import java.time.LocalDateTime

fun NavGraphBuilder.addAttendanceNav(navController: NavController) {
    composable(
        route = Screen.AttendanceForm.route,
        arguments = listOf(
            navArgument("groupId"){ type = NavType.StringType},
            navArgument("employeeId"){ type = NavType.StringType}
        )
    ){
        backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""

        val attendanceViewModel: AttendanceViewModel = hiltViewModel()

        AttendaceSceen(
            onBackClick = {
                navController.popBackStack()
            },
            onClick = {
                attendanceViewModel.CheckIn(
                    Attendance(
                        employeeId = employeeId.convertToReference("employees"),
                        shiftId = it.id,
                        startTime = DateTimeMap.from(LocalDateTime.now()),
                        attendanceType = "Đi làm"
                    )
                )
                Toast.makeText(navController.context, "Vào ca thành công", Toast.LENGTH_SHORT).show()
            },
            onCheckOut = { attendance ->
                attendanceViewModel.CheckOut(
                    attendance.copy(
                        endTime = DateTimeMap.from(LocalDateTime.now())
                    )
                )
                Toast.makeText(navController.context, "Ra ca thành công", Toast.LENGTH_SHORT).show()
            },
            employeeId = employeeId,
            groupId = groupId
        )
    }
}