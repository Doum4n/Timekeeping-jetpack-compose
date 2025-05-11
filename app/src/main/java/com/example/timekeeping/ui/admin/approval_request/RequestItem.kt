package com.example.timekeeping.ui.admin.approval_request

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Request
import com.example.timekeeping.utils.formatCurrency
import com.example.timekeeping.view_models.EmployeeViewModel

@Composable
fun RequestItem(
    request: Request,
    onAccept: (Request) -> Unit,
//    rejectReason: String,
//    onRejectReason: (String) -> Unit,
    employeeViewModel: EmployeeViewModel = hiltViewModel()
) {
    var reject by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var rejectReason by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        employeeViewModel.getName(request.employeeId, {
            name = it
        }, {
            name = "Không tìm thấy nhân viên"
        })
    }

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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Yêu cầu: ${request.type}",
                style = MaterialTheme.typography.titleMedium
            )

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
                    if (request.status == "Chờ duyệt")
                        Text("Trạng thái: ${request.status}")
                    else
                        Text("Trạng thái: ${request.status} (${request.reason})")
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text("Tạo lúc: ${request.createdAt.format("dd/MM/yyyy HH:mm")}")
                }
            }

            if (request.status == "Chờ duyệt") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { reject = !reject }) {
                        Text(if (reject) "Đóng từ chối" else "Từ chối")
                    }

                    Button(onClick = {
                        if (reject)
                            onAccept(request.copy(status = "Từ chối", reason = rejectReason))
                        else
                            onAccept(request.copy(status = "Đã duyệt"))
                    }) {
                        Text("Duyệt")
                    }
                }
            }

            if (reject) {
                TextField(
                    value = rejectReason,
                    onValueChange = { rejectReason = it },
                    label = { Text("Lý do từ chối") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}