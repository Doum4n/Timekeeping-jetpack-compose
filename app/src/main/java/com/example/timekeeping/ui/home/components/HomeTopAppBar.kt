package com.example.timekeeping.ui.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.timekeeping.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(navController: NavController) {
    TopAppBar(
        title = { Text("Trang chủ") },
//        actions = {
//            IconButton(onClick = {
//                navController.navigate("groupForm")
//            }) {
//                Icon(Icons.Default.Add, contentDescription = "")
//            }
//        }
    )
}

@Composable
fun HomeFloatingActionButton(navController: NavController) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        FloatingActionButton(
            onClick = { expanded = true }
        ) {
            Icon(Icons.Default.Menu, contentDescription = "Menu")
        }

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
                    navController.navigate("groupForm")
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
}
