package com.example.timekeeping.ui.admin.employees.form

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Adjustment
import com.example.timekeeping.ui.admin.calender.CalendarHeader
import com.example.timekeeping.ui.admin.calender.CalendarState
import com.example.timekeeping.ui.admin.components.InfoItem
import com.example.timekeeping.ui.admin.components.TopBarClassic
import com.example.timekeeping.ui.admin.employees.components.SimpleDialogS
import com.example.timekeeping.utils.SessionManager
import com.example.timekeeping.utils.toPositive
import com.example.timekeeping.view_models.EmployeeViewModel
import com.example.timekeeping.view_models.SalaryViewModel

enum class DeductSettingField(val label: String, val value: String) {
    NAME("Nhân viên", ""),
    TOTAL("Tổng tiền", "0"),
    TIMES("Số lần", "")
}

@Composable
fun DeductMoneyScreen(
    groupId: String = "",
    employeeId: String = "",
    onBackClick: () -> Unit = {},
    onMinusMoney: () -> Unit = {},
    salaryViewModel: SalaryViewModel = hiltViewModel(),
    employeeViewModel: EmployeeViewModel = hiltViewModel(),
    onEditClick: (String, String) -> Unit,
    onDeleteClick: (String, Adjustment) -> Unit,
    state: CalendarState
) {

    val salaryInfo by salaryViewModel.salaryInfo.collectAsState()
    var name by remember { mutableStateOf("") }

    var showDialog = remember { mutableStateOf(false) }
    var selectedAdjustment by remember { mutableStateOf(Adjustment()) }
    var selectedEmployeeId by remember { mutableStateOf("") }
    val role = SessionManager.getRole().toString()

    LaunchedEffect (
        employeeId,
        groupId,
        state.visibleMonth.monthValue,
        state.visibleMonth.year

    ) {
        salaryViewModel.getDeductMoney(groupId, employeeId, state.visibleMonth.monthValue, state.visibleMonth.year)
        employeeViewModel.getName(employeeId, { name = it })
    }

    Scaffold(
        topBar = {
            TopBarClassic(
                title = "Quản trừ tiền",
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
                state = state,
                modifier = Modifier
            )

            val items = listOf(
                DeductSettingField.NAME to name,
                DeductSettingField.TOTAL to salaryInfo.sumOf { it.adjustmentAmount }.toString(),
                DeductSettingField.TIMES to salaryInfo.filter { it.adjustmentType in TypeDeduct.entries.filter { it.label != "Ứng lương" }.map { it.label } }.size.toString()
            )

            Card(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items.forEach { item ->
                    InfoItem(
                        label = item.first.label,
                        value = item.second,
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f) // chiếm hết không gian còn lại
                    .fillMaxWidth()
            ) {
                items(salaryInfo.filter {
                    it.adjustmentType in TypeDeduct.entries.filter { it.label != "Ứng lương" }
                        .map { it.label }
                }) {
                    InfoDeductMoneyItem(
                        adjustment = it,
                        employeeId = employeeId,
                        modifier = Modifier,
                        deductType = it.adjustmentType,
                        onEditClick = onEditClick,
                        onDeleteClick = { employeeId, adjustment ->
                            showDialog.value = showDialog.value.not()
                            selectedAdjustment = adjustment
                            selectedEmployeeId = employeeId
                        }
                    )
                }
            }

            if (role == "ADMIN") {
                Button(
                    onClick = onMinusMoney,
                    Modifier.fillMaxWidth()
                ) {
                    Text("Thêm mới")
                }
            }

            /* ============================================================================
             * Hiển thị Dialogs
             * ============================================================================
             */
            if(showDialog.value) {
                SimpleDialogS(
                    title = "Thông báo",
                    question = "Bạn có chắc chắn muốn xóa khoản  trừ này không?",
                    onConfirm = {
                        onDeleteClick(selectedEmployeeId, selectedAdjustment)
                        salaryViewModel.getDeductMoney(groupId, employeeId, state.visibleMonth.monthValue, state.visibleMonth.year)
                        showDialog.value = false
                    },
                    onDismiss = {
                        showDialog.value = false
                    }
                )
            }
        }
    }
}

@Composable
fun InfoDeductMoneyItem(
    modifier: Modifier,
    employeeId: String,
    adjustment: Adjustment,
    deductType: String = "",
    onEditClick: (String, String) -> Unit,
    onDeleteClick: (String, Adjustment) -> Unit,
    role: String = SessionManager.getRole().toString()
){
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAEAF6))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 12.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("Số tiền: %,d".format(adjustment.adjustmentAmount.toPositive()), fontSize = 16.sp)
                Text("Ngày: ${adjustment.createdAt.format("dd/MM/yyyy HH:mm")}", fontSize = 14.sp)
                Text("Ghi chú: ${adjustment.note}", fontSize = 14.sp)
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(deductType, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                Row {
                    if (role == "ADMIN") {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Chỉnh sửa",
                            modifier = Modifier
                                .size(24.dp)
                                .padding(4.dp)
                                .clickable { onEditClick(employeeId, adjustment.id) }
                        )
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Xóa",
                            modifier = Modifier
                                .size(24.dp)
                                .padding(4.dp)
                                .clickable { onDeleteClick(employeeId, adjustment) }
                        )
                    }
                }
            }
        }
    }
}