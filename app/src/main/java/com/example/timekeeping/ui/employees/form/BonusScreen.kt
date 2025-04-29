package com.example.timekeeping.ui.employees.form

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timekeeping.ui.calender.CalendarHeader
import com.example.timekeeping.ui.calender.CalendarState
import com.example.timekeeping.ui.components.TopBarClassic
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Adjustment
import com.example.timekeeping.ui.components.InfoItem
import com.example.timekeeping.ui.employees.components.SimpleDialogS
import com.example.timekeeping.view_models.EmployeeViewModel
import com.example.timekeeping.view_models.SalaryViewModel
import org.checkerframework.checker.units.qual.A

enum class SettingItem(val label: String, val value: String) {
    NAME("Tên nhân viên", ""),
    TOTAL("Tổng thưởng / Phụ cấp", "0"),
    TIMES("Số lần", "")
}

@Composable
fun BonusScreen(
    groupId: String = "",
    employeeId: String = "",
    onBackClick: () -> Unit = {},
    onAddBonus: () -> Unit = {},
    state: CalendarState,
    onEditClick: (String, String) -> Unit,
    onDeleteClick: (String, Adjustment) -> Unit,
    salaryViewModel: SalaryViewModel = hiltViewModel(),
    employeeViewModel: EmployeeViewModel = hiltViewModel()
) {

    val salaryInfo by salaryViewModel.salaryInfo.collectAsState()
    var name by remember { mutableStateOf("") }

    var showDialog = remember { mutableStateOf(false) }
    var selectedAdjustmentId by remember { mutableStateOf("") }
    var selectedEmployeeId by remember { mutableStateOf("") }

    LaunchedEffect(employeeId, groupId, state.visibleMonth.monthValue, state.visibleMonth.year) {
        salaryViewModel.getBonusAdjustment(groupId, employeeId, state.visibleMonth.monthValue, state.visibleMonth.year)
        employeeViewModel.getName(employeeId, { name = it })
        Log.d("BonusScreen", "employeeId: $employeeId, groupId: $groupId, month: ${state.visibleMonth.monthValue}, year: ${state.visibleMonth.year}")
    }

    Scaffold(
        topBar = {
            TopBarClassic(
                title = "Quản lý thưởng / Phụ cấp",
                onBackClick = onBackClick
            )
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ){
            CalendarHeader(
                state = state,
                modifier = Modifier
            )

            val items = listOf(
                SettingItem.NAME to name,
                SettingItem.TOTAL to salaryInfo.sumOf { it.adjustmentAmount }.toString(),
                SettingItem.TIMES to salaryInfo.size.toString()
            )

            Card(
                modifier = Modifier
                    .padding(vertical = 8.dp)
            ) {
                items.forEach { item ->
                    InfoItem(
                        label = item.first.label,
                        value = item.second
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f) // chiếm hết không gian còn lại
                    .fillMaxWidth()
            ) {
                items(salaryInfo) {
                    BonusItem(
                        bonusType = it.adjustmentType,
                        adjustment = it,
                        employeeId = employeeId,
                        onEditClick = onEditClick,
                        modifier = Modifier,
                        onDeleteClick = onDeleteClick
                    )
                }
            }

            Button(
                onClick = onAddBonus,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Thêm phụ cấp")
            }

            /* ============================================================================
             * Hiển thị Dialog
             * ============================================================================
             */


//            if(showDialog.value) {
//                SimpleDialogS(
//                    title = "Thông báo",
//                    question = "Bạn có chắc chắn muốn xóa khoản  trừ này không?",
//                    onConfirm = {
//                        onDeleteClick(selectedEmployeeId, selectedAdjustmentId)
//                        salaryViewModel.getDeductMoney(groupId, employeeId, state.visibleMonth.monthValue, state.visibleMonth.year)
//                        showDialog.value = false
//                    },
//                    onDismiss = {
//                        showDialog.value = false
//                    }
//                )
//            }
        }
    }
}

@Preview
@Composable
fun PreviewBonusScreen(){
    BonusScreen(
        state = CalendarState(),
        onEditClick = { _, _ -> },
        onDeleteClick = { _, _ -> }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewInfoAllowanceSection(){
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preview InfoAllowanceSection", fontSize = 16.sp) }
            )
        }
    ) { paddingValues ->
        val items = listOf(
            SettingItem.NAME to "Nguyễn Văn A",
            SettingItem.TOTAL to "1000000",
            SettingItem.TIMES to "10"
        )
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Card(
                modifier = Modifier
                    .padding(vertical = 8.dp)
            ) {
                items.forEach { item ->
                    InfoItem(
                        label = item.first.label,
                        value = item.second
                    )
                }
            }

            val adjustment = Adjustment(
                id = "1",
                adjustmentAmount = 1000000,
                adjustmentType = "Thưởng",
                note = "Thưởng tháng 1",
            )

            BonusItem(
                adjustment = adjustment,
                bonusType = "Thưởng",
                employeeId = "1",
                onEditClick = { _, _ -> },
                onDeleteClick = { _, _ -> },
                modifier = Modifier
            )
        }
    }
}

@Composable
fun BonusItem(
    adjustment: Adjustment,
    bonusType: String = "",
    employeeId: String,
    onEditClick: (String, String) -> Unit,
    onDeleteClick: (String, Adjustment) -> Unit,
    modifier: Modifier
){
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = Icons.Default.ThumbUp,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 12.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text("Số tiền: %,d".format(adjustment.adjustmentAmount), fontSize = 16.sp)
                Text("Ngày: ${adjustment.createdAt.format("dd/MM/yyyy HH:mm")}", fontSize = 14.sp)
                Text(text = "Ghi chú: ${adjustment.note}", fontSize = 14.sp)
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(bonusType, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                Row {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Chỉnh sửa",
                        modifier = Modifier
                            .size(24.dp)
                            .padding(4.dp)
                            .clickable { onEditClick(employeeId, adjustment.id) }
                    )
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Xóa",
                        modifier = Modifier
                            .size(24.dp)
                            .padding(4.dp)
                            .clickable { onDeleteClick(employeeId, adjustment) }
                    )
                }
            }
        }
    }
}