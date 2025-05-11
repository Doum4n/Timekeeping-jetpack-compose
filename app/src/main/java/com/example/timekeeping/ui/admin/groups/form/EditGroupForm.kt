package com.example.timekeeping.ui.admin.groups.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Group
import com.example.timekeeping.ui.admin.components.TopBarClassic
import com.example.timekeeping.ui.admin.groups.showDatePicker
import com.example.timekeeping.view_models.GroupViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EditGroupForm(
    groupId: String = "",
    groupViewModel: GroupViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onSave: (Group) -> Unit = {}
) {
    val context = LocalContext.current
    var groupName by remember { mutableStateOf("") }
    var payday by remember { mutableStateOf<Date?>(null) }
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    // Load dữ liệu khi có groupId
    LaunchedEffect(groupViewModel.groupId) {
        if (groupViewModel.groupId.isNotEmpty()) {
            groupViewModel.getGroupById(groupId) { group ->
                groupName = group?.name ?: ""
                payday = group?.payday
            }
        }
    }

    Scaffold(
        topBar = {
            TopBarClassic(
                title = "Chỉnh sửa nhóm",
                onBackClick = onBackClick,
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Text("Tên nhóm")
            TextField(
                value = groupName,
                onValueChange = { groupName = it },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )

            val paydayText = payday?.let { dateFormatter.format(it) } ?: "Chưa chọn"
            Text("Ngày trả lương: $paydayText", modifier = Modifier.padding(16.dp))

            Button(
                onClick = {
                    showDatePicker(context) { selectedDate ->
                        payday = selectedDate
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Chọn ngày")
            }

            Button(
                onClick = {
                    val group = Group(
                        id = groupViewModel.groupId,
                        name = groupName,
                        payday = payday
                    )
                    onSave(group)
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                Text("Lưu")
            }
        }
    }
}
