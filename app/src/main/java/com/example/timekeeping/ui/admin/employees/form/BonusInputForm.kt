package com.example.timekeeping.ui.admin.employees.form

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Adjustment
import com.example.timekeeping.ui.admin.assignment.components.CalendarHeader
import com.example.timekeeping.ui.admin.calender.CalendarState
import com.example.timekeeping.ui.admin.components.TopBarWithDoneAction
import com.example.timekeeping.utils.DateTimeMap
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
    onBackClick: () -> Unit = {},
    onSave: (Adjustment) -> Unit = {},
    salaryViewModel: SalaryViewModel = hiltViewModel(),
    state: CalendarState
) {

    var amount by remember { mutableStateOf(0) }
    var note by remember { mutableStateOf(TextFieldValue()) }
    var selectedType by remember { mutableStateOf(TypeAllowance.ReachTarget) }

    LaunchedEffect(salaryViewModel.adjustmentId) {
        Log.d("BonusInputForm_adjustmentId", salaryViewModel.adjustmentId)
        if(salaryViewModel.adjustmentId != ""){
            salaryViewModel.getAdjustSalary(salaryViewModel.adjustmentId, {
                if (it != null) {
                    note = TextFieldValue(it.note)
                    amount = it.adjustmentAmount
                    selectedType = it.adjustmentType.convertToAllowanceType() ?: TypeAllowance.ReachTarget
                }
            })
        }
    }

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
                        type = type.label,
                        isSelected = type == selectedType,
                        onClick = { selectedType = type },
                        modifier = Modifier.padding(8.dp)
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                        groupId = salaryViewModel.groupId,
                        employeeId = salaryViewModel.employeeId,
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
    type: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        }
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