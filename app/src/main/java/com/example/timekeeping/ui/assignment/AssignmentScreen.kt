package com.example.timekeeping.ui.assignment

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Assignment
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Team
import com.example.timekeeping.ui.assignment.components.EmployeeItem
import com.example.timekeeping.ui.assignment.components.ScheduleCalenda
import com.example.timekeeping.ui.assignment.components.ShiftSection
import com.example.timekeeping.ui.assignment.components.TeamSection
import com.example.timekeeping.ui.assignment.utils.Calendar
import com.example.timekeeping.ui.assignment.utils.CalendarDay
import com.example.timekeeping.ui.assignment.utils.getDaysOfMonthExpanded
import com.example.timekeeping.ui.assignment.utils.getDaysOfMonthShrunk
import com.example.timekeeping.ui.assignment.utils.isEmployeeCalendarModified
import com.example.timekeeping.ui.calender.CalendarState
import com.example.timekeeping.utils.convertToReference
import com.example.timekeeping.view_models.AssignmentViewModel
import com.example.timekeeping.view_models.ShiftViewModel
import com.example.timekeeping.view_models.TeamViewModel
import java.time.DayOfWeek
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentScreen(
    state: CalendarState,
    //onDone: (List<Assignment>) -> Unit,
    onBackClick: () -> Unit,
    onChooseTeamClick: () -> Unit,
    shiftViewModel: ShiftViewModel = hiltViewModel(),
    teamViewModel: TeamViewModel = hiltViewModel(),
    viewModel: AssignmentViewModel = hiltViewModel()
) {
    // State chính
    var isSharedCalendar by remember { mutableStateOf(true) } // Cờ xác định lịch dùng chung hay cá nhân
    val selectedDays = remember { mutableStateListOf<String>() } // Lưu các ngày đã chọn
    var sharedCalendarDays by remember { mutableStateOf(listOf<CalendarDay>()) } // Lịch dùng chung cho các nhân viên

    var calendarByEmployee = remember { mutableStateMapOf<String, SnapshotStateList<CalendarDay>>() } // Lịch của từng nhân viên
    val weekdayByEmployee = remember { mutableStateMapOf<String, SnapshotStateList<DayOfWeek>>() } // Các ngày trong tuần của từng nhân viên
    val assignmentStates = remember { mutableStateMapOf<String, SnapshotStateList<String>>() } // Trạng thái phân công công việc của từng nhân viên

    val selectedWeekdays = remember { mutableStateListOf<DayOfWeek>() } // Các ngày trong tuần được chọn
    var assignmentDates by remember { mutableStateOf(listOf<Int>()) } // Ngày phân công công việc

    var expanded by remember { mutableStateOf(true) } // Cờ xác định trạng thái mở rộng của lịch

    var selectedShift by remember { mutableStateOf("") } // Shift đã chọn

    // Lịch khởi tạo của nhân viên, sử dụng List để tránh việc mutate
    // Danh sách này dùng để kiểm tra xem lịch của nhân viên đã được sửa đổi hay chưa
    val initialCalendarByEmployee = remember { mutableStateMapOf<String, List<CalendarDay>>() }

    val employees = teamViewModel.employees.collectAsState().value // Danh sách nhân viên của nhóm

    // Map giữa employeeId và assignmentId
    // Dùng để kiểm tra xem nhân viên đã có phân công công việc chưa
    val employeeId_assignmentId = remember { mutableStateMapOf<String, String>() }

    val teams = teamViewModel.teams.value // Danh sách các nhóm

    LaunchedEffect(teams) {
        if(teams.isNotEmpty())
        {
            teamViewModel.getEmployees(teams.first().id)
        }
    }

    // Load assignment dates
    LaunchedEffect(employees, state.visibleMonth) {
        teamViewModel.employees.value.forEach {
            viewModel.getAssignments(it.id) { assignments ->
                val currentMonth = YearMonth.now()
                assignmentStates.put(
                    it.id,
                    assignments
                        .flatMap { it.dates }
                        .map { date ->
                            currentMonth.atDay(date).dayOfMonth.toString()
                        }
                        .toMutableStateList()
                )
                assignmentDates = assignmentStates[it.id]?.map { it.toInt() } ?: listOf()

                initialCalendarByEmployee[it.id] = getDaysOfMonthExpanded(selectedDays, selectedWeekdays, assignmentStates[it.id]?.map { it.toInt() } ?: listOf())

                employeeId_assignmentId[it.id] = assignments.firstOrNull()?.id ?: ""
            }
        }
    }

    // Update shared calendar
    fun updateSharedCalendar(expanded: Boolean): List<CalendarDay> {
        return if (expanded) {
            getDaysOfMonthExpanded(selectedDays, selectedWeekdays, assignmentDates)
        } else {
            getDaysOfMonthShrunk(selectedDays, selectedWeekdays, assignmentDates)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Phân công công việc") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        calendarByEmployee.forEach({ assignment ->
                            val employeeAssignmentId = employeeId_assignmentId[assignment.key]

                            if (employeeAssignmentId != null) {
                                if (employeeAssignmentId.isNotBlank() && isEmployeeCalendarModified(assignment.key, initialCalendarByEmployee, calendarByEmployee)) {
                                    // Nếu bản ghi tồn tại và đã được sửa đổi, thực hiện cập nhật
                                    viewModel.updateAssignment(
                                        employeeAssignmentId,
                                        Assignment(
                                            employeeId = assignment.key.convertToReference("employees"),
                                            shiftId = selectedShift.convertToReference("shifts"),
                                            dates = assignment.value.filter { it.day.isNotBlank() && (it.isSelected || it.isAssigned) }
                                                .map { it.day.toInt() }
                                                .toList(),
                                            month = state.visibleMonth.monthValue,
                                            year = state.visibleMonth.year
                                        )
                                    )
                                } else if(employeeAssignmentId.isBlank()) {
                                    // Nếu không có bản ghi hoặc không sửa đổi, thực hiện thêm mới
                                    viewModel.addAssignment(
                                        Assignment(
                                            employeeId = assignment.key.convertToReference("employees"),
                                            shiftId = selectedShift.convertToReference("shifts"),
                                            dates = assignment.value.filter { it.day.isNotBlank() && (it.isSelected || it.isAssigned) }
                                                .map { it.day.toInt() }
                                                .toList(),
                                            month = state.visibleMonth.monthValue,
                                            year = state.visibleMonth.year
                                        )
                                    )
                                }
                            }
                        })
                    }) {
                        Icon(Icons.Default.Done, contentDescription = "Done")
                    }
                }
            )
        }
    ) { padding ->

        sharedCalendarDays = updateSharedCalendar(expanded)

        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                ShiftSection(
                    shiftViewModel,
                    onShiftSelected = {
                        selectedShift = it

                    },
                    selectedShiftId = selectedShift
                )
            }

            val shareCalendar = Calendar.Shared(
                selectedDays = selectedDays,
                selectedWeekdays = selectedWeekdays,
                calendarDay = sharedCalendarDays,
            )

            item {
                ScheduleCalenda(
                    state = state,
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    calendar = shareCalendar,
                    onClick = { clickedDay ->
                        shareCalendar.selectDay(clickedDay)
                    },
                    onWeekdayToggle = { weekday ->
                        shareCalendar.toggleWeekday(weekday)
                    }
                )
            }

            item {
                TeamSection(
                    teams = teamViewModel.teams.value,
                    onChooseTeamClick = onChooseTeamClick,
                    onTeamClick = { teamId -> teamViewModel.getEmployees(teamId) }
                )
            }

            items(teamViewModel.employees.value) { employee ->

                calendarByEmployee[employee.id] = if (isSharedCalendar) {
                    sharedCalendarDays.map { it.copy() }.toMutableStateList()
                } else {
                    val assignedDates = assignmentStates[employee.id]?.map { it.toInt() } ?: listOf()
                    getDaysOfMonthExpanded(selectedDays, selectedWeekdays, assignedDates)
                        .map { it.copy() }.toMutableStateList()
                }

                weekdayByEmployee[employee.id] = selectedWeekdays.toList().toMutableStateList()

                val individualCalendar = Calendar.Individual(
                    employeeId = employee.id,
                    weekdayByEmployee = weekdayByEmployee,
                    calendarByEmployee = calendarByEmployee
                )

                EmployeeItem(
                    state = state,
                    employee = employee,
                    expanded = expanded,
                    calendar = individualCalendar,
                    onWeekdayToggle = { weekday -> individualCalendar.toggleWeekday(weekday, sharedCalendarDays) },
                    onClick = { clickedDay ->
                        if (!isSharedCalendar) {
                            individualCalendar.selectDay(clickedDay)
                        }
                    },
                    onSharedCalendarClick = { isSharedCalendar = true },
                    onCalendarByEmployeeClick = { isSharedCalendar = false },
                    isEnableToggleWeekday = !isSharedCalendar
                )
            }
        }
    }
}