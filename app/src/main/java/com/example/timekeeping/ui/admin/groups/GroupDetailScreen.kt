package com.example.timekeeping.ui.admin.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.ui.admin.calender.CalendarScreen
import com.example.timekeeping.ui.admin.groups.components.GroupDetailButtonGrid
import com.example.timekeeping.ui.admin.groups.components.GroupDetailTopBar
import com.example.timekeeping.utils.formatCurrency
import com.example.timekeeping.utils.toPositive
import com.example.timekeeping.view_models.PayrollViewModel
import com.example.timekeeping.view_models.SalaryViewModel
import java.time.LocalDate

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
    onSalaryClick: (String) -> Unit,
    onRuleManagementClick: () -> Unit,
    onApproveRequestClick: () -> Unit,

    salaryViewModel: SalaryViewModel = hiltViewModel(),
    payrollViewModel: PayrollViewModel = hiltViewModel()
) {

    var totalSalary by remember { mutableStateOf(0) }
    var totalPayment by remember { mutableStateOf(0) }
    var totalSalaryByMonth by remember { mutableStateOf(0) }
    var totalAdvanceByMonth by remember { mutableStateOf(0) }

    LaunchedEffect(groupId) {
        salaryViewModel.getTotalUnpaidSalary(groupId, LocalDate.now().monthValue, LocalDate.now().year, isAllTime = true)

        salaryViewModel.getTotalAdvanceMoney(groupId, LocalDate.now().monthValue, LocalDate.now().year) {
            totalAdvanceByMonth = it
        }

        payrollViewModel.getTotalWageGroupByMonth(groupId, LocalDate.now().monthValue, LocalDate.now().year) {
            totalSalaryByMonth -= it
        }

        payrollViewModel.getTotalPayment(groupId, ) {
            totalPayment = it
        }

        payrollViewModel.getTotalWageGroup(groupId) {
            totalSalary = it
        }

        salaryViewModel.getSalaryInfo(groupId)
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Tổng chưa thanh toán",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                Text(
                    (totalSalary - totalPayment).formatCurrency()
                )
            }

            item {
                GroupDetailButtonGrid(
                    onCheckInClick = onCheckInClick,
                    onScheduleClick = onScheduleClick,
                    onEmployeeManagementClick = onEmployeeManagementClick,
                    onShiftManagementClick = onShiftManagementClick,
                    onRuleManagementClick = onRuleManagementClick,
                    onApproveRequestClick = onApproveRequestClick
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(colorScheme.surfaceVariant)
                        .clickable {
                            onSalaryClick(groupId)
                        }
                ) {
                    SalarySection(totalSalary = totalSalary.formatCurrency(), totalAdvance = totalAdvanceByMonth.toPositive().formatCurrency())
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(colorScheme.surfaceVariant)
                ) {
                    CalendarScreen()
                }
            }
        }

    }
}

@Composable
fun SalarySection(
    totalSalary: String,
    totalAdvance: String,
    modifier: Modifier = Modifier
) {
    val currentMonth = LocalDate.now().monthValue

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tiêu đề
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Tiền công tháng $currentMonth",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Tháng khác",
                modifier = Modifier.clickable { /* TODO */ },
                color = colorScheme.primary
            )
        }

        // Dòng tổng tiền công
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Tổng tiền công")
            Text(totalSalary, fontWeight = FontWeight.Bold)
        }

        // Dòng tổng đã ứng
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Tổng đã ứng")
            Text(totalAdvance, fontWeight = FontWeight.Bold)
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
        groupId = "",
        onSalaryClick = {},
        onRuleManagementClick = {},
        onApproveRequestClick = {}
    )

}


