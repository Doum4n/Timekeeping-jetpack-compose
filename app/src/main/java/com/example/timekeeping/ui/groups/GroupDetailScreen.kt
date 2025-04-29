package com.example.timekeeping.ui.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.ui.calender.CalendarScreen
import com.example.timekeeping.ui.groups.components.GroupDetailButtonGrid
import com.example.timekeeping.ui.groups.components.GroupDetailTopBar
import com.example.timekeeping.ui.groups.components.IconButtonWithLabel
import com.example.timekeeping.utils.formatCurrency
import com.example.timekeeping.view_models.SalaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupId: String,
    onBackClick: () -> Unit,
    onEmployeeManagementClick: () -> Unit,
    onShiftManagementClick: () -> Unit,
    onScheduleClick: () -> Unit,
    onCheckInClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onDelete: () -> Unit,

    salaryViewModel: SalaryViewModel = hiltViewModel()
) {

    LaunchedEffect(groupId) {
        salaryViewModel.getTotalUnpaidSalary(groupId)
    }

    Scaffold(
        topBar = {
            GroupDetailTopBar(
                onBackClick = onBackClick,
                onSettingsClick = onSettingsClick,
                onDelete = onDelete
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Tổng chưa thanh toán", style = MaterialTheme.typography.titleLarge)
            Text(salaryViewModel.totalUnpaidSalary.collectAsState().value.formatCurrency())
            GroupDetailButtonGrid(
                onCheckInClick = onCheckInClick,
                onScheduleClick = onScheduleClick,
                onEmployeeManagementClick = onEmployeeManagementClick,
                onShiftManagementClick = onShiftManagementClick
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                CalendarScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupDetailScreenPreview() {
    GroupDetailScreen(
        //groupId = "1",
        onBackClick = { /*TODO*/ },
        onEmployeeManagementClick = { /*TODO*/ },
        onShiftManagementClick = { /*TODO*/ },
        onScheduleClick = { /*TODO*/ },
        onCheckInClick = { /*TODO*/ },
        onSettingsClick = { /*TODO*/ },
        onDelete = { /*TODO*/ },
        groupId = ""
    )

}


