package com.example.timekeeping.ui.admin.employees.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Request
import com.example.timekeeping.models.RequestType
import com.example.timekeeping.ui.admin.components.TopBarClassic
import com.example.timekeeping.view_models.SalaryViewModel

@Composable
fun RequestInputScreen(
    employeeId: String,
    groupId: String,
    onSendRequestAdvance: (Request) -> Unit,
    onBackClick: () -> Unit,
    salaryViewModel: SalaryViewModel = hiltViewModel()
) {
    var reason by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(RequestType.ADVANCE_SALARY.displayName) }

    Scaffold(
        topBar = {
            TopBarClassic(
                title = "Yêu cầu ứng lương",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Lý do ứng lương")
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Nhập lý do") }
            )

            Text("Loại yêu cầu")
            Box {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = {},
                    label = { Text("Loại") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true },
                    enabled = false,
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    RequestType.entries.map { it.displayName }.forEach {
                        DropdownMenuItem(onClick = {
                            selectedType = it
                            expanded = false
                        }, text = { Text(it) })
                    }
                }
            }

            Text("Số tiền ứng")
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                placeholder = { Text("Nhập số tiền") }
            )

            Button(
                onClick = {
                    val safeAmount = amount.toIntOrNull() ?: 0
                    onSendRequestAdvance(
                        Request(
                            employeeId = employeeId,
                            groupId = groupId,
                            reason = reason,
                            amount = safeAmount,
                            type = selectedType
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gửi yêu cầu")
            }
        }
    }
}
