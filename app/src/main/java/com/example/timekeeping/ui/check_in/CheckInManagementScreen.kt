package com.example.timekeeping.ui.check_in

import android.util.Log
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
import com.example.timekeeping.ui.assignment.components.ShiftSection
import com.example.timekeeping.ui.calender.CalendarState
import com.example.timekeeping.view_models.ShiftViewModel
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Attendance
import com.example.timekeeping.ui.components.TopBarWithDoneAction
import com.example.timekeeping.utils.convertLocalDateToDate
import com.example.timekeeping.view_models.AttendanceViewModel

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

@Composable
fun CheckInManagementScreen(
    state: CalendarState,
    shiftViewModel: ShiftViewModel = hiltViewModel(),
    attendanceViewModel: AttendanceViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    groupId: String
){
    val checkInStates = remember { mutableStateMapOf<String, SnapshotStateMap<AttendanceType, Boolean>>() }
    val shifts = shiftViewModel.shifts.value
    var selectedShift = remember { mutableStateOf<String?>("") }

    val attendances = remember { mutableStateOf<List<Attendance>>(emptyList()) }

    var shareCheckInStates = remember { mutableStateMapOf<AttendanceType, Boolean>() }

    val isShareCheckIn by remember {
        derivedStateOf {
            shareCheckInStates.values.any { it }
        }
    }

    shareCheckInStates = remember {
        mutableStateMapOf(
            AttendanceType.FullDay to false,
            AttendanceType.HalfDay to false,
            AttendanceType.PaidLeave to false,
            AttendanceType.UnpaidLeave to false
        )
    }

    LaunchedEffect(shifts) {
        if (shifts.isNotEmpty()) {
            //shiftViewModel.loadEmployees(shifts.first().id)

            attendanceViewModel.getAttendanceByShiftId(shifts.first().id) { attendancesList ->
                attendances.value = attendancesList
            }
        }
    }

    LaunchedEffect(shareCheckInStates.toMap()) {
        checkInStates.forEach { (employeeId, individualStates) ->
            shareCheckInStates.forEach { (type, checked) ->
                individualStates[type] = checked
            }
        }
    }

    Scaffold(
        topBar = {
            TopBarWithDoneAction(
                title = "Chấm công",
                onDoneClick = {
                    checkInStates.forEach { (employeeId, attendanceStates) ->
                        attendanceStates.forEach { (type, checked) ->
                            if (checked) {
                                val alreadyCheckedIn = attendances.value.firstOrNull {
                                    it.employeeId.id == employeeId &&
                                            //it.attendanceType == type.label &&
                                            it.shiftId == selectedShift.value // Kiểm tra chấm công của ca hiện tại
                                    //it.dayCheckIn == state.visibleDate.convertLocalDateToDate() // Kiểm tra ngày chấm công
                                }

                                Log.d("CheckInManagementScreen", "Attendance ID: $alreadyCheckedIn.id")

                                if (alreadyCheckedIn != null) {
                                    val attendanceId = alreadyCheckedIn.id

                                    val updatedAttendance = alreadyCheckedIn.copy(
                                        attendanceType = type.label,
                                        dayCheckIn = state.visibleDate.convertLocalDateToDate()
                                    )

                                    Log.d("CheckInManagementScreen", "Attendance ID: $attendanceId")
                                    // Gọi update nếu có logic cập nhật
                                    attendanceViewModel.updateAttendance(attendanceId, updatedAttendance)
                                } else {
                                    // Gọi tạo mới
                                    attendanceViewModel.CheckIn(employeeId, selectedShift.value!!, type.label)
                                }
                            }
                        }
                    }
                },
                onBackClick = onBackClick
            )
        }
    ) {paddingValues ->

        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            item {
                Text("Ngày chấm công")
                Header(state)
            }

            item {
                ShiftSection(
                    shiftViewModel = shiftViewModel,
                    onShiftSelected = {
                        selectedShift.value = it
                        shiftViewModel.loadEmployees(it)
                    }
                )
            }

            item {
                Text("Chấm cả nhóm")
                Spacer(modifier = Modifier.height(8.dp))

                AttendanceOptions(
                    attendanceStates = shareCheckInStates,
                    onCheck = { type, checked ->
                        // Cập nhật trạng thái chung cho tất cả nhân viên khi checkbox thay đổi
                        shareCheckInStates[type] = checked
                    }
                )

                HorizontalDivider()
            }

            items(shiftViewModel.employees.value) { employee ->
                // Tạo và lưu trạng thái chấm công riêng biệt cho từng nhân viên
                val individualState = remember {
                    mutableStateMapOf<AttendanceType, Boolean>().apply {
                        attendances.value.forEach {
                            if (it.employeeId.id == employee.id)
                                this[AttendanceType.fromLabel(it.attendanceType)!!] = true
                        }
                    }
                }

                // Lưu trạng thái của từng nhân viên vào checkInStates
                checkInStates[employee.id] = individualState

                EmployeeCheckInSection(
                    employeeName = employee.fullName,
                    shareCheckInStates = if(isShareCheckIn) shareCheckInStates
                    else individualState,
                    checkInStates = checkInStates,
                    employeeId = employee.id
                )
            }
        }
    }
}

@Composable
fun EmployeeCheckInSection(
    employeeName: String,
    shareCheckInStates: SnapshotStateMap<AttendanceType, Boolean>,
    checkInStates: SnapshotStateMap<String, SnapshotStateMap<AttendanceType, Boolean>> = mutableStateMapOf(),
    employeeId: String,
){
    Column {
        Text(employeeName)
        Spacer(modifier = Modifier.height(8.dp))
        AttendanceOptions(
            attendanceStates = shareCheckInStates,
            onCheck = { type, checked ->
                // Cập nhật trạng thái checkbox cho từng nhân viên khi checkbox thay đổi
                shareCheckInStates[type] = checked
                checkInStates[employeeId]?.put(type, checked)
                Log.d("EmployeeCheckInSection", checkInStates[employeeId].toString())
            }
        )
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
    onCheck: (AttendanceType, Boolean) -> Unit
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
