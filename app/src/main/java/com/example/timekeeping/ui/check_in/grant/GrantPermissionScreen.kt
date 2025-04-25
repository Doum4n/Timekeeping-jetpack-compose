package com.example.timekeeping.ui.check_in.grant

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.ScanneScreen
import com.example.timekeeping.ui.components.TopBarClassic
import com.example.timekeeping.utils.QRCodeScannerScreen
import com.example.timekeeping.view_models.EmployeeViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrantPermissionScreen(
    groupId: String,
    employeeId: String,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    onBackClick: () -> Unit,
    employeeViewModel: EmployeeViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val roleOptions = listOf("Admin", "Quản lý", "Nhân viên")
    var selectedRole by remember { mutableStateOf(roleOptions.first()) }
    var expanded by remember { mutableStateOf(false) }

    var scannedResult by remember { mutableStateOf<String?>(null) }

    LaunchedEffect (Unit) {
        employeeViewModel.getEmployeeById(
            employeeId,
            onSuccess = { employee ->
                name = employee.name.fullName
                email = employee.email
            },
            onFailure = { exception ->
                // Handle error
            }
        )
    }

    Scaffold(
        topBar = {
            TopBarClassic(
                title = "Liên kết tài khoản",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            if (scannedResult == null) {
                QRCodeScannerScreen { result ->
                    scannedResult = result
                }
            }else{
                Button(
                    onClick = { scannedResult = null }
                ) {
                    Text("Quét lại")
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Tên nhân viên") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = scannedResult ?: "",
                onValueChange = {},
                label = { Text("Mã người dùng") },
                modifier = Modifier.fillMaxWidth(),
                //enabled = false // Không cho sửa
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedRole,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Chức vụ") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    roleOptions.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role) },
                            onClick = {
                                selectedRole = role
                                expanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    employeeViewModel.grantPermission(groupId, employeeId, scannedResult)
                    onBackClick()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Liên kết tài khoản")
            }
        }
    }
}
