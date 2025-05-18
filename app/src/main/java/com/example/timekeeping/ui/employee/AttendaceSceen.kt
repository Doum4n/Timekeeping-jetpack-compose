package com.example.timekeeping.ui.employee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Attendance
import com.example.timekeeping.models.Shift
import com.example.timekeeping.ui.admin.components.TopBarClassic
import com.example.timekeeping.utils.DateTimeMap
import com.example.timekeeping.view_models.AttendanceViewModel
import com.example.timekeeping.view_models.ShiftViewModel
import java.time.LocalDate

@Composable
fun AttendaceSceen(
    employeeId: String,
    groupId: String,
    onBackClick: () -> Unit,
    onClick: (Shift) -> Unit,
    onCheckOut: (Attendance) -> Unit,
    shiftViewModel: ShiftViewModel = hiltViewModel(),
    attendanceViewModel: AttendanceViewModel = hiltViewModel()
) {
    var shifts by remember { mutableStateOf<List<Shift>>(emptyList()) }
    var attendances by remember { mutableStateOf<Map<String, Attendance>>(emptyMap()) }

    LaunchedEffect(employeeId) {
        shiftViewModel.getOnGoingShift { loadedShifts ->
            shifts = loadedShifts

            // Sau khi có shift, mới gọi getAttendance
            attendanceViewModel.getAttendanceByEmployeeIdAndDate(
                employeeId = employeeId,
                date = LocalDate.now()
            ) { attendanceList ->
                // Chỉ lấy attendance tương ứng với shift đang có
                attendances = attendanceList
                    .filter { att -> loadedShifts.any { it.id == att.shiftId } }
                    .associate { att -> att.shiftId to att }
            }
        }
    }


    Scaffold(
        topBar = {
            TopBarClassic(
                title = "Attendance",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Ca đang diễn ra",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(shifts){
                ShiftItem(
                    shift = it,
                    attendance = attendances[it.id],
                    onClick = onClick,
                    onCheckOut = onCheckOut
                )
            }
        }
    }
}

@Composable
fun ShiftItem(
    shift: Shift,
    attendance: Attendance?,
    onClick: (Shift) -> Unit,
    onCheckOut: (Attendance) -> Unit
) {
    Card (
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        ShiftField(label = "Tên ca:", value = shift.shiftName)
        ShiftField(label = "Thời gian:", value = "${shift.startTime} - ${shift.endTime}")
        ShiftField(label = "Trạng thái:", value = ("${attendance?.attendanceType} : ${attendance?.startTime?.toTime()} - ${attendance?.endTime?.toTime()}"))

        Button(
            onClick = {
                if (attendance?.attendanceType == "Đi làm") {
                    onCheckOut(attendance)
                } else {
                    onClick(shift)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (attendance?.attendanceType == "Đi làm") {
                Text("Chạm để kết thúc ca")
            } else {
                Text("Chạm để vào ca")
            }
        }
    }
}

@Composable
fun ShiftField(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxSize().padding(6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Text(text = value)
    }
}