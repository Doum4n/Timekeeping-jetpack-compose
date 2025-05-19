package com.example.timekeeping.ui.admin.employees.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Beenhere
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.icons.filled.PersonRemoveAlt1
import androidx.compose.material.icons.filled.PublishedWithChanges
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.ui.admin.calender.CalendarState
import com.example.timekeeping.ui.admin.calender.rememberCalendarState
import com.example.timekeeping.ui.admin.employees.components.SimpleDialogS
import com.example.timekeeping.ui.admin.employees.components.WorkScheduleCalendar
import com.example.timekeeping.ui.admin.employees.components.WorkStatus
import com.example.timekeeping.ui.admin.groups.components.IconButtonWithLabel
import com.example.timekeeping.view_models.AttendanceViewModel
import com.example.timekeeping.view_models.EmployeeViewModel
import com.example.timekeeping.view_models.PaymentViewModel
import com.example.timekeeping.view_models.PayrollViewModel
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
    payrollViewModel: PayrollViewModel = hiltViewModel(),
    onBonusClick: () -> Unit = {},
    onMinusMoneyClick: () -> Unit = {},
    onAdvanceSalaryClick: () -> Unit = {},
    onPaymentClick: () -> Unit = {},
    onRequestAdvanceSalaryClick: () -> Unit = {},
    onAttendanceClick: () -> Unit = {},

    onBackToEmployeeList: () -> Unit = {},
    state: CalendarState
) {

    var showDialog = remember { mutableStateOf(false) }
    val attendances = remember { mutableStateMapOf<LocalDate, WorkStatus>() }
    val attendanceNumber = remember { mutableStateMapOf<WorkStatus, Int>() }

    var totalPayment = remember { mutableIntStateOf(0) }
    val totalWage = remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        salaryViewModel.calculateTotalWage(groupId, employeeId, state.visibleMonth.month.value, state.visibleMonth.year) {
            totalWage.value = it
        }
        paymentViewModel.getPayments(groupId, employeeId, state.visibleMonth.monthValue, state.visibleMonth.year)

        payrollViewModel.getTotalPaymentEmployeeByMonth(groupId, employeeId, state.visibleMonth.month.value, state.visibleMonth.year, {
            totalPayment.intValue = it
        }, {
            //Exception
        }
        )
//        payrollViewModel.getTotalWageEmployeeByMonth(groupId, employeeId, state.visibleMonth.month.value, state.visibleMonth.year) {
//            totalWage.value = it
//        }
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
        Text("${salaryViewModel.getTotalUnpaidSalaryByEmployee(totalWage.value, totalPayment.intValue)}")
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButtonWithLabel(
                    onClick = {onAttendanceClick()},
                    icon =  Icons.Default.Beenhere,
                    label = "Chấm công"
                )
                IconButtonWithLabel(
                    onClick = {onAdvanceSalaryClick()},
                    icon = Icons.Default.RequestQuote,
                    label = "Ứng lương"
                )
                IconButtonWithLabel(
                    onClick = {onRequestAdvanceSalaryClick()},
                    icon = Icons.Default.UploadFile,
                    label = "Gửi yêu cầu ứng lương"
                )
                IconButtonWithLabel(
                    onClick = {onBonusClick()},
                    icon = Icons.Default.PersonAddAlt,
                    label = "Thưởng/Phụ cấp"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButtonWithLabel(
                    onClick = {onMinusMoneyClick()},
                    icon = Icons.Default.PersonRemoveAlt1,
                    label = "Trừ tiền"
                )
                IconButtonWithLabel(
                    onClick = {onPaymentClick()},
                    icon = Icons.Default.Paid,
                    label = "Thanh toán"
                )
                IconButtonWithLabel(
                    onClick = {onEmployeeInfoClick()},
                    icon = Icons.Default.AssignmentInd,
                    label = "Thông tin nhân viên"
                )
                IconButtonWithLabel(
                    onClick = {
                        showDialog.value = true},
                    icon = Icons.Default.Block,
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

@Preview(showBackground = true, name = "Employee Detail Grid Preview")
@Composable
fun PreviewEmployeeDetailGridSimple() {
    val showDialog = remember { mutableStateOf(false) }
    val attendanceNumber = remember {
        mutableStateMapOf(
            WorkStatus.WORK to 5,
            WorkStatus.HALF_DAY to 2,
            WorkStatus.PAID_LEAVE to 1,
            WorkStatus.UNPAID_LEAVE to 0,
            WorkStatus.OTHER to 0
        )
    }

    val mockCalendarState = rememberCalendarState()
    val attendances = remember {
        mutableStateMapOf<LocalDate, WorkStatus>().apply {
            put(LocalDate.now(), WorkStatus.WORK)
            put(LocalDate.now().plusDays(1), WorkStatus.HALF_DAY)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Tổng chưa thanh toán:")
        Text("1.000.000 VNĐ")

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButtonWithLabel(
                    onClick = {},
                    icon = Icons.Default.AssignmentInd,
                    label = "Chấm công"
                )
                IconButtonWithLabel(
                    onClick = {},
                    icon = Icons.Default.RequestQuote,
                    label = "Ứng lương"
                )
                IconButtonWithLabel(
                    onClick = {},
                    icon = Icons.Default.UploadFile,
                    label = "Gửi yêu cầu ứng lương"
                )
                IconButtonWithLabel(
                    onClick = {},
                    icon = Icons.Default.PersonAddAlt,
                    label = "Thưởng/Phụ cấp"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButtonWithLabel(
                    onClick = {},
                    icon = Icons.Default.PersonRemoveAlt1,
                    label = "Trừ tiền"
                )
                IconButtonWithLabel(
                    onClick = {},
                    icon = Icons.Default.Paid,
                    label = "Thanh toán"
                )
                IconButtonWithLabel(
                    onClick = {},
                    icon = Icons.Default.AssignmentInd,
                    label = "Thông tin nhân viên"
                )
                IconButtonWithLabel(
                    onClick = { showDialog.value = true },
                    icon = Icons.Default.Block,
                    label = "Ngừng chấm"
                )
            }

            if (showDialog.value) {
                SimpleDialogS(
                    title = "Thông báo",
                    question = "Bạn có chắc chắn muốn dừng chấm công cho nhân viên này không?",
                    onConfirm = { showDialog.value = false },
                    onDismiss = { showDialog.value = false }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                WorkScheduleCalendar(
                    state = mockCalendarState,
                    workStatusMap = attendances.toMap()
                )
            }

            WorkStatus.entries.chunked(2).forEach { rowItems ->
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
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
