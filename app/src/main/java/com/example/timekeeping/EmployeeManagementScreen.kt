package com.example.timekeeping

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.timekeeping.view_models.EmployeeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EmployeeManagementScreen(
    viewModel: EmployeeViewModel,
    onBackClick: () -> Unit,
    onMenuItemClick: (MenuItem) -> Unit
) {
    val tabs = listOf("Chưa liên kết", "Thành viên", "Xét duyệt")
    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
    val scope = rememberCoroutineScope()

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
                    IconButton(onClick = { onMenuItemClick(MenuItem.SEARCH) }) {
                        Icon(Icons.Default.Search, "Search")
                    }
                    IconButton(onClick = { onMenuItemClick(MenuItem.MORE) }) {
                        Icon(Icons.Default.MoreVert, "More")
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
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
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
                    0 -> UnlinkedEmployeesScreen()
                    1 -> MembersScreen(viewModel)
                    2 -> ApprovalScreen()
                }
            }
        }
    }
}

@Composable
fun UnlinkedEmployeesScreen() {
    // Thay thế bằng nội dung thực tế
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Chưa liên kết")
    }
}

@Composable
fun MembersScreen(viewModel: EmployeeViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
//                    .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        items(viewModel.employees) { employee ->
            EmployeeCard(
                employeeName = employee.name,
                onActionClick = {}
            )
        }
    }
}

@Composable
fun ApprovalScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Xét duyệt")
    }
}

sealed class MenuItem {
    object SEARCH : MenuItem()
    object MORE : MenuItem()
}

@Composable
fun EmployeeCard(
    employeeName: String,
    onActionClick: () -> Unit
) {
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
                    text = employeeName,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "Thông tin bổ sung 1")
                Text(text = "Thông tin bổ sung 2")
            }

            Button(
                onClick = onActionClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cấp quyền")
            }
        }
    }
}
