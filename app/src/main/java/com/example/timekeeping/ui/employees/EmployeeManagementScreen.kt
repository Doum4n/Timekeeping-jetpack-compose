package com.example.timekeeping.ui.employees

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timekeeping.R
import com.example.timekeeping.models.Employee
import com.example.timekeeping.ui.components.EntityList
import com.example.timekeeping.ui.employees.components.SearchBar
import com.example.timekeeping.view_models.EmployeeViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeManagementScreen(
    groupId: String,
    viewModel: EmployeeViewModel,
    onBackClick: () -> Unit,
    onMenuItemClick: (MenuItem) -> Unit
) {

    EmployeeManagementScreen(
        groupId = groupId,
        employees = viewModel.employees.value,
        unlinkedEmployees = viewModel.unlinkedEmployees.value,
        pendingEmployees = viewModel.pendingEmployees.value,
        onSearch = { searchText -> viewModel.searchEmployeesByName(searchText) },
        onBackClick = onBackClick,
        onMenuItemClick = onMenuItemClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeManagementScreen(
    groupId: String,

    employees: List<Employee>,
    unlinkedEmployees: List<Employee>,
    pendingEmployees: List<Employee>,

    onSearch: (String) -> Unit,
    onBackClick: () -> Unit,
    onMenuItemClick: (MenuItem) -> Unit
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
                EmployeePage.Unlinked(unlinkedEmployees),
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
                onTabSelected = { pageIndex -> /* xử lý chọn tab */ }
            )
        }
    }
}

@Composable
fun UnlinkedEmployeesScreen(unlinkedEmployees: List<Employee>, groupId: String) {
    EntityList(
        unlinkedEmployees,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ){
        EmployeeCard(
            groupId = groupId,
            employee = it,
            onLinkClick = {}
        )
    }
}

@Composable
fun MembersScreen(employees: List<Employee>, groupId: String) {
    EntityList(
        employees,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ){
        EmployeeCard(
            groupId = groupId,
            employee = it,
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
            groupId = groupId,
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
        Employee("1", "John Doe", "john.doe@example.com", "", "1"),
        Employee("2", "Jane Smith", "jane.smith@example.com", "", "1"),
        Employee("3", "Peter Jones", "peter.jones@example.com", "", "1")
    )
    val sampleUnlinkedEmployees = listOf(
        Employee("4", "Alice Brown", "alice.brown@example.com", "", ""),
        Employee("5", "Bob White", "bob.white@example.com", "", "")
    )
    val samplePendingEmployees = listOf(
        Employee("6", "Charlie Green", "charlie.green@example.com", "", ""),
        Employee("7", "David Black", "david.black@example.com", "", "")
    )
    EmployeeManagementScreen(
        groupId = "1",
        employees = sampleEmployees,
        unlinkedEmployees = sampleUnlinkedEmployees,
        pendingEmployees = samplePendingEmployees,
        onSearch = {},
        onBackClick = {},
        onMenuItemClick = {}
    )
}