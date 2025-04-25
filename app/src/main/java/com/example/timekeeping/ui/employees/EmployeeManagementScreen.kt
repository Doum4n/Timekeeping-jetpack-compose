package com.example.timekeeping.ui.employees

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Name
import com.example.timekeeping.ui.components.EntityList
import com.example.timekeeping.ui.employees.components.EmployeeCard
import com.example.timekeeping.ui.employees.components.SearchBar
import com.example.timekeeping.ui.employees.list_employees.EmployeePage
import com.example.timekeeping.ui.employees.list_employees.EmployeePagerContent
import com.example.timekeeping.view_models.EmployeeViewModel

@Composable
fun EmployeeManagementScreen(
    viewModel: EmployeeViewModel,
    onBackClick: () -> Unit,
    onMenuItemClick: (MenuItem) -> Unit,
    onEmployeeIdClick: (String) -> Unit,
    onLinkClick: (String) -> Unit
) {
    EmployeeManagementContentScreen(
        groupId = viewModel.groupId,
        employees = viewModel.employees.value,
        unlinkedEmployees = viewModel.unlinkedEmployees.value,
        pendingEmployees = viewModel.pendingEmployees.value,
        onSearch = { searchText -> viewModel.searchEmployeesByName(searchText) },
        onBackClick = onBackClick,
        onMenuItemClick = onMenuItemClick,
        onEmployeeIdClick = { onEmployeeIdClick(it) },
        onLinkClick = { employeeId -> onLinkClick(employeeId) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeManagementContentScreen(
    groupId: String,

    employees: List<Employee>,
    unlinkedEmployees: List<Employee>,
    pendingEmployees: List<Employee>,

    onSearch: (String) -> Unit,
    onBackClick: () -> Unit,
    onMenuItemClick: (MenuItem) -> Unit,

    onEmployeeIdClick: (String) -> Unit,

    onLinkClick: (String) -> Unit
) {
    val searchText = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Danh sách thành viên") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onMenuItemClick(MenuItem.ADD) }) {
                        Icon(Icons.Default.Add, "Add")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            SearchBar(
                searchText = searchText.value,
                onSearch = { onSearch(searchText.value) },
                onTextChanged = {
                    searchText.value = it
                }
            )

            val pages = listOf(
                EmployeePage.Unlinked(
                    unlinkedEmployees,
                    onLinkClick = { employeeId -> onLinkClick(employeeId) }
                ),
                EmployeePage.Members(employees),
                EmployeePage.Approval(
                    pendingEmployees,
                    onAcceptClick = {},
                    onRejectClick = {}
                )
            )

            EmployeePagerContent(
                pages = pages,
                groupId = groupId,
                currentPage = 1, // ví dụ đang ở tab "Thành viên"
                onTabSelected = { pageIndex -> /* xử lý chọn tab */ },
                onEmployeeClick = {onEmployeeIdClick(it)}
            )
        }
    }
}

@Composable
fun UnlinkedEmployeesScreen(unlinkedEmployees: List<Employee>, groupId: String, onClick: (String) -> Unit = {}, onLinkClick: (String) -> Unit = {}) {
    EntityList(
        unlinkedEmployees,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ){
        EmployeeCard(
            employee = it,
            onClick = {employeeId -> onClick(employeeId)},
            onLinkClick = onLinkClick
        )
    }
}

@Composable
fun MembersScreen(employees: List<Employee>, groupId: String, onEmployeeClick: (String) -> Unit) {
    EntityList(
        employees,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ){
        EmployeeCard(
            employee = it,
            onClick = {employeesId -> onEmployeeClick(employeesId)}
        )
    }
}

@Composable
fun ApprovalScreen(pendingEmployees: List<Employee>, groupId: String, onAcceptClick: () -> Unit = {}) {
    EntityList(
        pendingEmployees,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ){
        EmployeeCard(
            employee = it,
            onAcceptClick = onAcceptClick,
            onRejectClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeeManagementScreenPreview() {
    val sampleEmployees = listOf(
        Employee("1", "John Doe",  Name(firstName = "John", lastName = "Doe"), "", ""),
        Employee("2", "Jane Smith", Name(firstName = "Jane", lastName = "Smith"), "", ""),
        Employee("3", "Jim Brown", Name(firstName = "Jim", lastName = "Brown"), "", ""),
    )
    val sampleUnlinkedEmployees = listOf(
        Employee("1", "John Doe",  Name(firstName = "John", lastName = "Doe"), "", ""),
        Employee("2", "Jane Smith", Name(firstName = "Jane", lastName = "Smith"), "", ""),
        Employee("3", "Jim Brown", Name(firstName = "Jim", lastName = "Brown"), "", ""),
    )
    val samplePendingEmployees = listOf(
        Employee("1", "John Doe",  Name(firstName = "John", lastName = "Doe"), "", ""),
        Employee("2", "Jane Smith", Name(firstName = "Jane", lastName = "Smith"), "", ""),
        Employee("3", "Jim Brown", Name(firstName = "Jim", lastName = "Brown"), "", ""),
    )
    EmployeeManagementContentScreen(
        groupId = "1",
        employees = sampleEmployees,
        unlinkedEmployees = sampleUnlinkedEmployees,
        pendingEmployees = samplePendingEmployees,
        onSearch = {},
        onBackClick = {},
        onMenuItemClick = {},
        onEmployeeIdClick = {},
        onLinkClick = {}
    )
}