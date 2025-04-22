package com.example.timekeeping.ui.employees.form

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.timekeeping.models.Adjustment
import com.example.timekeeping.models.Salary
import com.example.timekeeping.ui.assignment.components.CalendarHeader
import com.example.timekeeping.ui.calender.CalendarState
import com.example.timekeeping.ui.components.TopBarClassic
import com.example.timekeeping.utils.convertLocalDateToDate

@Composable
fun SalaryAdvanceInputForm(
    groupId: String,
    employeeId: String,
    onBackClick: () -> Unit,
    onSave: (Adjustment) -> Unit,
    state: CalendarState
) {
    var amount by remember { mutableStateOf(TextFieldValue()) }
    var note by remember { mutableStateOf(TextFieldValue()) }

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
                modifier = Modifier.fillMaxWidth()
            )

            Text("Ngày ứng lương")
            CalendarHeader(
                state = state,
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
                            adjustmentAmount = - amount.text.toInt(),
                            createdAt = state.selectedDate.convertLocalDateToDate(),
                            adjustmentType = "Ứng lương",
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

// Helper function nếu muốn xử lý khi nhập không đúng số
fun String.toIntOrNullSafe(): Int? = this.filter { it.isDigit() }.toIntOrNull()
fun Int?.orZero() = this ?: 0