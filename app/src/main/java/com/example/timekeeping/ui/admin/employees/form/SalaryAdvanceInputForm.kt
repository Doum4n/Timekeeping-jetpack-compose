package com.example.timekeeping.ui.admin.employees.form

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Adjustment
import com.example.timekeeping.ui.admin.calender.CalendarState
import com.example.timekeeping.ui.admin.components.TopBarClassic
import com.example.timekeeping.utils.DateTimeMap
import com.example.timekeeping.utils.toPositive
import com.example.timekeeping.view_models.SalaryViewModel
import java.time.LocalDateTime

@Composable
fun SalaryAdvanceInputForm(
    groupId: String,
    employeeId: String,
    adjustmentId: String = "",
    onBackClick: () -> Unit,
    onSave: (Adjustment) -> Unit,
    state: CalendarState,
    salaryViewModel: SalaryViewModel = hiltViewModel()

) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    LaunchedEffect(adjustmentId) {
        if(adjustmentId == "")
            return@LaunchedEffect
        else {
            salaryViewModel.getAdjustSalary(
                adjustmentId,
                { adjustment ->
                    amount = (adjustment?.adjustmentAmount)?.toPositive().toString() ?: ""
                    note = adjustment?.note ?: ""
                })
        }
    }

    Scaffold(
        topBar = {
            TopBarClassic(
                title = "Tiền ứng",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            Text("Số tiền ứng")
            TextField(
                value = amount,
                onValueChange = { amount = it },
                placeholder = { Text("Nhập số tiền") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Hình ảnh")
            Image(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp)
            )

            Text("Ghi chú")
            TextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text("Ghi chú thêm...") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    onSave(
                        Adjustment(
                            groupId = groupId,
                            employeeId = employeeId,
                            adjustmentAmount = - amount.toInt(),
                            createdAt = DateTimeMap.from(LocalDateTime.now()),
                            adjustmentType = "Ứng lương",
                            note = note,
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Lưu")
            }
        }
    }
}