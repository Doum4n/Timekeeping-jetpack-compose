package com.example.timekeeping.ui.admin.shifts.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timekeeping.models.Shift

@Composable
fun ShiftItem(
    shift: Shift,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = CenterVertically
        ) {
            Text(
                text = shift.shiftName,
                modifier = Modifier
                    .padding(0.dp)
                    .weight(1f)

            )
            Icon(
                Icons.Default.Edit,
                contentDescription = "Edit",
                modifier = Modifier.padding(end = 8.dp)
                    .clickable { onEditClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable { onDeleteClick() }
            )
        }

        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${shift.startTime} - ${shift.endTime}")
                Text(text = "Hệ số: ${shift.coefficient}")
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Phụ cấp: ${shift.allowance}")
                Text(text = "Trạng thái")
            }
        }
    }
}


