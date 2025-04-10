package com.example.timekeeping.ui.shifts

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timekeeping.models.Shift
import com.example.timekeeping.ui.components.EntityList
import com.example.timekeeping.ui.components.TopBarWithAddAction
import com.example.timekeeping.ui.shifts.components.ShiftItem
import com.example.timekeeping.view_models.ShiftViewModel


@Composable
fun ShiftManagementScreen(
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit = {},
    onDeleteClick: (String) -> Unit = {},
    onAddShiftClick: () -> Unit = {},
    viewModel: ShiftViewModel = viewModel(),
) {
    val shifts by viewModel.shifts
    ShiftManagementScreen(
        onBackClick = onBackClick,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        onAddShiftClick = onAddShiftClick,
        shifts = shifts
    )
}

@Composable
fun ShiftManagementScreen(
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit = {},
    onDeleteClick: (String) -> Unit = {},
    onAddShiftClick: () -> Unit = {},
    shifts: List<Shift>,
) {
    Scaffold(
        topBar = {
            TopBarWithAddAction(
                title = "Ca công việc",
                onBackClick = onBackClick,
                onAddShiftClick = onAddShiftClick
            )
        }
    ) { paddingValues ->
        EntityList(shifts, modifier = Modifier.padding(paddingValues)) {
            ShiftItem(
                shift = it,
                onEditClick = { onEditClick(it.id) },
                onDeleteClick = { onDeleteClick(it.id) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShiftManagementScreenPreview() {
    ShiftManagementScreen(
        onBackClick = {},
        onEditClick = {},
        onDeleteClick = {},
        onAddShiftClick = {},
        shifts = listOf(
            Shift(
                id = "1",
                shiftName = "Ca sáng",
                startTime = "08:00",
                endTime = "12:00",
                coefficient = 1.0,
                allowance = 50000),
            Shift(
                id = "2",
                shiftName = "Ca chiều",
                startTime = "13:00",
                endTime = "17:00",
                coefficient = 1.0,
            )
        )
    )
}