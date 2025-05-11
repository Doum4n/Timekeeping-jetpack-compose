package com.example.timekeeping.ui.admin.account

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.ui.admin.components.TopBarClassic
import com.example.timekeeping.utils.generateQRCode
import com.example.timekeeping.view_models.EmployeeViewModel

@Composable
fun MyQRCodeScreen(
    employeeId: String,
    onBack: () -> Unit,
    employeeViewModel: EmployeeViewModel = hiltViewModel()
) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    LaunchedEffect(employeeId) {
        employeeViewModel.getEmployeeById(employeeId, onSuccess = {
            name = it.name.fullName
            email = it.email
        }, onFailure = {
            Log.e("MyQRCodeScreen", "Error loading employee", it)
        })
    }

    Scaffold (
        topBar = {
            TopBarClassic(
                title = "Mã QR của bạn",
                onBackClick = onBack
            )
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hiển thị QR Code
            QRCodeImage(employeeId)

            // Tên
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Tên",
                    style = MaterialTheme.typography.labelLarge
                )
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Email
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Email",
                    style = MaterialTheme.typography.labelLarge
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

    }
}

@Composable
fun QRCodeImage(text: String) {
    val bitmap = generateQRCode(text, 500)
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "QR Code"
    )
}