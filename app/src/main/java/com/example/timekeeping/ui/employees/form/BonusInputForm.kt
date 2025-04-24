package com.example.timekeeping.ui.employees.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Adjustment
import com.example.timekeeping.models.Salary
import com.example.timekeeping.ui.assignment.components.CalendarHeader
import com.example.timekeeping.ui.calender.CalendarState
import com.example.timekeeping.ui.components.TopBarWithDoneAction
import com.example.timekeeping.utils.DateTimeMap
import com.example.timekeeping.utils.convertLocalDateToDate
import com.example.timekeeping.view_models.SalaryViewModel
import java.time.LocalDateTime

enum class TypeAllowance(val label: String) {
    ReachTarget("Đạt chỉ tiêu"),
    Holiday("Thưởng dịp lễ, tết"),
    Travel("Phụ cấp đi lại"),
    Junket("Phụ cấp ăn uống"),
    Diligence("Phụ cấp chuyên cần"),
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BonusInputForm(
    groupId: String = "",
    employeeId: String = "",
    onBackClick: () -> Unit = {},
    onSave: (Adjustment) -> Unit = {},
    salaryViewModel: SalaryViewModel = hiltViewModel(),
    state: CalendarState
) {

    var amount by remember { mutableStateOf(0) }
    var note by remember { mutableStateOf(TextFieldValue()) }
    var selectedType by remember { mutableStateOf(TypeAllowance.ReachTarget) }

    Scaffold(
        topBar = {
            TopBarWithDoneAction(
                title = "Thêm phụ cấp",
                onBackClick = onBackClick,
                onDoneClick = {}
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Chọn loại phụ cấp",
                style = MaterialTheme.typography.titleMedium
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TypeAllowance.entries.forEach { type ->
                    TypeAllowanceItem(
                        onTypeClick = { },
                        modifier = Modifier,
                        type = type.label
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Số tiền", fontSize = 16.sp)
                TextField(
                    value = amount.toString(),
                    onValueChange = {
                        amount = it.toIntOrNull() ?: 0
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Nhập số tiền") }
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Ngày", fontSize = 16.sp)
                CalendarHeader(
                    state = state,
                    Modifier.fillMaxWidth()
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Ghi chú", fontSize = 16.sp)
                TextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Nhập ghi chú") }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {onSave(
                    Adjustment(
                        adjustmentType = selectedType.label,
                        adjustmentAmount = amount,
                        note = note.text,
                        createdAt = DateTimeMap.from(LocalDateTime.now())
                    )
                )},
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Lưu")
            }
        }
    }
}

@Composable
fun TypeAllowanceItem(
    onTypeClick: (TypeAllowance) -> Unit,
    modifier: Modifier = Modifier,
    type: String = ""
) {
    Card(
        modifier = modifier
            .clickable { onTypeClick(type.convertToAllowanceType() ?: TypeAllowance.ReachTarget) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = type,
                fontSize = 16.sp
            )
        }
    }
}

fun String.convertToAllowanceType() : TypeAllowance?{
    return TypeAllowance.entries.find { it.label == this }
}