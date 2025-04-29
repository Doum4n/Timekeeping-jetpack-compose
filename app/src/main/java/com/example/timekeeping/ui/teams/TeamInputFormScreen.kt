package com.example.timekeeping.ui.teams

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Team
import com.example.timekeeping.utils.convertToReference
import com.example.timekeeping.view_models.EmployeeViewModel
import com.example.timekeeping.view_models.TeamViewModel
import com.google.firebase.firestore.DocumentReference


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamInputFormScreen(
    teamId: String = "",
    viewModel: EmployeeViewModel = hiltViewModel(),
    onSubmit: (Team) -> Unit,
    onCancel: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var employees by remember { mutableStateOf<List<DocumentReference>>(emptyList()) }

    LaunchedEffect (teamId) {
        if(teamId.isBlank()) return@LaunchedEffect
        else {
            viewModel.loadTeamById(teamId) { team ->
                name = team.name
                description = team.description
                employees = team.members
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tạo tổ làm việc") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Tên tổ") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Mô tả") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Text("Chọn thành viên")
            LazyColumn {
                items(viewModel.employees.value) { employee ->
                    EmployeeItem(
                        employee,
                        employees = employees,
                        onCheckedChange = { employeeId -> 
                            if (employees.contains(employeeId)) {
                                employees -= employeeId
                            } else {
                                employees += employeeId
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(viewModel.unlinkedEmployees.value) { employee ->
                    EmployeeItem(
                        employee,
                        employees = employees,
                        onCheckedChange = { employeeId ->
                            if (employees.contains(employeeId)) {
                                employees -= employeeId
                            } else {
                                employees += employeeId
                            }
                        }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onSubmit(Team(
                        id = teamId,
                        name = name,
                        groupId = viewModel.groupId,
                        description = description,
                        members = employees
                    )) },
                    enabled = name.isNotBlank()
                ) {
                    Text("Lưu")
                }
            }
        }
    }
}

@Composable
fun EmployeeItem(
    employee: Employee,
    employees: List<DocumentReference>,
    onCheckedChange: (DocumentReference) -> Unit
) {
    val employeeRef = employee.id.convertToReference("employees")
    val isChecked = employees.contains(employeeRef)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = employee.name.fullName,
                style = MaterialTheme.typography.titleMedium
            )
            Checkbox(
                checked = isChecked,
                onCheckedChange = { checked ->
                    onCheckedChange(employeeRef)
                }
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewTeamInputFormScreen() {
//    TeamInputFormScreen(
//        onSubmit = {
//                   name, description, employees -> println("Name: $name, Description: $description, Employees: $employees") },
//    )
//}