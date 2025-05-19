package com.example.timekeeping.ui.admin.groups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.ui.admin.calender.CalendarHeader
import com.example.timekeeping.ui.admin.calender.CalendarState
import com.example.timekeeping.ui.admin.calender.rememberCalendarState
import com.example.timekeeping.ui.admin.components.TopBarClassic
import com.example.timekeeping.utils.toPositive
import com.example.timekeeping.view_models.PayrollViewModel
import com.example.timekeeping.view_models.SalaryViewModel
import java.time.format.DateTimeFormatter

enum class InfoSection(val label: String) {
    TIME("Thời gian"),
    TOTAL_WORKDAY("Tổng ngày công"),
    PAID_LEAVE("Tổng nghỉ có lương"),
    TOTAL_BONUS("Tổng thưởng/phụ cấp"),
    TOTAL_SALARY("Tổng lương"),
    ADVANCE("Đã ứng"),
    TOTAL_UNPAID("Tổng chưa thanh toán")
}

@Composable
fun MonthlyPayrollScreen(
    groupId: String,
    onBackClick: () -> Unit,
    salaryViewModel: SalaryViewModel = hiltViewModel(),
    payrollViewModel: PayrollViewModel = hiltViewModel(),
    state: CalendarState = rememberCalendarState()
) {

    val firstDayOfMonth by remember {
        derivedStateOf {
            state.visibleMonth.atDay(1) // YearMonth → LocalDate
        }
    }
    val lastDayOfMonth by remember {
        derivedStateOf {
            state.visibleMonth.atEndOfMonth() // tiện hơn dùng TemporalAdjusters
        }
    }

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val firstDayFormatted = firstDayOfMonth.format(formatter)
    val lastDayFormatted = lastDayOfMonth.format(formatter)

    var totalSalary by remember { mutableStateOf(0) }
    var totalAdvance by remember { mutableStateOf(0) }
    var totalWorkDay by remember { mutableStateOf(0) }
    var totalPaidLeave by remember { mutableStateOf(0) }
    var totalBonus by remember { mutableStateOf(0) }

    var totalPayment by remember { mutableStateOf(0) }

    LaunchedEffect(groupId, state.visibleMonth) {
        salaryViewModel.getTotalUnpaidSalary(groupId, state.visibleMonth.monthValue, state.visibleMonth.year)

//        salaryViewModel.getTotalSalary(groupId, state.visibleMonth.monthValue, state.visibleMonth.year) {
//            totalSalary = it
//        }

        salaryViewModel.getTotalAdvanceMoney(groupId, state.visibleMonth.monthValue, state.visibleMonth.year) {
            totalAdvance = it
        }

        salaryViewModel.getTotalWorkDay(groupId, state.visibleMonth.monthValue, state.visibleMonth.year) {workDays, paidLeaveDays ->
            totalWorkDay = workDays
            totalPaidLeave = paidLeaveDays
        }

        salaryViewModel.getTotalBonus(groupId, state.visibleMonth.monthValue, state.visibleMonth.year) {
            totalBonus = it
        }

        payrollViewModel.getTotalWageGroupByMonth(groupId, state.visibleMonth.monthValue, state.visibleMonth.year){
            totalSalary = it
        }

        payrollViewModel.getTotalPaymentGroupByMonth(groupId, state.visibleMonth.monthValue, state.visibleMonth.year) {
            totalPayment = it
        }
    }

    Scaffold(
        topBar = {
            TopBarClassic(
                title = "Bảng lương theo tháng",
                onBackClick = onBackClick
            )
        }
    ) {
        paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item{
                CalendarHeader(
                    state = state,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            item{
                Card ( modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    getValues(
                        firstDayFormatted,
                        lastDayFormatted,
                        totalSalary.toString(),
                        totalAdvance.toPositive().toString(),
                        totalSalary.minus(-totalAdvance).minus(totalPayment).toString(),
                        totalWorkDay.toString(),
                        totalPaidLeave.toString(),
                        totalBonus.toString()
                    ).entries.forEach({ info ->
                        InfoItem(infoSection = info.key, info.value)
                    })
                }
            }

            item {
                Text("Danh sách nhân viên")
            }
        }
    }
}

fun getValues(
    firstDayFormatted: String,
    lastDayFormatted: String,
    totalSalary: String,
    totalAdvance: String,
    totalUnpaid: String,
    totalWorkDay: String,
    totalPaidLeave: String,
    totalBonus: String
): Map<InfoSection, String> {

    return mapOf(
        InfoSection.TIME to "$firstDayFormatted - $lastDayFormatted",
        InfoSection.TOTAL_WORKDAY to totalWorkDay,
        InfoSection.PAID_LEAVE to totalPaidLeave,
        InfoSection.TOTAL_BONUS to totalBonus,
        InfoSection.TOTAL_SALARY to totalSalary,
        InfoSection.ADVANCE to totalAdvance,
        InfoSection.TOTAL_UNPAID to totalUnpaid
    )

}

@Composable
fun InfoItem(
    infoSection: InfoSection,
    value: String
){
    Row(
        modifier = Modifier.padding(16.dp),
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ){
            Text(infoSection.label)
        }
        Text(value)
    }
}

@Preview
@Composable
fun MonthlyPayrollScreenPreview() {
    MonthlyPayrollScreen(
        groupId = "1",
        onBackClick = {}
    )
}