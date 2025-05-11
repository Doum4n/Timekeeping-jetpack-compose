package com.example.timekeeping.ui.admin.shifts

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Shift
import com.example.timekeeping.ui.admin.components.EntityList
import com.example.timekeeping.ui.admin.components.TopBarWithAddAction
import com.example.timekeeping.ui.admin.shifts.components.ShiftItem
import com.example.timekeeping.view_models.ShiftViewModel


@Composable
fun ShiftManagementScreen(
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit = {},
    onDeleteClick: (String) -> Unit = {},
    onAddShiftClick: () -> Unit = {},
    viewModel: ShiftViewModel = hiltViewModel(),
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