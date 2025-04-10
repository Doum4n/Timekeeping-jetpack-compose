package com.example.timekeeping.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.timekeeping.ui.employees.components.SearchBar
import com.example.timekeeping.ui.home.components.GroupList
import com.example.timekeeping.ui.home.components.HomeFloatingActionButton
import com.example.timekeeping.ui.home.components.HomeTopAppBar
import com.example.timekeeping.view_models.GroupViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: GroupViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = { HomeTopAppBar(navController) },
        floatingActionButton = { HomeFloatingActionButton(navController) }
    ) { paddingValues ->
        val keyboardController = LocalSoftwareKeyboardController.current
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            val joinedGroups by viewModel.joinedGroups

            // Search bar
            SearchBar(
                searchText = searchQuery,
                onTextChanged = { searchQuery = it },
                onSearch = {
                    viewModel.searchGroupsByName(searchQuery)
                    keyboardController?.hide()
                }
            )

            // Joined groups
            Text(
                text = "Joined Groups",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
            GroupList(
                groups = joinedGroups,
                onItemClick = { group -> navController.navigate(Screen.GroupDetail.createRoute(group.id)) },
                onCheckInClick = { group -> navController.navigate(Screen.CheckIn.createRoute(group.id)) }
            )

            // Created groups
            Text(
                text = "Created Groups",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )

            // Add LazyColumn for created groups here
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

