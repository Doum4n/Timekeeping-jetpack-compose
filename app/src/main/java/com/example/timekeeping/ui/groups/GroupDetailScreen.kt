package com.example.timekeeping.ui.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.timekeeping.ui.calender.CalendarScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupId: String,
    onBackClick: () -> Unit,
    onEmployeeManagementClick: () -> Unit,
    onShiftManagementClick: () -> Unit,
    onCheckInClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Group Management") },
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
                    var expanded by remember { mutableStateOf(false) }
                    Box { // Cần Box làm container
                        // Nút mở menu
                        IconButton(
                            onClick = { expanded = true }
                        ) {
                            Icon(Icons.Default.MoreVert, "Menu")
                        }

                        // Dropdown Menu
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.widthIn(min = 180.dp)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Cài đặt") },
                                onClick = {
                                    expanded = false
                                    onSettingsClick()
                                },
                                leadingIcon = { Icon(Icons.Default.Settings, null) }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Đăng xuất") },
                                onClick = {
                                    expanded = false
                                    // Xử lý đăng xuất
                                },
                                leadingIcon = { Icon(Icons.Default.ExitToApp, null) }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Đảm bảo nội dung không bị che bởi AppBar
                .padding(16.dp)
        ) {
            // Grid of buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // First row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButtonWithLabel(
                        onClick = { onCheckInClick() },
                        icon = Icons.Default.Warning,
                        label = "Chấm công"
                    )
                    IconButtonWithLabel(
                        onClick = { /* Handle button 2 */ },
                        icon = Icons.Default.Warning,
                        label = "Button 2"
                    )
                    IconButtonWithLabel(
                        onClick = { /* Handle button 3 */ },
                        icon = Icons.Default.Warning,
                        label = "Button 3"
                    )
                }

                // Second row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButtonWithLabel(
                        onClick = { onEmployeeManagementClick() },
                        icon = Icons.Default.Warning,
                        label = "Danh sách nhân viên"
                    )
                    IconButtonWithLabel(
                        onClick = { onShiftManagementClick() },
                        icon = Icons.Default.Warning,
                        label = "Quản lý ca"
                    )
                    IconButtonWithLabel(
                        onClick = { /* Handle button 6 */ },
                        icon = Icons.Default.Warning,
                        label = "Button 6"
                    )
                }
            }

            // Calendar fragment replacement
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                CalendarScreen()
            }
        }
    }
}


@Composable
fun IconButtonWithLabel(
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(32.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}