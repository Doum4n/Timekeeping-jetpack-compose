package com.example.timekeeping.ui.shifts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timekeeping.models.Shift
import com.example.timekeeping.view_models.ShiftViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftManagementScreen(
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit = {},
    onDeleteClick: (String) -> Unit = {},
    onAddShiftClick: () -> Unit = {},
    viewModel: ShiftViewModel,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ca công việc") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onAddShiftClick() }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // Đảm bảo padding được áp dụng đúng
            contentPadding = PaddingValues(16.dp)
        ) {
            items(viewModel.shifts.value) { shift ->
                ShiftItem(
                    shift,
                    onEditClick = { onEditClick(shift.id) },
                    onDeleteClick = { onDeleteClick(shift.id) }
                )
            }
        }
    }
}


@Composable
fun ShiftItem(
    shift: Shift,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.Start
        ){
            Text(
                text = shift.shiftName,
                modifier = Modifier.padding(0.dp).weight(1f)
            )
            Icon(
                Icons.Default.Edit,
                contentDescription = "Edit",
                modifier = Modifier.padding(end = 8.dp).clickable {
                    onEditClick()
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                Icons.Default.Delete, contentDescription = "Delete",
                modifier = Modifier.padding(end = 8.dp).clickable {
                    onDeleteClick()
                }
            )
        }

        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "${shift.startTime} - ${shift.endTime}")
                Text(text = "Hệ số: ${shift.coefficient}")
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Phụ cấp: ${shift.allowance}")
                Text(text = "Trạng thái")
            }
        }
    }
}
