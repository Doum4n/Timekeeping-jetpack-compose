package com.example.timekeeping.ui.employees.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Request
import com.example.timekeeping.models.RequestType
import com.example.timekeeping.ui.components.TopBarClassic
import com.example.timekeeping.ui.components.TopBarWithAddAction
import com.example.timekeeping.utils.formatCurrency
import com.example.timekeeping.view_models.RequestViewModel
import com.example.timekeeping.view_models.SalaryViewModel

@Composable
fun RequestScreen(
    employeeId: String,
    groupId: String,
    onBackClick: () -> Unit,
    onAddRequestClick: () -> Unit,
    onDeleteRequestClick: (Request) -> Unit,

    requestViewModel: RequestViewModel = hiltViewModel()
) {

    var requests by remember { mutableStateOf(listOf<Request>()) }

    LaunchedEffect(Unit) {
        requestViewModel.getRequestById(employeeId, groupId){
            requests = it
        }
    }

    Scaffold(
        topBar = {
            TopBarWithAddAction(
                title = "Yêu cầu",
                onBackClick = onBackClick,
                onAddShiftClick = onAddRequestClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                requests.forEach {
                    RequestItem(it){
                        onDeleteRequestClick(it)
                    }
                }
            }
        }
    }
}

@Composable
fun RequestItem(
    request: Request,
    onDelete: (Request) -> Unit // callback khi nhấn xóa
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { /* handle click if needed */ }
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Yêu cầu ${request.type}",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Lý do: ${request.reason}")
                        Text("Số tiền: ${request.amount.formatCurrency()}")
                        Text("Trạng thái: ${request.status}")
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Tạo lúc: ${request.createdAt.format("dd/MM/yyyy HH:mm")}")
                    }
                }
            }

            IconButton(
                onClick = { onDelete(request) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa yêu cầu",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
