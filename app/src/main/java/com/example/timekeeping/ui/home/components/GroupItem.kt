package com.example.timekeeping.ui.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timekeeping.models.Group
import java.text.SimpleDateFormat

@Composable
fun GroupItem(group: Group, onClick: () -> Unit, onCheckInClick: () -> Unit = {}) {
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
                text = "2 thành viên",
                style = MaterialTheme.typography.bodyMedium
            )

            group.payday?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ngày thanh toán: ${SimpleDateFormat("dd/MM/yyyy").format(it)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onCheckInClick,
                modifier = Modifier.align(Alignment.Start),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text("Châm công")
            }
        }
    }
}
