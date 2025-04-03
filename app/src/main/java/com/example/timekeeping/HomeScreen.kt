package com.example.timekeeping

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.timekeeping.models.Group
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
            FloatingActionButton(onClick = {
//                context.startActivity(Intent(context, AddGroupActivity::class.java))
            }) {
                Icon(Icons.Default.Add, contentDescription = "Thêm nhóm")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
        ) {
            // Nút tham gia nhóm
            Button(
                onClick = {
//                    context.startActivity(
//                        Intent(context, ScannerActivity::class.java).apply {
//                            putExtra("type", "JoinWorkGroup")
//                        }
//                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Tham gia công việc nhóm")
            }

            // Danh sách nhóm đã tham gia
            Text(
                text = "Nhóm đã tham gia",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
//                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                items(viewModel.joinedGroups) { group ->
                    GroupItem(group) {
                        navController.navigate( Screen.GroupDetail.createRoute(group.id))
                    }
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
                    .wrapContentHeight()
            ) {
                items(viewModel.createdGroups) { group ->
                    GroupItem(group) {
                        navController.navigate("groupDetail/${group.id}")
                    }
                }
            }

            // Nút chấm công
            Button(
                onClick = {
//                    context.startActivity(
//                        Intent(context, ScannerActivity::class.java).apply {
//                            putExtra("type", "Timekeeping")
//                        }
//                    )
                },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.End)
            ) {
                Text("Chấm công")
            }
        }
    }
}

@Composable
fun GroupItem(group: Group, onClick: () -> Unit) {
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
        }
    }
}