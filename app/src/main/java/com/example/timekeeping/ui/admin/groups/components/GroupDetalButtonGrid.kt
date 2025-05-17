package com.example.timekeeping.ui.admin.groups.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.Beenhere
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.PeopleAlt

import androidx.compose.material.icons.filled.PermContactCalendar
import androidx.compose.material.icons.filled.PlaylistAddCheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun GroupDetailButtonGrid(
    onCheckInClick: () -> Unit,
    onScheduleClick: () -> Unit,
    onEmployeeManagementClick: () -> Unit,
    onShiftManagementClick: () -> Unit,
    onRuleManagementClick: () -> Unit,
    onApproveRequestClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButtonWithLabel(
                onClick = onCheckInClick,
                icon = Icons.Default.Beenhere,
                label = "Chấm công"
            )
            IconButtonWithLabel(
                onClick = onScheduleClick,
                icon = Icons.Default.EditCalendar,
                label = "Xếp lịch"
            )
            IconButtonWithLabel(
                onClick = onRuleManagementClick,
                icon = Icons.Default.PermContactCalendar,
                label = "Qui tắc tính lương"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButtonWithLabel(
                onClick = onEmployeeManagementClick,
                icon = Icons.Default.PeopleAlt,
                label = "Danh sách nhân viên"
            )
            IconButtonWithLabel(
                onClick = onShiftManagementClick,
                icon = Icons.AutoMirrored.Filled.FactCheck,


                        label = "Quản lý ca"
            )
            IconButtonWithLabel(
                onClick = onApproveRequestClick,
                icon = Icons.Default.PlaylistAddCheckCircle,
                label = "Duyệt yêu cầu"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGroupDetailButtonGrid(){
    GroupDetailButtonGrid(
        onCheckInClick = {},
        onScheduleClick = {},
        onEmployeeManagementClick = {},
        onShiftManagementClick = {},
        onRuleManagementClick = {},
        onApproveRequestClick = {}
    )
}