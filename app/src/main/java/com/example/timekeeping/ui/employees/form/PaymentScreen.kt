package com.example.timekeeping.ui.employees.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Adjustment
import com.example.timekeeping.ui.assignment.components.CalendarHeader
import com.example.timekeeping.ui.calender.CalendarState
import com.example.timekeeping.ui.components.TopBarClassic
import com.example.timekeeping.view_models.SalaryViewModel

data class SalaryInfo(
    val employeeName: String = "",
    val totalWorkingSalary: Int = 0,
    val totalBonus: Int = 0,
    val totalSalary: Int = 0,
    val totalAdvance: Int = 0,
    val totalDeduct: Int = 0,
    val totalPaid: Int = 0,
    val totalUnpaid: Int = 0
)

data class PaymentItem(
    val title: String,
    val value: String
)

@Composable
fun PaymentScreen(
    state: CalendarState,
    employeeId: String,
    groupId: String,
    onBack: () -> Unit,
    salaryViewModel: SalaryViewModel = hiltViewModel()
) {

    val adjustmentsInfo = salaryViewModel.salaryInfo.collectAsState()

    val totalWage = remember { mutableStateOf(0) }

    LaunchedEffect(adjustmentsInfo, totalWage) {
        salaryViewModel.getSalaryInfoByMonth(groupId, employeeId, state.visibleMonth.month.value, state.visibleMonth.year)
        salaryViewModel.calculateTotalWage(groupId, employeeId, state.visibleMonth.month.value, state.visibleMonth.year) {
            totalWage.value = it
        }
    }

    Scaffold(
        topBar = {
            TopBarClassic(
                title = "Quản lý thanh toán",
                onBackClick = onBack
            )
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            PaymentContent(state, adjustmentsInfo.value, totalWage.value)
        }
    }
}

@Composable
fun PaymentContent(state: CalendarState, salaryInfo: List<Adjustment>, totalWage: Int) {

    val salary = SalaryInfo(
        employeeName = "Nguyễn Văn A",
        totalWorkingSalary = totalWage ,
        totalBonus = salaryInfo.filter { it.adjustmentType in TypeAllowance.entries.map { it.label } }.sumOf { it.adjustmentAmount },
        totalSalary = salaryInfo.filter { it.adjustmentType in TypeAllowance.entries.map { it.label } }.sumOf { it.adjustmentAmount } + totalWage,
        totalAdvance = salaryInfo.filter { it.adjustmentType == "Ứng lương" }.sumOf { it.adjustmentAmount },
        totalDeduct = salaryInfo.filter {
            it.adjustmentType in TypeDeduct.entries.filter { it.label != "Ứng lương" }.map { it.label }
        }.sumOf { it.adjustmentAmount },
        totalPaid = salaryInfo.filter { it.adjustmentType == "Đã thanh toán" }.sumOf { it.adjustmentAmount },
        totalUnpaid = salaryInfo.filter { it.adjustmentType == "Chưa thanh toán" }.sumOf { it.adjustmentAmount }
    )

    val paymentInfo = listOf(
        PaymentItem("Nhân viên", salary.employeeName),
        PaymentItem("Thời gian", "${state.visibleMonth.month.value}/${state.visibleMonth.year}"),
        PaymentItem("Tổng tiền công", formatCurrency(salary.totalWorkingSalary)),
        PaymentItem("Tổng thưởng / Phụ cấp", formatCurrency(salary.totalBonus)),
        PaymentItem("Tổng lương", formatCurrency(salary.totalSalary)),
        PaymentItem("Tổng đã ứng", formatCurrency(salary.totalAdvance)),
        PaymentItem("Tổng trừ lương", formatCurrency(salary.totalDeduct)),
        PaymentItem("Tổng đã thanh toán", formatCurrency(salary.totalPaid)),
        PaymentItem("Tổng chưa thanh toán", formatCurrency(salary.totalUnpaid)),
    )

    CalendarHeader(
        state = state,
    )
    Card (
        modifier = Modifier
            .padding(16.dp)
    ) {
        LazyColumn {
            items(paymentInfo) { item ->
                Item(
                    title = item.title,
                    value = item.value
                )
            }
        }
    }
}

@Composable
fun Item(
    title: String,
    value: String = "",
){
    Column(
        Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(

        ) {
            Box(
                modifier = Modifier.weight(1f)
            ){
                Text(text = title)
            }
            Text(text = value)
        }
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
    }
}

fun formatCurrency(value: Int): String {
    return "%,dđ".format(value).replace(',', '.')
}