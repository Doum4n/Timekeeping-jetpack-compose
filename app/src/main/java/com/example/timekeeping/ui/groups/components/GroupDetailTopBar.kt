package com.example.timekeeping.ui.groups.components

import android.app.AlertDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.timekeeping.ui.employees.components.SimpleDialogS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailTopBar(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onDelete: () -> Unit
) {

    var showDialog = remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("Group Management") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        actions = {
            var expanded by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.widthIn(min = 180.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text("Chỉnh sửa công việc") },
                        onClick = {
                            expanded = false
                            onSettingsClick()
                        },
                        leadingIcon = { Icon(Icons.Default.Settings, null) }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Xóa nhóm") },
                        onClick = {
                            showDialog.value = showDialog.value.not()
                        },
                        leadingIcon = { Icon(Icons.Default.ExitToApp, null) }
                    )

                    if (showDialog.value){
                        SimpleDialogS(
                            title = "Thông báo",
                            question = "Bạn có chắc chắn muốn xóa nhóm này không này không?",
                            onConfirm = {
                                onDelete()
                                onBackClick()
                            },
                            onDismiss = {
                                showDialog.value = false
                            }
                        )
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun PreviewGroupDetailTopBar() {
    GroupDetailTopBar(
        onBackClick = { /*TODO*/ },
        onSettingsClick = { /*TODO*/ },
        onDelete = { /*TODO*/ }
    )
}

