package com.example.timekeeping

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.ui.admin.components.TopBarClassic
import com.example.timekeeping.utils.QRCodeScannerScreen
import com.example.timekeeping.utils.SessionManager
import com.example.timekeeping.view_models.EmployeeViewModel

@Composable
fun RequestJoinGroupScreen(
    onBackClick: () -> Unit,
    employeeViewModel: EmployeeViewModel = hiltViewModel()
) {

    val currentEmployeeId = SessionManager.getEmployeeId()
    var groupId by remember { mutableStateOf("") }

    Scaffold (
        topBar = {
            TopBarClassic(
                title = "Tham gia nhóm",
                onBackClick = onBackClick
            )
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // QR Code Scanner
            QRCodeScannerScreen { result ->
                if (currentEmployeeId != null) {
                    employeeViewModel.requestJoinGroup(currentEmployeeId, result)
                }
            }

            // Tiêu đề và hướng dẫn
            Text(
                text = "Quét mã QR",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Hoặc",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Nhập mã nhóm",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Nhập mã nhóm
            TextField(
                value = groupId,
                onValueChange = { groupId = it },
                label = { Text("Nhập mã nhóm") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (currentEmployeeId != null) {
                        employeeViewModel.requestJoinGroup(currentEmployeeId, groupId)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gửi yêu cầu")
            }
        }
    }
}