package com.example.timekeeping.ui.assignment.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timekeeping.view_models.ShiftViewModel

@Composable
fun ShiftSection(
    shiftViewModel: ShiftViewModel,
    selectedShiftId: String?, // Thêm dòng này
    onShiftSelected: (String) -> Unit,
) {
    Column {
        Text("Chọn ca", modifier = Modifier.padding(16.dp))
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            shiftViewModel.shifts.value.forEach { shift ->
                val isSelected = shift.id == selectedShiftId
                ShiftItem(
                    onShiftClick = { onShiftSelected(shift.id) },
                    id = shift.id,
                    shiftName = shift.shiftName,
                    startTime = shift.startTime,
                    endTime = shift.endTime,
                    isSelected = isSelected // Truyền vào đây
                )
            }
        }
    }
}
