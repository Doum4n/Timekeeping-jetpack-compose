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
    onBackClick: () -> Unit = {},
    onEmployeeInfoClick: () -> Unit = {}
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
                onEmployeeInfoClick = onEmployeeInfoClick
            )
        }
    }
}

@Preview
@Composable
fun Preview(){
    EmployeeDetail()
}