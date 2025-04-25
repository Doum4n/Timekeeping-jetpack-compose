package com.example.timekeeping.ui.employees.form

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Payment
import com.example.timekeeping.ui.calender.CalendarState
import com.example.timekeeping.ui.components.TopBarClassic
import com.example.timekeeping.utils.DateTimeMap
import com.example.timekeeping.view_models.EmployeeViewModel
import com.example.timekeeping.view_models.PaymentViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun PaymentInputForm(
    employeeId: String,
    groupId: String,
    state: CalendarState = CalendarState(),
    onPaymentClick: (Payment) -> Unit = {},
    paymentViewModel: PaymentViewModel = hiltViewModel(),
    employeeViewModel: EmployeeViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

    var name by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        employeeViewModel.getName(employeeId, {
            name = it
        })
    }

    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBarClassic(
                title = "Thanh toán",
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tên nhân viên
            Text(
                text = "Tên nhân viên",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            TextField(
                value = name,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )

            // Số tiền
            Text(
                text = "Số tiền",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            TextField(
                value = amount,
                onValueChange = {amount = it},
                modifier = Modifier.fillMaxWidth()
            )

            // Ngày thanh toán
            Text(
                text = "Ngày thanh toán",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            Header(state)

            // Hình ảnh
            Text(
                text = "Hình ảnh",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            Image(
                imageVector = Icons.Default.Info,
                contentDescription = "Hình ảnh",
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterHorizontally)
            )

            // Ghi chú
            Text(
                text = "Ghi chú",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            TextField(
                value = note,
                onValueChange = {note = it},
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { onPaymentClick(
                    Payment(
                        amount = amount.toInt(),
                        createAt = DateTimeMap.from(LocalDateTime.now()),
                        note = note
                    )
                ) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
            ) {
                Text("Thanh toán")
            }
        }
    }
}

@Composable
fun Header(
    state: CalendarState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { state.prevMonth() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
        }

        Text(
            text = state.visibleDate.format(DateTimeFormatter.ofPattern("dd")),
            style = MaterialTheme.typography.titleMedium
        )

        IconButton(onClick = { state.nextMonth() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPayMentInputForm() {
    val calendarState = remember { CalendarState() }

    PaymentInputForm(
        employeeId = "Nguyễn Văn A",
        groupId = "group001",
        state = calendarState,
        onBack = {}
    )
}
