package com.example.timekeeping.ui.employees.employee_info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Salary
import com.example.timekeeping.ui.components.TopBarWithDoneAction
import com.example.timekeeping.ui.employees.employee_info.pages.EmployeeInfoPage
import com.example.timekeeping.ui.employees.employee_info.pages.EmployeeInfoPageScreen
import com.example.timekeeping.view_models.EmployeeViewModel
@Composable
fun EmployeeInfoScreen(
    employeeId: String,
    groupId: String,
    onBackClick: () -> Unit = {},
    employeeViewModel: EmployeeViewModel = hiltViewModel()
) {
    // Tạo state để giữ employee
    var employee by remember { mutableStateOf(Employee()) }
    var salary by remember { mutableStateOf(Salary()) }

    // Gọi lấy dữ liệu chỉ 1 lần khi Composable được dựng
    LaunchedEffect(employeeId) {
        employeeViewModel.getEmployeeById(
            employeeId,
            onSuccess = { _employee -> employee = _employee },
            onFailure = { exception -> /* handle lỗi nếu cần */ }
        )

        employeeViewModel.getSalaryById(
            employeeId,
            groupId,
            onSuccess = { _salary -> salary = _salary },
            onFailure = { exception -> /* handle lỗi nếu cần */ }
        )
    }

    val pages = listOf(
        EmployeeInfoPage.PersonalInfo(employee),
        EmployeeInfoPage.SalaryInfo(salary)
    )

    Scaffold(
        topBar = {
            TopBarWithDoneAction(
                title = "Thông tin nhân viên",
                onBackClick = onBackClick,
                onDoneClick = { /* xử lý khi nhấn nút Done */ }
            )
        }
    ) { paddingValues ->
        Column (
            modifier = Modifier.padding(paddingValues)
        ) {
            EmployeeInfoPageScreen(
                pages = pages,
                currentPage = 0,
                onTabSelected = {}
            )
        }
    }
}

//@Preview
//@Composable
//fun PreviewEmployeeInfoScreen(){
//    EmployeeInfoScreen(employeeId = "Q9mJTM0obtCKva53GXqJ")
//}