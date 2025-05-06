package com.example.timekeeping.ui.employees.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.timekeeping.ui.calender.CalendarState
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
    onPaymentClick: () -> Unit = {},
    onBackToEmployeeList: () -> Unit = {},

    state: CalendarState
) {
    Scaffold(
        topBar = {
            TopBarClassic(
                title = "Thông tin nhân viên",
                onBackClick = onBackClick
            )
        },
    ){paddingValues ->
        LazyColumn (modifier = Modifier.padding(paddingValues)) {
            item {
                EmployeeDetailGrid(
                    employeeId = employeeId,
                    groupId = groupId,
                    onEmployeeInfoClick = onEmployeeInfoClick,
                    onBonusClick = onBonusClick,
                    onMinusMoneyClick = onMinusMoneyClick,
                    onAdvanceSalaryClick = onAdvanceSalaryClick,
                    onPaymentClick = onPaymentClick,
                    onBackToEmployeeList = onBackToEmployeeList,
                    state = state
                )
            }
        }
    }
}

@Preview
@Composable
fun Preview(){
    EmployeeDetail(
        employeeId = TODO(),
        groupId = TODO(),
        onBackClick = TODO(),
        onEmployeeInfoClick = TODO(),
        onBonusClick = TODO(),
        onMinusMoneyClick = TODO(),
        onAdvanceSalaryClick = TODO(),
        onPaymentClick = TODO(),
        onBackToEmployeeList = TODO(),
        state = TODO()
    )
}