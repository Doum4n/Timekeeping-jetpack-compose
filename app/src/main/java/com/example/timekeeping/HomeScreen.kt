package com.example.timekeeping

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.timekeeping.models.Group
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.utils.NotificationButton
import com.example.timekeeping.view_models.GroupViewModel
import com.google.firebase.FirebaseApp
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: GroupViewModel
) {
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }

    FirebaseApp.initializeApp(context)

    // Load data khi màn hình hiển thị
    LaunchedEffect(Unit) {
        viewModel.loadGroups()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trang chủ") },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("groupForm")
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Thêm ca làm")
                    }
                }
            )
        },
        floatingActionButton = {
            var expanded by remember { mutableStateOf(false) }

            // Sử dụng Box làm container
            Box {
                // FloatingActionButton làm anchor
                FloatingActionButton(
                    onClick = { expanded = true } // Mở menu khi click
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }

                // DropdownMenu phải ở cùng cấp với FAB trong Box
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.widthIn(min = 200.dp),
                    offset = DpOffset(x = (-5).dp, y = (-180).dp)
                ) {
                    DropdownMenuItem(
                        text = { Text("Thêm nhóm") },
                        onClick = {
                            expanded = false
                            // Xử lý thêm nhóm
                        },
                        leadingIcon = { Icon(Icons.Default.Warning, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Thêm tham gia nhóm") },
                        onClick = {
                            expanded = false
                            navController.navigate(Screen.RequestJoinGroup.route)
                        },
                        leadingIcon = { Icon(Icons.Default.Warning, null) }
                    )
                }
            }
        },
    ) { paddingValues ->
        // Lấy bàn phím hiện tại
        val keyboardController = LocalSoftwareKeyboardController.current

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Tìm kiếm nhóm") },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                trailingIcon = {
                    IconButton(onClick = { viewModel.searchGroupsByName(searchQuery) }) {
                        Icon(Icons.Default.Search, contentDescription = "Select Start Time")
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search // Hoặc ImeAction.Done nếu bạn thích
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.searchGroupsByName(searchQuery)
                        keyboardController?.hide() // Ẩn bàn phím khi nhấn Enter/Search
                    }
                ),
                singleLine = true,
                maxLines = 1
            )

            // Danh sách nhóm đã tham gia
            Text(
                text = "Nhóm đã tham gia",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
//                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                items(viewModel.joinedGroups.value) { group ->
                    GroupItem(
                        group,
                        onClick = { navController.navigate( Screen.GroupDetail.createRoute(group.id)) },
                        onCheckInClick = { navController.navigate( Screen.CheckIn.createRoute(group.id)) }
                    )
                }
            }

            // Danh sách nhóm đã tạo
            Text(
                text = "Nhóm đã tạo",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                items(viewModel.createdGroups.value) { group ->
                    GroupItem(
                        group,
                        onClick = { navController.navigate( Screen.GroupDetail.createRoute(group.id)) },
                        onCheckInClick = { navController.navigate( Screen.CheckIn.createRoute(group.id)) }
                    )
                }
            }

        }
    }
}

@Composable
fun GroupItem(group: Group, onClick: () -> Unit, onCheckInClick: () -> Unit = {}) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "? thành viên",
                style = MaterialTheme.typography.bodyMedium
            )
            group.payday?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ngày thanh toán: ${SimpleDateFormat("dd/MM/yyyy").format(it)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onCheckInClick,
                modifier = Modifier.align(Alignment.Start),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text("Châm công")
            }
        }
    }
}