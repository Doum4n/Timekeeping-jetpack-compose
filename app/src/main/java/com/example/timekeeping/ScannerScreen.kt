package com.example.timekeeping

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timekeeping.utils.QRCodeScannerScreen

@Composable
fun ScanneScreen() {
    var scannedResult by remember { mutableStateOf<String?>(null) }

    if (scannedResult == null) {
        QRCodeScannerScreen { result ->
            scannedResult = result
        }
    } else {
        Column(
            modifier = Modifier.height(150.dp).width(150.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Kết quả: $scannedResult", style = MaterialTheme.typography.bodyLarge)
            Button(onClick = { scannedResult = null }) {
                Text("Quét lại")
            }
        }
    }
}