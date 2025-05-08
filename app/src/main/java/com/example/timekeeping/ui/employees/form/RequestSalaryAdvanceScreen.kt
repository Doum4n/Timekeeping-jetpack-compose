package com.example.timekeeping.ui.employees.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.timekeeping.ui.components.TopBarClassic

@Composable
fun RequestSalaryAdvanceScreen(
    onSendRequest: () -> Unit,
    onBackClick: () -> Unit
) {

    var reason by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBarClassic(
                title = "Request Salary Advance",
                onBackClick = onBackClick
            )
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row{
                Text("Lý do ứng lương")
                OutlinedTextField(
                    value = reason,
                    onValueChange = {reason = it}
                )
            }

            Row{
                Text("Số tiền ứng lương")
                OutlinedTextField(
                    value = amount,
                    onValueChange = {amount = it}
                )
            }

            Button(onClick = { /*TODO*/ }) {
                Text("Gửi yêu cầu")
            }
        }
    }
}