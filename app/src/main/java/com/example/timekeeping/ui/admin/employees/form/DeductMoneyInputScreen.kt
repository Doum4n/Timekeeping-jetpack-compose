package com.example.timekeeping.ui.admin.employees.form

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.timekeeping.utils.toPositive
import com.example.timekeeping.view_models.SalaryViewModel
import java.time.LocalDateTime

enum class TypeDeduct(val label: String) {
    NotCome("Không đi làm"),
    Late("Đi muộn"),
    NotReachTarget("Không đạt chỉ tiêu"),
    Insurance("Bảo hiểm"),
    Other("Khác"),

    SalaryAdvance("Ứng lương")
}

fun String.convertTypeDeductToLabel(): TypeDeduct {
    return TypeDeduct.entries.find { it.label == this } ?: TypeDeduct.Other
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeductMoneyInputScreen(
    groupId: String = "",
    employeeId: String = "",
    adjustmentId: String = "",
    onBackClick: () -> Unit = {},
    onSave: (Adjustment) -> Unit = {},
    salaryViewModel: SalaryViewModel = hiltViewModel(),
    state: CalendarState
) {


    var amount by remember { mutableStateOf(TextFieldValue()) }
    var note by remember { mutableStateOf(TextFieldValue()) }
    var selectedType by remember { mutableStateOf(TypeDeduct.NotCome) }

    LaunchedEffect(adjustmentId) {
        if (adjustmentId != "") {
            salaryViewModel.getAdjustSalary(
                adjustmentId,
                { adjustment ->
                    if (adjustment != null) {
                        amount = TextFieldValue(adjustment.adjustmentAmount.toPositive().toString())
                        note = TextFieldValue(adjustment.note)
                        selectedType = adjustment.adjustmentType.convertTypeDeductToLabel();
                    }
                })
        }
    }

    Scaffold(
        topBar = {
            TopBarWithDoneAction(
                title = "Trừ tiền",
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
                TypeDeduct.entries.filter { it.label != "Ứng lương" }.forEach { type ->
                    TypeDeductItem(
                        isSelected = type == selectedType,
                        onTypeClick = {
                            selectedType = it
                        },
                        modifier = Modifier,
                        type = type.label
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Số tiền", fontSize = 16.sp)
                TextField(
                    value = amount,
                    onValueChange = { amount = it },
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
                        groupId = groupId,
                        employeeId = employeeId,
                        adjustmentType = selectedType.label,
                        adjustmentAmount = -amount.text.toInt(),
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
fun TypeDeductItem(
    onTypeClick: (TypeDeduct) -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    type: String = ""
) {
    Card(
        modifier = modifier
            .clickable { onTypeClick(type.convertToTypeDeduct() ?: TypeDeduct.Other) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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

fun String.convertToTypeDeduct(): TypeDeduct? {
    return TypeDeduct.entries.find { it.label == this }
}
