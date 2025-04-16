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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timekeeping.ui.groups.components.IconButtonWithLabel

@Composable
fun EmployeeDetailGrid(
    onEmployeeInfoClick: () -> Unit = {}
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
                onClick = {},
                icon = Icons.Default.Warning,
                label = "Chấm công"
            )
            IconButtonWithLabel(
                onClick = {},
                icon = Icons.Default.Warning,
                label = "Đơn nghỉ phép"
            )
            IconButtonWithLabel(
                onClick = {},
                icon = Icons.Default.Warning,
                label = "Cộng lương"
            )
            IconButtonWithLabel(
                onClick = {},
                icon = Icons.Default.Warning,
                label = "Trừ lương"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButtonWithLabel(
                onClick = {},
                icon = Icons.Default.Warning,
                label = "Ứng lương"
            )
            IconButtonWithLabel(
                onClick = {onEmployeeInfoClick()},
                icon = Icons.Default.Info,
                label = "Thông tin nhân viên"
            )
        }
    }
}

@Preview
@Composable
fun PreviewEmployeeDetailGrid(){
    EmployeeDetailGrid()
}