package com.example.timekeeping.ui.assignment

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.timekeeping.models.Assignment
import com.example.timekeeping.models.Employee
import com.example.timekeeping.ui.assignment.components.ScheduleCalenda
import com.example.timekeeping.ui.assignment.utils.getDaysOfMonthExpanded
import com.example.timekeeping.ui.assignment.utils.getDaysOfMonthShrunk
import com.example.timekeeping.ui.assignment.utils.getShortNameFor
import com.example.timekeeping.ui.assignment.utils.getWeekdays
import com.example.timekeeping.ui.calender.CalendarState
import com.example.timekeeping.view_models.AssignmentViewModel
import com.example.timekeeping.view_models.EmployeeViewModel
import com.example.timekeeping.view_models.ShiftViewModel
import com.example.timekeeping.view_models.TeamViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class CalendarDay(val day: String, var isSelected: Boolean, val isAssigned: Boolean = false)

data class AssignmentState(
    val teamId: String,
    val assignment: Assignment,
    var isModified: Boolean = false,
    var isNew: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentScreen(
    state: CalendarState,
    onDone: (List<Assignment>) -> Unit,
    onBackClick: () -> Unit,
    onChooseTeamClick: () -> Unit,
    shiftViewModel: ShiftViewModel,
    teamViewModel: TeamViewModel,
    employeeViewModel: EmployeeViewModel,
    viewModel: AssignmentViewModel
) {
    var employees by remember { mutableStateOf(listOf<Employee>()) }
    var assignments by remember { mutableStateOf(listOf<AssignmentState>()) }
    var calendarDays by remember { mutableStateOf(listOf<CalendarDay>()) }
    var assignmentDates by remember { mutableStateOf(listOf<DayOfWeek>()) }
    var assignmentsId by remember { mutableStateOf(listOf("")) }
    var selectedShiftId by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.getAssignments { assignments ->
            if (assignments.isNotEmpty()) {
                val currentYearMonth = YearMonth.now()
                assignmentDates = assignments
                    .flatMap { it.dates }
                    .map { day -> currentYearMonth.atDay(day).dayOfWeek }
                    .distinct()

                assignmentsId = assignments.map { it.id }
            } else {
                assignmentDates = emptyList()
                assignmentsId = listOf()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Danh sách công việc") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
//                        if (calendarDays.count { it.isAssigned && !it.isSelected } < calendarDays.size) {
//                            if(assignmenstId.isEmpty()){
//                                viewModel.addAssignment(
//                                    Assignment(
//                                        shiftId = shiftViewModel.shifts.value.first().id,
//                                        employeeId = "userId",
//                                        teamId = "teamId",
//                                        month = state.visibleMonth.month,
//                                        year = java.time.Year.of(state.visibleMonth.year).value,
//                                        dates = calendarDays.filter { it.isSelected || it.isAssigned }
//                                            .map { it.day.toInt() }
//                                    )
//                                )
//                            }else{
//                                assignmentsId.forEach { assignmentId ->
//                                    viewModel.updateAssignment(
//                                        assignmentId = assignmentId,
//                                        Assignment(
//                                            shiftId = shiftViewModel.shifts.value.first().id,
//                                            employeeId = "userId",
//                                            teamId = "teamId",
//                                            month = state.visibleMonth.month,
//                                            year = java.time.Year.of(state.visibleMonth.year).value,
//                                            dates = calendarDays.filter { it.isSelected || it.isAssigned }
//                                                .map { it.day.toInt() }
//                                        )
//                                    )
//                                }
//                            }
//                        }
                    }) {
                        Icon(Icons.Default.Done, "Done")
                    }
                }
            )
        }
    ) { paddingValues ->
        val expanded = remember { mutableStateOf(false) }
        val selectedWeekdays = remember { mutableStateListOf<DayOfWeek>() }

        Column(modifier = Modifier.padding(paddingValues)) {

            Text(text = "Chọn ca", modifier = Modifier.padding(16.dp))

            Row (
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                shiftViewModel.shifts.value.forEach { shift ->
                    ShiftItem(onShiftClick = { selectedShiftId = shift.id }, shift.id, shift.shiftName, shift.startTime, shift.endTime)
                }
            }

            // Calendar Header + Expand Button
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    CalendarHeader(state)
                }
                Button(onClick = { expanded.value = !expanded.value }) {
                    Text(text = if (expanded.value) "Thu gọn" else "Mở rộng")
                }
            }

            // Weekday Selector
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                getWeekdays().forEach { dayOfWeek ->
                    val isChecked = assignmentDates.contains(dayOfWeek)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = getShortNameFor(dayOfWeek))
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = {
                                if (isChecked) selectedWeekdays.remove(dayOfWeek)
                                else selectedWeekdays.add(dayOfWeek)
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // TODO
            // Calendar Grid
             calendarDays =
                if (expanded.value) getDaysOfMonthExpanded(selectedWeekdays, assignmentDates)
                else getDaysOfMonthShrunk(selectedWeekdays)


            ScheduleCalenda(calendarDays = calendarDays)

            Row (
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Chọn tổ", modifier = Modifier.padding(16.dp).weight(1f))
                Button(onClick = { onChooseTeamClick() }) {
                    Text(text = "Quản lý tổ")
                }
            }

            Row (
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                teamViewModel.teams.value.forEach { team ->
                    TeamItem(team.name)
                }
            }

            employeeViewModel.loadEmployeeByShiftId(
                selectedShiftId,
                onSuccess = {
                    employees += it
                    Log.d("AssignmentScreen", "Loaded employees: $employees")
                }
            )

            LazyColumn {
                items(employees) { employee ->
                    EmployeeItem(employee)
                }
            }
        }
    }
}

@Composable
fun CalendarHeader(state: CalendarState) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { state.prevMonth() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
        }

        Text(
            text = state.visibleMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            style = MaterialTheme.typography.titleMedium
        )

        IconButton(onClick = { state.nextMonth() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
        }
    }
}

//fun checkDayOfWeek(selectedWeekdays: List<DayOfWeek>, )

@Composable
fun ShiftItem(
    onShiftClick: (String) -> Unit,
    id: String,
    shiftName: String,
    startTime: String,
    endTime: String,
){
    Card(
        onClick = { onShiftClick(id) },
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = shiftName)
            Text(text = "$startTime - $endTime")
        }
    }
}

@Composable
fun TeamItem(
    teamName: String
){
    Card(
        modifier = Modifier.padding(16.dp)
    ) {
        Box(
            modifier = Modifier.padding(16.dp)
        ){
            Text(text = teamName)
        }
    }
}

@Composable
fun EmployeeItem(
    employee: Employee
) {
    Card(
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = employee.fullName)
        }
    }
}