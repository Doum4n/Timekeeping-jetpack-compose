package com.example.timekeeping.ui.admin.shifts

import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import com.example.timekeeping.models.Shift
import com.example.timekeeping.models.Time
import com.example.timekeeping.view_models.ShiftViewModel
import java.time.LocalTime
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftInputForm(
    shiftViewModel: ShiftViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onSave: (Shift) -> Unit
) {
    // State for input fields
    var name by remember { mutableStateOf(TextFieldValue()) }
    var startTime by remember { mutableStateOf(Time(0, 0)) }
    var endTime by remember { mutableStateOf(Time(0, 0)) }
    var salaryCoefficient by remember { mutableStateOf(1F) }
    var allowance by remember { mutableStateOf(0) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (shiftViewModel.shiftId != "") {
            shiftViewModel.getShiftById { shift ->
                name = TextFieldValue(shift.shiftName)
                startTime = shift.startTime
                endTime = shift.endTime
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
                    startTime = Time.form(time)
                } else {
                    endTime = Time.form(time)
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Allowance Input
                OutlinedTextField(
                    value = allowance.toString(),
                    onValueChange = { allowance = it.toIntOrNull() ?: 0 },
                    label = { Text("Phụ cấp ca") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                            shiftName = name.text,
                            startTime = startTime,
                            endTime = endTime,
                            groupId = shiftViewModel.groupId
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
