package com.example.timekeeping.ui.shifts

import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.timekeeping.models.Shift
import com.example.timekeeping.view_models.ShiftViewModel
import java.time.LocalTime
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftInputForm(
    shiftId: String = "",
    groupId: String,
    onBackClick: () -> Unit,
    onSave: (Shift) -> Unit
) {
    // State for input fields
    var name by remember { mutableStateOf(TextFieldValue()) }
    var startTime by remember { mutableStateOf(LocalTime.of(0, 0)) }
    var endTime by remember { mutableStateOf(LocalTime.of(0, 0)) }
    var salaryCoefficient by remember { mutableStateOf(1F) }
    var allowance by remember { mutableStateOf(0) }

    val context = LocalContext.current

    LaunchedEffect(shiftId) {
        Log.d("ShiftInputForm", "shiftId: $shiftId")
        if (shiftId != "") {
            ShiftViewModel(groupId = groupId).getShiftById(shiftId) { shift ->
                name = TextFieldValue(shift.name)
                startTime = LocalTime.parse(shift.startTime)
                endTime = LocalTime.parse(shift.endTime)
                allowance = shift.allowance
                salaryCoefficient = shift.coefficient.toFloat()
            }
        }
    }

    // Function to show the TimePickerDialog
    fun showTimePicker(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val timeString = String.format("%02d:%02d", hourOfDay, minute)
                val time = LocalTime.parse(timeString)
                if (isStartTime) {
                    startTime = time
                } else {
                    endTime = time
                }
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    // Scaffold Layout
    Scaffold(
        topBar = {
            // You can customize the top bar here if needed
            TopAppBar(
                title = { Text("Danh sách thành viên") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(paddingValues), // Adding padding from Scaffold
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Shift Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Start Time Input (Button to show time picker)
                    OutlinedTextField(
                        value = startTime.toString(),
                        onValueChange = {},
                        label = { Text("Start Time") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showTimePicker(isStartTime = true) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Select Start Time")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                    )

                    // End Time Input (Button to show time picker)
                    OutlinedTextField(
                        value = endTime.toString(),
                        onValueChange = {},
                        label = { Text("End Time") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showTimePicker(isStartTime = false) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Select End Time")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Salary coefficient Input
                OutlinedTextField(
                    value = salaryCoefficient.toString(),
                    onValueChange = { salaryCoefficient = it.toFloatOrNull() ?: 1F },
                    label = { Text("Hệ số lương") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Allowance Input
                OutlinedTextField(
                    value = allowance.toString(),
                    onValueChange = { allowance = it.toIntOrNull() ?: 0 },
                    label = { Text("Phụ cấp ca") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Save Button
                Button(
                    onClick = {
                        // Create a Shift object from the form data
                        val shift = Shift(
                            coefficient = salaryCoefficient.toDouble(),
                            allowance = allowance,
                            name = name.text,
                            startTime = startTime.toString(),
                            endTime = endTime.toString(),
                            groupId = groupId
                        )
                        onSave(shift) // Handle the save action
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Lưu")
                }
            }
        }
    )
}
