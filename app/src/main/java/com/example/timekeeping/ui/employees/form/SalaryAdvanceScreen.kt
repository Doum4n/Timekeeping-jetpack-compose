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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Adjustment
import com.example.timekeeping.ui.calender.CalendarHeader
import com.example.timekeeping.ui.calender.CalendarState
import com.example.timekeeping.ui.components.InfoItem
import com.example.timekeeping.ui.components.TopBarClassic
import com.example.timekeeping.utils.DateTimeMap
import com.example.timekeeping.view_models.EmployeeViewModel
import com.example.timekeeping.view_models.SalaryViewModel
import com.google.type.DateTime
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

enum class AdvanceField(val label: String, val value: String){
    NAME("Nhân viên", ""),
    TOTAL("Tổng tiền", "0"),
    TIMES("Số lần", "")
}

@Composable
fun SalaryAdvanceScreen(
    groupId: String,
    employeeId: String,
    onBackClick: () -> Unit,
    onAddSalaryAdvance: () -> Unit,
    salaryViewModel: SalaryViewModel = hiltViewModel(),
    employeeViewModel: EmployeeViewModel = hiltViewModel(),
    onEditClick: (String, String) -> Unit,
    onDeleteClick: (String, Adjustment) -> Unit,
    state: CalendarState
) {

    val advanceMoney = salaryViewModel.advanceMoney.collectAsState()
    var name by remember { mutableStateOf("") }

    LaunchedEffect(state.visibleMonth) {
        salaryViewModel.getAdvanceMoney(groupId, employeeId, state.visibleMonth.month.value, state.visibleMonth.year)
        employeeViewModel.getName(employeeId, { name = it })
    }

    val items = remember(advanceMoney.value, name) {
        listOf(
            AdvanceField.NAME to name,
            AdvanceField.TOTAL to -advanceMoney.value.filter { it.adjustmentType in TypeDeduct.entries.map { it.label } }.sumOf { it.adjustmentAmount },
            AdvanceField.TIMES to advanceMoney.value.size
        )
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
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 1. Calendar header
            CalendarHeader(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // 2. Card bọc InfoItems
            Card(
                modifier = Modifier
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    items.forEach { item ->
                        InfoItem(
                            label = item.first.label,
                            value = item.second.toString()
                        )
                    }
                }
            }

            // 3. LazyColumn chỉ cho danh sách history
            LazyColumn(
                modifier = Modifier
                    .weight(1f) // chiếm hết không gian còn lại
                    .fillMaxWidth()
            ) {
                items(salaryViewModel.advanceMoney.value) {
                    SalaryAdvanceHistoryItem(
                        employeeId = employeeId,
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick,
                        adjustment = it,
                        modifier = Modifier
                    )
                }
            }

            // 4. Button thêm mới
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

@Composable
fun SalaryAdvanceHistoryItem(
    employeeId: String,
    adjustment: Adjustment,
    onEditClick: (String, String) -> Unit,
    onDeleteClick: (String, Adjustment) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ThumbUp,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = "Số tiền: %,d".format(-adjustment.adjustmentAmount),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ngày: ${adjustment.createdAt.format("dd/MM/yyyy HH:mm")}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Ghi chú: ${adjustment.note}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onEditClick(employeeId, adjustment.id) },
                    tint = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onDeleteClick(employeeId, adjustment) },
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
