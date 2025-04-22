package com.example.timekeeping.ui.employees.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.ui.employees.components.SimpleDialogS
import com.example.timekeeping.ui.groups.components.IconButtonWithLabel
import com.example.timekeeping.view_models.EmployeeViewModel

@Composable
fun EmployeeDetailGrid(
    employeeId: String = "",
    groupId: String = "",
    employeeViewModel: EmployeeViewModel = hiltViewModel(),
    onEmployeeInfoClick: () -> Unit = {},
    onBonusClick: () -> Unit = {},
    onMinusMoneyClick: () -> Unit = {},
    onAdvanceSalaryClick: () -> Unit = {},
    onPaymentClick: () -> Unit = {},

    onBackToEmployeeList: () -> Unit = {}
) {

    var totalOutStanding by remember { mutableStateOf(0) }

    var showDialog = remember { mutableStateOf(false) }

    LaunchedEffect(employeeId) {
        employeeViewModel.getTotalOutstanding(groupId, employeeId,{
            totalOutStanding = it
        }, {})
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Tổng chưa thanh toán")
            Text(totalOutStanding.toString())

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButtonWithLabel(
                    onClick = {onAdvanceSalaryClick()},
                    icon = Icons.Default.Warning,
                    label = "Ứng lương"
                )
                IconButtonWithLabel(
                    onClick = {onBonusClick()},
                    icon = Icons.Default.Warning,
                    label = "Thưởng/Phụ cấp"
                )
                IconButtonWithLabel(
                    onClick = {onMinusMoneyClick()},
                    icon = Icons.Default.Warning,
                    label = "Trừ tiền"
                )
                IconButtonWithLabel(
                    onClick = {onPaymentClick()},
                    icon = Icons.Default.Warning,
                    label = "Thanh toán"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButtonWithLabel(
                    onClick = {},
                    icon = Icons.Default.Warning,
                    label = "Thanh toán"
                )
                IconButtonWithLabel(
                    onClick = {onEmployeeInfoClick()},
                    icon = Icons.Default.Info,
                    label = "Thông tin nhân viên"
                )
                IconButtonWithLabel(
                    onClick = {
                        showDialog.value = true},
                    icon = Icons.Default.Warning,
                    label = "Ngừng chấm"
                )

                if (showDialog.value){
                    SimpleDialogS(
                        title = "Thông báo",
                        question = "Bạn có chắc chắn muốn dừng chấm công cho nhân viên này không?",
                        onConfirm = {
                            employeeViewModel.deleteEmloyeeGroup(groupId, employeeId)
                            showDialog.value = false
                            onBackToEmployeeList()
                        },
                        onDismiss = {
                            showDialog.value = false
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewEmployeeDetailGrid(){
    EmployeeDetailGrid("", "")
}