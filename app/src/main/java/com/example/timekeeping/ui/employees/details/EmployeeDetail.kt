package com.example.timekeeping.ui.employees.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.timekeeping.ui.components.TopBarClassic

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetail(
    employeeId: String = "",
    groupId: String = "",
    onBackClick: () -> Unit = {},
    onEmployeeInfoClick: () -> Unit = {},
    onBonusClick: () -> Unit = {},
    onMinusMoneyClick: () -> Unit = {},
    onAdvanceSalaryClick: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopBarClassic(
                title = "Thông tin nhân viên",
                onBackClick = onBackClick
            )
        },
    ){paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            EmployeeDetailGrid(
                employeeId = employeeId,
                groupId = groupId,
                onEmployeeInfoClick = onEmployeeInfoClick,
                onBonusClick = onBonusClick,
                onMinusMoneyClick = onMinusMoneyClick,
                onAdvanceSalaryClick = onAdvanceSalaryClick
            )
        }
    }
}

@Preview
@Composable
fun Preview(){
    EmployeeDetail()
}