package com.example.timekeeping.ui.employees

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
import com.example.timekeeping.view_models.EmployeeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeManagementScreen(
    viewModel: EmployeeViewModel,
    onBackClick: () -> Unit,
    onMenuItemClick: (MenuItem) -> Unit
) {
    val tabs = listOf("Chưa liên kết", "Thành viên", "Xét duyệt")
    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
    val scope = rememberCoroutineScope()

    var searchText by remember { mutableStateOf("") }

    // Đồng bộ khi pager thay đổi
    LaunchedEffect(pagerState.currentPage) {
        // Không cần thực hiện gì vì TabRow tự động theo pagerState.currentPage
    }

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

        // Lấy bàn phím hiện tại
        val keyboardController = LocalSoftwareKeyboardController.current

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = {searchText = it},
                label = { Text("Tìm kiếm") },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                trailingIcon = {
                    IconButton(onClick = { viewModel.searchEmployeesByName(searchText) }) {
                        Icon(Icons.Default.Search, contentDescription = "Select Start Time")
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.searchEmployeesByName(searchText)
                        keyboardController?.hide() // Ẩn bàn phím khi nhấn Enter/Search
                    }
                ),
                singleLine = true,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage, // Sử dụng trực tiếp pagerState
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {
//                    TabRowDefaults.Divider(
//                        thickness = 2.dp,
//                        color = MaterialTheme.colorScheme.primary
//                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(title) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState, // Sử dụng chung state
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> UnlinkedEmployeesScreen(viewModel)
                    1 -> MembersScreen(viewModel)
                    2 -> ApprovalScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun UnlinkedEmployeesScreen(viewModel: EmployeeViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        items(viewModel.unlinkedEmployees.value) { employee ->
            EmployeeCard(
                employee = employee,
                onLinkClick = {}
            )
        }
    }
}

@Composable
fun MembersScreen(viewModel: EmployeeViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        items(viewModel.employees.value) { employee ->
            EmployeeCard(
                employee = employee,
            )
        }
    }
}

@Composable
fun ApprovalScreen(viewModel: EmployeeViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        items(viewModel.pendingEmployees.value) { employee ->
            EmployeeCard(
                employee = employee,
                isPending = true,
                onAcceptClick = {},
                onRejectClick = {}
            )
        }
    }
}

sealed class MenuItem {
    object ADD : MenuItem()
  //  object MORE : MenuItem()
}

@Composable
fun EmployeeCard(
    employee: Employee,
    isPending: Boolean = false,
    onLinkClick: () -> Unit = {},
    onAcceptClick: () -> Unit = {},
    onRejectClick: () -> Unit = {}
) {

    var salary by remember { mutableStateOf<Double?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(employee.id) {
        // Gọi hàm bất đồng bộ để lấy lương
        try {
            EmployeeViewModel().getSalaryById(employee.id, onSuccess = { fetchedSalary ->
                salary = fetchedSalary
                isLoading = false
            }, onFailure = { exception ->
                errorMessage = "Error: ${exception.message}"
                isLoading = false
            })
        } catch (e: Exception) {
            errorMessage = "Exception: ${e.message}"
            isLoading = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
                Text(
                    text = employee.fullName,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isLoading) {
                    Text(text = "Loading salary...")
                } else {
                    if (salary != null) {
                        Text(text = "${salary} VND")
                    } else if (errorMessage != null) {
                        Text(text = errorMessage ?: "Unknown error")
                    }
                }
                Text(text = "Thông tin bổ sung 2")
            }

            if (isPending) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAcceptClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Chấp nhận")
                    }

                    Button(
                        onClick = onRejectClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Từ chối")
                    }
                }
            }else if (employee.userId.isEmpty()) {
                Button(
                    onClick =  onLinkClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Liên kết")
                }
            }else {
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Xem thông tin")
                }
            }
        }
    }
}