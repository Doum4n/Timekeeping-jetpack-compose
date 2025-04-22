package com.example.timekeeping.ui.employees.form

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.ui.calender.CalendarHeader
import com.example.timekeeping.ui.calender.CalendarState
import com.example.timekeeping.ui.components.TopBarClassic
import com.example.timekeeping.view_models.SalaryViewModel
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Composable
fun SalaryAdvanceScreen(
    groupId: String,
    employeeId: String,
    onBackClick: () -> Unit,
    onAddSalaryAdvance: () -> Unit,
    salaryViewModel: SalaryViewModel = hiltViewModel(),
    state: CalendarState = CalendarState()
) {

    val advanceMoney = salaryViewModel.advanceMoney.collectAsState()

    LaunchedEffect(advanceMoney){
        salaryViewModel.getAdvanceMoney(groupId, employeeId, state.visibleMonth.month.value, state.visibleMonth.year)
    }

    Scaffold(
        topBar = {
            TopBarClassic(
                title = "Quản lý ứng lương",
                onBackClick = onBackClick
            )
        }
    ) {
            paddingValues ->
        LazyColumn (
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ){
            item {
                CalendarHeader(
                    state = state,
                    modifier = Modifier
                )
            }

            item{
                InfoAdvanceSalarySection(
                    modifier = Modifier,
                    name = "Nguyễn Văn A",
                    total = 1000000
                )
            }

            items(salaryViewModel.advanceMoney.value){
                SalaryAdvanceHistoryItem(
                    total = -it.adjustmentAmount,
                    date =  SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it.createdAt) ,
                    modifier = Modifier
                )
            }

            item {
                Button(
                    onClick = onAddSalaryAdvance,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Thêm mới")
                }
            }
        }
    }
}

@Composable
fun InfoAdvanceSalarySection(
    modifier: Modifier,
    name: String = "",
    total: Int,
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Nhân viên", fontSize = 16.sp)
                Text(name, fontSize = 16.sp)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tổng tiền", fontSize = 16.sp)
                Text("%,d".format(total), fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun SalaryAdvanceHistoryItem(
    total: Int,
    date: String,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = Icons.Default.ThumbUp,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 12.dp)
            )
            Column {
                Text("Số tiền: %,d".format(-total), fontSize = 16.sp)
                Text("Ngày: $date", fontSize = 14.sp)
            }
        }
    }
}