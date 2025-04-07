package com.example.timekeeping.ui.employees

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Status

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeInputForm(
    onBackClick: () -> Unit,
    onSave: (List<Employee>) -> Unit,
) {
    val employees = remember { mutableStateListOf(Employee(
        status = Status.PENDING,
        isCreator = false
    )) }

    // Scaffold chứa TopAppBar, BottomBar, và phần nội dung chính
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Employee Form") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { onSave(employees, ) }) {
                        Icon(Icons.Filled.Done, contentDescription = "Action save")
                    }
                }
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                items(employees) { employee ->
                    InputFormCard(
                        employee = employee,
                        onEmployeeChange = { updatedEmployee ->
                            val index = employees.indexOf(employee)
                            if (index != -1) {
                                employees[index] = updatedEmployee
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Button(
                        onClick = { employees.add(Employee(
                            status = Status.PENDING,
                            isCreator = false
                        )) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Employee")
                        Text("Add Employee")
                    }
                }
            }
        },
    )
}

@Composable
fun InputFormCard(
    employee: Employee,
    onEmployeeChange: (Employee) -> Unit,
) {
    // State để lưu trữ dữ liệu người dùng nhập
    var fullName by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }

    var salaryOptions = listOf("Giờ", "Ca", "Ngày")
    var selectedSalary by remember { mutableStateOf(salaryOptions.first()) }
    var expanded by remember { mutableStateOf(false) }

    var roleOptions = listOf("Admin", "Quản lý", "Nhân viên")
    var selectedRole by remember { mutableStateOf(roleOptions.first()) }
    var expandedRole by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Input Full Name
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        onEmployeeChange(employee.copy(fullName = it))
                    },
                    label = { Text("Tên") },
                    modifier = Modifier.weight(1f)
                )

                // Role Dropdown
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = selectedRole,
                        onValueChange = {
                            selectedRole = it
                            onEmployeeChange(employee.copy(role = it))
                        },
                        label = { Text("Chức vụ") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { expandedRole = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Icon")
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = expandedRole,
                        onDismissRequest = { expandedRole = false },
                    ) {
                        roleOptions.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role) },
                                onClick = {
                                    expandedRole = false
                                    selectedRole = role
                                    onEmployeeChange(employee.copy(role = role))
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Salary Input and Dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Salary Dropdown
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = selectedSalary,
                        onValueChange = {
                            selectedSalary = it
                            onEmployeeChange(employee.copy(salaryType = it))
                        },
                        label = { Text("Cách tính lương") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Icon")
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        salaryOptions.forEach { salary ->
                            DropdownMenuItem(
                                text = { Text(salary) },
                                onClick = {
                                    expanded = false
                                    selectedSalary = salary
                                    onEmployeeChange(employee.copy(salaryType = salary))
                                }
                            )
                        }
                    }
                }

                // Input Salary
                OutlinedTextField(
                    value = salary,
                    onValueChange = {
                        salary = it
                        onEmployeeChange(employee.copy(salary = it.toInt()))
                    },
                    label = { Text("Lương") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}