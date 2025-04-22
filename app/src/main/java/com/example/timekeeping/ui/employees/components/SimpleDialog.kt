package com.example.timekeeping.ui.employees.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SimpleDialogS(
    title: String,
    question: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(title) },
            text = { Text(question) },
            confirmButton = {
                Text(
                    "Đồng ý",
                    modifier = Modifier
                        .clickable {
                            onConfirm()
                            showDialog = false
                        }
                        .padding(8.dp)
                )
            },
            dismissButton = {
                Text(
                    "Hủy",
                    modifier = Modifier
                        .clickable {
                            onDismiss()
                            showDialog = false
                        }
                        .padding(8.dp)
                )
            }
        )
    }
}
