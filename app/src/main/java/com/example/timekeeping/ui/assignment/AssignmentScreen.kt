package com.example.timekeeping.ui.assignment

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.timekeeping.ui.calender.CalendarState
import com.example.timekeeping.view_models.AssignmentViewModel
import com.example.timekeeping.view_models.ShiftViewModel
import com.example.timekeeping.view_models.TeamViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class CalendarDay(val day: String, var isSelected: Boolean, val isAssigned: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentScreen(
    state: CalendarState,
    onDone: () -> Unit,
    onBackClick: () -> Unit,
    onChooseTeamClick: () -> Unit,
    shiftViewModel: ShiftViewModel,
    teamViewModel: TeamViewModel,
    viewModel: AssignmentViewModel
) {
    var calendarDays by remember { mutableStateOf(listOf<CalendarDay>()) }
    var assignmentDates by remember { mutableStateOf(listOf<DayOfWeek>()) }
    var assignmentId by remember { mutableStateOf("") }

    Log.d("AssignmentScreen", "assignmentDates: $assignmentDates")

    LaunchedEffect(Unit) {
        viewModel.getAssignments { assignments ->
            if (assignments.isNotEmpty()) {
                val currentYearMonth = YearMonth.now()
                assignmentDates = assignments
                    .flatMap { it.dates }
                    .map { day -> currentYearMonth.atDay(day).dayOfWeek }
                    .distinct()

                assignmentId = assignments.first().id
            } else {
                assignmentDates = emptyList()
                assignmentId = "" // hoặc null nếu bạn dùng nullable String
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
                        if (calendarDays.count { it.isAssigned && !it.isSelected } < calendarDays.size) {
                            if(assignmentId.isEmpty()){
                                viewModel.addAssignment(
                                    Assignment(
                                        shiftId = shiftViewModel.shifts.value.first().id,
                                        employeeId = "userId",
                                        teamId = "teamId",
                                        month = state.visibleMonth.month,
                                        year = java.time.Year.of(state.visibleMonth.year).value,
                                        dates = calendarDays.filter { it.isSelected || it.isAssigned }
                                            .map { it.day.toInt() }
                                    )
                                )
                            }else{
                                viewModel.updateAssignment(
                                    assignmentId = assignmentId,
                                    Assignment(
                                        shiftId = shiftViewModel.shifts.value.first().id,
                                        employeeId = "userId",
                                        teamId = "teamId",
                                        month = state.visibleMonth.month,
                                        year = java.time.Year.of(state.visibleMonth.year).value,
                                        dates = calendarDays.filter { it.isSelected || it.isAssigned }
                                            .map { it.day.toInt() }
                                    )
                                )
                            }
                        }
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
                    ShiftItem(shift.shiftName, shift.startTime, shift.endTime)
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

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                userScrollEnabled = false
            ) {
                items(calendarDays) { calendarDay ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .aspectRatio(1f)
                            .background(
                                color = when {
                                    calendarDay.isSelected -> MaterialTheme.colorScheme.primary
                                    calendarDay.isAssigned -> Color.LightGray // ✅ hiển thị ngày đã phân công
                                    else -> Color.Transparent
                                }
                            ).clickable {
                                calendarDays = calendarDays.map {
                                    if (it.day == calendarDay.day) {
                                        it.copy(isSelected = !it.isSelected)
                                    } else {
                                        it
                                    }
                                }
                            },
                            contentAlignment = Alignment.Center,
                    ) {
                        Text(text = calendarDay.day)
                    }
                }
            }


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

fun getWeekdays(): List<DayOfWeek> = listOf(
    DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
)

fun getShortNameFor(dayOfWeek: DayOfWeek): String {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> "T2"
        DayOfWeek.TUESDAY -> "T3"
        DayOfWeek.WEDNESDAY -> "T4"
        DayOfWeek.THURSDAY -> "T5"
        DayOfWeek.FRIDAY -> "T6"
        DayOfWeek.SATURDAY -> "T7"
        DayOfWeek.SUNDAY -> "CN"
    }
}

@SuppressLint("SimpleDateFormat")
fun getDaysOfMonthExpanded(
    selectedWeekdays: List<DayOfWeek>,
    assignedDates: List<DayOfWeek>
): List<CalendarDay> {
    val now = LocalDate.now()
    val firstDay = now.withDayOfMonth(1)
    val lastDay = now.withDayOfMonth(now.lengthOfMonth())

    val startOffset = (firstDay.dayOfWeek.value + 5) % 6
    val days = mutableListOf<CalendarDay>()

    // Add prefix empty cells
    repeat(startOffset) {
        days.add(CalendarDay("", false))
    }

    for (day in 1..lastDay.dayOfMonth) {
        val date = now.withDayOfMonth(day)
        val isSelected = selectedWeekdays.contains(date.dayOfWeek)
        val isAssigned = assignedDates.contains(date.dayOfWeek)
        days.add(CalendarDay(day.toString(), isSelected, isAssigned))
    }

    return days
}

fun getDaysOfMonthShrunk(selectedWeekdays: List<DayOfWeek>): List<CalendarDay> {
    val now = LocalDate.now()
    val days = mutableListOf<CalendarDay>()
    val firstDay = now.withDayOfMonth(1)
    val startOffset = (firstDay.dayOfWeek.value + 5) % 6

    repeat(startOffset) {
        days.add(CalendarDay("", false))
    }

    for (i in 1..6) {
        val date = now.plusDays(i.toLong())
        val isSelected = selectedWeekdays.contains(date.dayOfWeek)
        days.add(CalendarDay(i.toString(), isSelected))
    }

    return days
}

//fun checkDayOfWeek(selectedWeekdays: List<DayOfWeek>, )

@Composable
fun ShiftItem(
    shiftName: String,
    startTime: String,
    endTime: String,
){
    Card {
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