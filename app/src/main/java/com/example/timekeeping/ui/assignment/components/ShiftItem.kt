package com.example.timekeeping.ui.assignment.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShiftItem(
    onShiftClick: (String) -> Unit,
    id: String,
    shiftName: String,
    startTime: String,
    endTime: String,
){
    Card(
        onClick = { onShiftClick(id) },
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = shiftName)
            Text(text = "$startTime - $endTime")
        }
    }
}