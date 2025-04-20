package com.example.timekeeping.ui.employees.form

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timekeeping.ui.calender.CalendarHeader
import com.example.timekeeping.ui.calender.CalendarState
import com.example.timekeeping.ui.components.TopBarClassic

@Composable
fun BonusScreen(
    groupId: String = "",
    employeeId: String = "",
    onBackClick: () -> Unit = {},
    onAddBonus: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopBarClassic(
                title = "Quản lý thưởng / Phụ cấp",
                onBackClick = onBackClick
            )
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ){
            CalendarHeader(
                state = CalendarState(),
                modifier = Modifier
            )

            InfoAllowanceSection(
                modifier = Modifier.weight(1f),
                name = "Nguyễn Văn A",
                total = 1000000
            )

            Button(
                onClick = onAddBonus
            ) {
                Text("Thêm phụ cấp")
            }
        }
    }
}

@Composable
fun InfoAllowanceSection(
    modifier: Modifier,
    name: String = "",
    total: Int,
){
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ){
                Text("Nhân viên")
            }
            Text(name)
        }
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ){
                Text("Tổng tiền")
            }
            Text(total.toString())
        }
    }
}