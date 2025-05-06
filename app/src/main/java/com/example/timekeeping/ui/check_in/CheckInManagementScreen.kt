package com.example.timekeeping.ui.check_in

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import com.example.timekeeping.ui.assignment.components.ShiftSection
import com.example.timekeeping.ui.calender.CalendarState
import com.example.timekeeping.view_models.ShiftViewModel
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Attendance
import com.example.timekeeping.ui.components.TopBarWithDoneAction
import com.example.timekeeping.utils.DateTimeMap
import com.example.timekeeping.utils.convertLocalDateToDate
import com.example.timekeeping.utils.convertToReference
import com.example.timekeeping.view_models.AttendanceViewModel
import java.time.LocalDate
import java.time.LocalDateTime

enum class AttendanceType(val label: String) {
    FullDay("Đi làm"),
    HalfDay("Chấm 1/2 công"),
    PaidLeave("Nghỉ có lương"),
    UnpaidLeave("Nghỉ không lương");

    companion object {
        fun fromLabel(label: String): AttendanceType? {
            return entries.find { it.label == label }
        }
    }
}

data class CheckInState(
    val employeeId: String,
    val employeeName: String,
    // Lưu trạng thái của các loại chấm công
    val attendanceStates: SnapshotStateMap<AttendanceType, Boolean> = mutableStateMapOf(),
    var reason: MutableState<String> = mutableStateOf("")
)

data class CheckInUiState(
    var selectedDate: LocalDate,
    var selectedShiftId: String? = null,
    val sharedAttendanceStates: SnapshotStateMap<AttendanceType, Boolean> = mutableStateMapOf(
        AttendanceType.FullDay to false,
        AttendanceType.HalfDay to false,
        AttendanceType.PaidLeave to false,
        AttendanceType.UnpaidLeave to false
    ),
    val attendances: List<Attendance> = emptyList(),
    var checkInStates: SnapshotStateMap<String, CheckInState> = mutableStateMapOf()
)

@Composable
fun CheckInManagementScreen(
    state: CalendarState,
    shiftViewModel: ShiftViewModel = hiltViewModel(),
    attendanceViewModel: AttendanceViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    groupId: String
){

    val context = LocalContext.current

    val uiState = remember {
        mutableStateOf(
            CheckInUiState(
                selectedDate = state.visibleDate
            )
        )
    }

    val isSharedCheckIn by remember {
        derivedStateOf {
            uiState.value.sharedAttendanceStates.any { it.value }
        }
    }

    LaunchedEffect(uiState.value.sharedAttendanceStates.toMap()) {
        if (isSharedCheckIn) {
            uiState.value.checkInStates.forEach { (_, checkIn) ->
                uiState.value.sharedAttendanceStates.forEach { (type, checked) ->
                    checkIn.attendanceStates[type] = checked
                }
            }
        }
    }

    val visibleDate = state.visibleDate

    LaunchedEffect(shiftViewModel.shifts.value, visibleDate) {
        val shifts = shiftViewModel.shifts.value
        if (shifts.isNotEmpty()) {

            // reset
            uiState.value.checkInStates = mutableStateMapOf()

            val shiftId = shifts.first().id
            uiState.value.selectedShiftId = shiftId

            attendanceViewModel.getAttendanceByShiftId(shiftId, state.visibleDate.convertLocalDateToDate()) { attendances ->
                shiftViewModel.loadEmployees(shiftId, state.visibleDate.dayOfMonth)

                attendances.forEach { attendance ->
                    val empId = attendance.employeeId.id
                    val employeeName = shiftViewModel.employees.value
                        .find { it.id == empId }?.name?.fullName ?: "Unknown"

                    val state = uiState.value.checkInStates.getOrPut(empId) {
                        CheckInState(
                            employeeId = empId,
                            employeeName = employeeName,
                            reason = mutableStateOf(attendance.note)
                        )
                    }

                    AttendanceType.fromLabel(attendance.attendanceType)?.let {
                        state.attendanceStates[it] = true
                    }
                }

                uiState.value = uiState.value.copy(attendances = attendances)
            }
        }
    }


    Scaffold(
        topBar = {
            TopBarWithDoneAction(
                title = "Chấm công",
                onDoneClick = {
                    uiState.value.checkInStates.forEach { (employeeId, checkIn) ->
                        checkIn.attendanceStates.forEach { (type, checked) ->
                            if (checked) {

                                if(uiState.value.selectedShiftId == null) {
                                    Toast.makeText(
                                        context,
                                        "Vui lòng chọn ca làm việc",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    return@forEach
                                }
                                val alreadyAttended = uiState.value.attendances.firstOrNull {
                                    it.employeeId.id == employeeId &&
                                    it.shiftId == uiState.value.selectedShiftId &&
                                    it.startTime.equals(DateTimeMap.from(state.visibleDate))
                                }

                                Log.d("CheckInManagementScreen", "alreadyAttended: $alreadyAttended")

                                if (alreadyAttended != null)
                                    attendanceViewModel.updateAttendance(
                                        alreadyAttended.id,
                                        Attendance(
                                            employeeId = alreadyAttended.employeeId,
                                            shiftId = alreadyAttended.shiftId,
                                            attendanceType = type.label,
                                            startTime = DateTimeMap.from(LocalDateTime.now()),
                                            note = checkIn.reason.value
                                        )
                                    )
                                else
                                    attendanceViewModel.CheckIn(
                                        Attendance(
                                            employeeId = employeeId.convertToReference("employees"),
                                            shiftId = uiState.value.selectedShiftId!!,
                                            attendanceType = type.label,
                                            startTime = DateTimeMap.from(LocalDateTime.now()),
                                            note = checkIn.reason.value
                                        )
                                    )
                            }
                        }
                    }
                },
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            item {
                Text("Ngày chấm công")
                Header(state)
            }

            item {
                ShiftSection(
                    shiftViewModel = shiftViewModel,
                    onShiftSelected = {
                        uiState.value.selectedShiftId = it
                        shiftViewModel.loadEmployees(it, state.visibleDate.dayOfMonth)
                    },
                    selectedShiftId = uiState.value.selectedShiftId
                )
            }

            item {
                Text("Chấm cả nhóm")
                AttendanceOptions(
                    attendanceStates = uiState.value.sharedAttendanceStates,
                    onCheck = { type, checked ->
                        uiState.value.sharedAttendanceStates[type] = checked
                    }
                )
                HorizontalDivider()
            }

            items(shiftViewModel.employees.value) { employee ->
                val checkIn = uiState.value.checkInStates.getOrPut(employee.id) {
                    CheckInState(employeeId = employee.id, employeeName = employee.name.fullName)
                }

                EmployeeCheckInSection(
                    employeeName = employee.name.fullName,
                    shareCheckInStates = if (isSharedCheckIn) uiState.value.sharedAttendanceStates else checkIn.attendanceStates,
                    reason = checkIn.reason.value,
                    onReasonChange = { reason -> checkIn.reason.value = reason },
                    isLeave = checkIn.attendanceStates[AttendanceType.PaidLeave] == true ||
                            checkIn.attendanceStates[AttendanceType.UnpaidLeave] == true
                )
            }
        }
    }
}

@Composable
fun EmployeeCheckInSection(
    employeeName: String,
    shareCheckInStates: SnapshotStateMap<AttendanceType, Boolean>,
    reason: String,
    onReasonChange: (String) -> Unit = {},
    isLeave: Boolean
) {

    Column {
        Text(employeeName)
        AttendanceOptions(
            attendanceStates = shareCheckInStates,
            onCheck = { type, checked ->
                shareCheckInStates[type] = checked
            }
        )
        if (isLeave) {
            TextField(
                value = reason,
                onValueChange = {
                    onReasonChange(it)
                },
                label = { Text("Lý do nghỉ phép") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun Header(
    state: CalendarState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { state.prevMonth() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(state.visibleDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        }

        IconButton(onClick = { state.nextMonth() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
        }
    }
}

@Composable
fun AttendanceOptions(
    attendanceStates: Map<AttendanceType, Boolean>,
    onCheck: (AttendanceType, Boolean) -> Unit,
    isLeave: Boolean = false
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            AttendanceOption(
                type = AttendanceType.FullDay,
                checked = attendanceStates[AttendanceType.FullDay] ?: false,
                onCheck = onCheck,
                modifier = Modifier.weight(1f)
            )
            AttendanceOption(
                type = AttendanceType.HalfDay,
                checked = attendanceStates[AttendanceType.HalfDay] ?: false,
                onCheck = onCheck,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            AttendanceOption(
                type = AttendanceType.PaidLeave,
                checked = attendanceStates[AttendanceType.PaidLeave] ?: false,
                onCheck = onCheck,
                modifier = Modifier.weight(1f)
            )
            AttendanceOption(
                type = AttendanceType.UnpaidLeave,
                checked = attendanceStates[AttendanceType.UnpaidLeave] ?: false,
                onCheck = onCheck,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AttendanceOption(
    type: AttendanceType,
    checked: Boolean,
    onCheck: (AttendanceType, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onCheck(type, it) }
        )
        Text(text = type.label)
    }
}
