package com.example.timekeeping.ui.admin.employees.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Adjustment
import com.example.timekeeping.models.Payment
import com.example.timekeeping.ui.admin.calender.CalendarState
import com.example.timekeeping.ui.admin.components.TopBarClassic
import com.example.timekeeping.utils.SessionManager
import com.example.timekeeping.utils.formatCurrency
import com.example.timekeeping.view_models.EmployeeViewModel
import com.example.timekeeping.view_models.PaymentViewModel
import com.example.timekeeping.view_models.SalaryViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

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
    onPaymentClick: () -> Unit,
    salaryViewModel: SalaryViewModel = hiltViewModel(),
    employeeViewModel: EmployeeViewModel = hiltViewModel(),
    paymentViewModel: PaymentViewModel = hiltViewModel(),
    onEditClick: (String) -> Unit,
    onDeleteClick: (Payment) -> Unit
) {

    var name by remember { mutableStateOf("") }

    val adjustmentsInfo = salaryViewModel.salaryInfo.collectAsState()
    val payments by paymentViewModel.payments.collectAsState()

    val totalPayment by remember(payments) {
        mutableStateOf(payments.sumOf { it.amount })
    }
    val totalWage = remember { mutableStateOf(0) }

    val role = SessionManager.getRole()

    LaunchedEffect(state.visibleMonth) {
        salaryViewModel.getSalaryInfoByMonth(groupId, employeeId, state.visibleMonth.month.value, state.visibleMonth.year)
        salaryViewModel.calculateTotalWage(groupId, employeeId, state.visibleMonth.month.value, state.visibleMonth.year) {
            totalWage.value = it
        }
        paymentViewModel.getPayments(groupId, employeeId, state.visibleMonth.monthValue, state.visibleMonth.year)
        employeeViewModel.getName(employeeId, {
            name = it
        })
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
            modifier = Modifier.run {
                padding(paddingValues)
                        .padding(8.dp)
                        .fillMaxSize()
            }
        ) {
            PaymentContent(name, state, adjustmentsInfo.value, totalWage.value, totalPayment)

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(payments) { payment ->
                    PaymentItem(
                        payment,
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick,
                        role = role.toString()
                    )
                }
            }

            if (role == "ADMIN") {
                Button(
                    onClick = onPaymentClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Thanh toán")
                }
            }
        }
    }
}

@Composable
fun PaymentContent(name: String, state: CalendarState, salaryInfo: List<Adjustment>, totalWage: Int, totalPayment: Int) {

    val allowanceLabels = TypeAllowance.entries.map { it.label }
    val deductLabels = TypeDeduct.entries.map { it.label }.filter { it != "Ứng lương" }
    val totalBonus = salaryInfo.filter { it.adjustmentType in allowanceLabels }.sumOf { it.adjustmentAmount }
    val totalAdvance = salaryInfo.filter { it.adjustmentType == "Ứng lương" }.sumOf { it.adjustmentAmount }
    val totalDeduct = salaryInfo.filter { it.adjustmentType in deductLabels }.sumOf { it.adjustmentAmount }
    val totalUnpaid = totalWage + totalBonus + totalAdvance + totalDeduct - totalPayment

    val salary = SalaryInfo(
        employeeName = name,
        totalWorkingSalary = totalWage,
        totalBonus = totalBonus,
        totalSalary = totalWage + totalBonus,
        totalAdvance = totalAdvance,
        totalDeduct = totalDeduct,
        totalPaid = totalPayment,
        totalUnpaid = totalUnpaid
    )

    val paymentInfo = listOf(
        PaymentItem("Nhân viên", salary.employeeName),
        PaymentItem("Thời gian", "${state.visibleMonth.month.value}/${state.visibleMonth.year}"),
        PaymentItem("Tổng tiền công", salary.totalWorkingSalary.formatCurrency()),
        PaymentItem("Tổng thưởng / Phụ cấp", salary.totalBonus.formatCurrency()),
        PaymentItem("Tổng lương", (salary.totalSalary.formatCurrency())),
        PaymentItem("Tổng đã ứng", (salary.totalAdvance.formatCurrency())),
        PaymentItem("Tổng trừ lương", (salary.totalDeduct.formatCurrency())),
        PaymentItem("Tổng đã thanh toán", (salary.totalPaid.formatCurrency())),
        PaymentItem("Tổng chưa thanh toán", (salary.totalUnpaid.formatCurrency())),
    )

    CalendarHeaderWithMonthYear(
        state = state
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
fun CalendarHeaderWithMonthYear(
    state: CalendarState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { state.prevMonth() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
        }

        Text(
            text = state.visibleMonth.format(DateTimeFormatter.ofPattern("MMMM 'năm' yyyy", Locale("vi", "VN"))),
            style = MaterialTheme.typography.titleMedium
        )

        IconButton(onClick = { state.nextMonth() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
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

@Composable
fun PaymentItem(
    payment: Payment,
    onEditClick: (String) -> Unit,
    onDeleteClick: (Payment) -> Unit,
    role: String
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Ngày thanh toán: ${payment.createdAt.format("dd/MM/yyyy HH:mm")}")
            Text(text = "Số tiền: ${payment.amount.formatCurrency()}")
            Text(text = "Ghi chú: ${payment.note}")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (role == "ADMIN") {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { onEditClick(payment.id) }
                    )
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.clickable { onDeleteClick(payment) }
                    )
                }
            }
        }
    }
}
