package com.example.timekeeping.ui.employees.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.ui.calender.CalendarScreen
import com.example.timekeeping.ui.calender.CalendarState
import com.example.timekeeping.ui.calender.rememberCalendarState
import com.example.timekeeping.ui.employees.components.SimpleDialogS
import com.example.timekeeping.ui.employees.components.WorkScheduleCalendar
import com.example.timekeeping.ui.employees.components.WorkStatus
import com.example.timekeeping.ui.groups.components.IconButtonWithLabel
import com.example.timekeeping.view_models.AttendanceViewModel
import com.example.timekeeping.view_models.EmployeeViewModel
import com.example.timekeeping.view_models.PaymentViewModel
import com.example.timekeeping.view_models.SalaryViewModel
import java.time.LocalDate

@Composable
fun EmployeeDetailGrid(
    employeeId: String = "",
    groupId: String = "",
    employeeViewModel: EmployeeViewModel = hiltViewModel(),
    paymentViewModel: PaymentViewModel = hiltViewModel(),
    onEmployeeInfoClick: () -> Unit = {},
    attendanceViewModel: AttendanceViewModel = hiltViewModel(),
    salaryViewModel: SalaryViewModel = hiltViewModel(),
    onBonusClick: () -> Unit = {},
    onMinusMoneyClick: () -> Unit = {},
    onAdvanceSalaryClick: () -> Unit = {},
    onPaymentClick: () -> Unit = {},
    onRequestAdvanceSalaryClick: () -> Unit = {},

    onBackToEmployeeList: () -> Unit = {},
    state: CalendarState
) {

    var showDialog = remember { mutableStateOf(false) }
    val attendances = remember { mutableStateMapOf<LocalDate, WorkStatus>() }
    val attendanceNumber = remember { mutableStateMapOf<WorkStatus, Int>() }

    val payments by paymentViewModel.payments.collectAsState()
    val totalPayment by remember(payments) {
        mutableStateOf(payments.sumOf { it.amount })
    }
    val totalWage = remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        salaryViewModel.calculateTotalWage(groupId, employeeId, state.visibleMonth.month.value, state.visibleMonth.year) {
            totalWage.value = it
        }
        paymentViewModel.getPayments(groupId, employeeId, state.visibleMonth.monthValue, state.visibleMonth.year)
    }

    LaunchedEffect(state.visibleMonth) {
        attendanceNumber.clear() // Reset dữ liệu cũ

        // Load dữ liệu cho "getTotalUnpaidSalaryByEmployee" phía dưới
        salaryViewModel.getSalaryInfoByMonth(groupId, employeeId, state.visibleMonth.month.value, state.visibleMonth.year)

        attendanceViewModel.getAttendanceByEmployeeId(employeeId, state.visibleMonth.monthValue, state.visibleMonth.year) { _attendances ->
            _attendances.forEach { attendance ->
                attendances[attendance.startTime.toLocalDate()] = when (attendance.attendanceType) {
                    "Đi làm" -> {
                        attendanceNumber[WorkStatus.WORK] = (attendanceNumber[WorkStatus.WORK] ?: 0) + 1
                        WorkStatus.WORK
                    }
                    "Chấm 1/2 công" -> {
                        attendanceNumber[WorkStatus.HALF_DAY] = (attendanceNumber[WorkStatus.HALF_DAY] ?: 0) + 1
                        WorkStatus.HALF_DAY
                    }
                    "Nghỉ có lương" -> {
                        attendanceNumber[WorkStatus.PAID_LEAVE] = (attendanceNumber[WorkStatus.PAID_LEAVE] ?: 0) + 1
                        WorkStatus.PAID_LEAVE
                    }
                    "Nghỉ không lương" -> {
                        attendanceNumber[WorkStatus.UNPAID_LEAVE] = (attendanceNumber[WorkStatus.UNPAID_LEAVE] ?: 0) + 1
                        WorkStatus.UNPAID_LEAVE
                    }
                    else -> WorkStatus.OTHER
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Tổng chưa thanh toán:")
        Text("${salaryViewModel.getTotalUnpaidSalaryByEmployee(totalWage.value, totalPayment)}")
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButtonWithLabel(
                    onClick = {onAdvanceSalaryClick()},
                    icon = Icons.Default.Warning,
                    label = "Ứng lương"
                )
                IconButtonWithLabel(
                    onClick = {onRequestAdvanceSalaryClick()},
                    icon = Icons.Default.Warning,
                    label = "Gửi yêu cầu ứng lương"
                )
                IconButtonWithLabel(
                    onClick = {onBonusClick()},
                    icon = Icons.Default.Warning,
                    label = "Thưởng/Phụ cấp"
                )
                IconButtonWithLabel(
                    onClick = {onMinusMoneyClick()},
                    icon = Icons.Default.Warning,
                    label = "Trừ tiền"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButtonWithLabel(
                    onClick = {onPaymentClick()},
                    icon = Icons.Default.Warning,
                    label = "Thanh toán"
                )
                IconButtonWithLabel(
                    onClick = {onEmployeeInfoClick()},
                    icon = Icons.Default.Info,
                    label = "Thông tin nhân viên"
                )
                IconButtonWithLabel(
                    onClick = {
                        showDialog.value = true},
                    icon = Icons.Default.Warning,
                    label = "Ngừng chấm"
                )

                if (showDialog.value){
                    SimpleDialogS(
                        title = "Thông báo",
                        question = "Bạn có chắc chắn muốn dừng chấm công cho nhân viên này không?",
                        onConfirm = {
                            employeeViewModel.deleteEmployee(groupId, employeeId)
                            showDialog.value = false
                            onBackToEmployeeList()
                        },
                        onDismiss = {
                            showDialog.value = false
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp) // hoặc 300-500dp tùy giao diện
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                WorkScheduleCalendar(
                    state = state,
                    workStatusMap = attendances.toMap()
                )
            }

            WorkStatus.entries
                .chunked(2) // Mỗi hàng 2 mục
                .forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        rowItems.forEach { status ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .background(
                                            color = when (status) {
                                                WorkStatus.WORK -> Color.Green
                                                WorkStatus.HALF_DAY -> Color.Yellow
                                                WorkStatus.PAID_LEAVE -> Color.Blue
                                                WorkStatus.UNPAID_LEAVE -> Color.Red
                                                WorkStatus.OTHER -> Color.Gray
                                            },
                                            shape = CircleShape
                                        )
                                        .padding(8.dp)
                                )
                                Text(
                                    text = "${status.label}: ${attendanceNumber[status] ?: 0}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        // Nếu dòng này chỉ có 1 item, thêm khoảng trống để giữ bố cục 2 cột cân đối
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
        }
    }
}

@Preview
@Composable
fun PreviewEmployeeDetailGrid(){
    EmployeeDetailGrid("", "", state = rememberCalendarState())
}