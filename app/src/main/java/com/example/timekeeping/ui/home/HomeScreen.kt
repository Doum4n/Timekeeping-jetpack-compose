package com.example.timekeeping.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.timekeeping.models.Group
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.admin.employees.components.SearchBar
import com.example.timekeeping.ui.home.components.GroupList
import com.example.timekeeping.ui.home.components.HomeFloatingActionButton
import com.example.timekeeping.ui.home.components.HomeTopAppBar
import com.example.timekeeping.utils.SessionManager
import com.example.timekeeping.view_models.EmployeeViewModel
import com.example.timekeeping.view_models.GroupViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: GroupViewModel = hiltViewModel(),
    employeeViewModel: EmployeeViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val employeeId = SessionManager.getEmployeeId()

    val joinedGroups by viewModel.joinedGroups
    val createdGroups by viewModel.createdGroups

    LaunchedEffect(Unit) {
        viewModel.loadGroups()
    }

    Scaffold(
        topBar = { HomeTopAppBar(navController) },
        floatingActionButton = { HomeFloatingActionButton(navController) }
    ) { paddingValues ->
        val keyboardController = LocalSoftwareKeyboardController.current
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            item {
                // Search bar
                SearchBar(
                    searchText = searchQuery,
                    onTextChanged = { searchQuery = it },
                    onSearch = {
                        viewModel.searchGroupsByName(searchQuery)
                        keyboardController?.hide()
                    }
                )
            }

            item {
                // Joined groups
                Text(
                    text = "Nhóm đã tham gia",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
            }

            item {
                GroupList(
                    groups = joinedGroups,
                    onItemClick = { group ->

                        employeeViewModel.getRole(employeeId.toString(), group.id) { role ->
                            SessionManager.setRole(role)

                            if (role == "ADMIN") {
                                navController.navigate(Screen.GroupDetail.createRoute(group.id))
                            } else if (role == "EMPLOYEE") {
                                navController.navigate(
                                    Screen.EmployeeDetail.createRoute(
                                        group.id,
                                        employeeId.toString()
                                    )
                                )
                            }
                        }
                    },
                    onCheckInClick = { group ->
                        navController.navigate(
                            Screen.CheckIn.createRoute(
                                group.id
                            )
                        )
                    }
                )
            }

            item {
                // Created groups
                Text(
                    text = "Nhóm đã tạo",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
                GroupList(
                    groups = createdGroups,
                    onItemClick = { group ->

                        employeeViewModel.getRole(employeeId.toString(), group.id) { role ->
                            SessionManager.setRole(role)

                            if (role == "ADMIN") {
                                navController.navigate(Screen.GroupDetail.createRoute(group.id))
                            } else if (role == "EMPLOYEE") {
                                navController.navigate(
                                    Screen.EmployeeDetail.createRoute(
                                        group.id,
                                        employeeId.toString()
                                    )
                                )
                            }
                        }
                    },
                    onCheckInClick = { group ->
                        navController.navigate(
                            Screen.CheckIn.createRoute(
                                group.id
                            )
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    val groups = listOf(
        Group(
            id = "1",
            name = "Nhóm 1",
            payday = java.util.Date()
        ),
        Group(
            id = "2",
            name = "Nhóm 2",
            payday = java.util.Date()
        ),
        Group(
            id = "3",
            name = "Nhóm 3",
            payday = java.util.Date()
        )
    )

    Scaffold(
        topBar = { HomeTopAppBar(rememberNavController()) },
        floatingActionButton = { HomeFloatingActionButton(rememberNavController()) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            GroupList(groups = groups, onItemClick = {}, onCheckInClick = {})
        }
    }
}

