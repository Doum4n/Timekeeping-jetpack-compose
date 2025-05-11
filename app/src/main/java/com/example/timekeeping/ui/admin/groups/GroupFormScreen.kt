package com.example.timekeeping.ui.admin.groups

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.timekeeping.models.Group
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GroupFormScreen(
    creatorId: String,
    onSubmit: (Group) -> Unit
) {
    val creatorIdState = remember { mutableStateOf("") }
    val nameState = remember { mutableStateOf("") }
    val paydayState = remember { mutableStateOf<Date?>(null) }

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        TextField(
            value = nameState.value,
            onValueChange = { nameState.value = it },
            label = { Text("Group Name") },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Hiển thị ngày đã chọn
        Text("Payday: ${paydayState.value?.let { dateFormatter.format(it) } ?: "Chưa chọn"}")

        Button(
            onClick = {
                showDatePicker(context) { selectedDate ->
                    paydayState.value = selectedDate
                }
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Chọn ngày")
        }

        Button(
            onClick = {
                val group = Group(
                    name = nameState.value,
                    payday = paydayState.value
                )
                onSubmit(group)
            }
        ) {
            Text("Submit")
        }
    }
}

// Hàm mở DatePickerDialog
fun showDatePicker(context: Context, onDateSelected: (Date) -> Unit) {
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            onDateSelected(selectedCalendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.show()
}
