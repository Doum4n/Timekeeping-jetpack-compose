package com.example.timekeeping.ui.groups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.intellij.lang.annotations.JdkConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSettingsScreen(
    groupId: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Cài đặt nhóm", color = Color.White)
                        Text("Tháng hiện tại", color = Color.Gray, fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                // Để tạm
                FeatureIcon("Xuất Excel", Icons.Default.Warning)
                FeatureIcon("Quản lý lợi nhuận", Icons.Default.Warning)
                FeatureIcon("Kiểm quỹ", Icons.Default.Warning)
                FeatureIcon("Chi tiêu cá nhân", Icons.Default.Warning)
            }

            HorizontalDivider(
                color = Color.Gray.copy(alpha = 0.3f),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Để tạm
            val settingsList = listOf(
                "Chấm công WiFi",
                "Chấm công vị trí",
                "Chấm công QR",
                "Chụp ảnh khi vào ca, ra ca",
                "Thời gian chấm công",
                "Ngày chốt lương",
                "Phụ cấp có điều kiện",
                "Trừ lương tự động",
                "Phạt tiền nếu quên Ra ca",
                "Loại tiền tệ",
                "Xóa nhóm"
            )

            SectionTitle("Cài đặt của nhóm")
            Card(
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            ) {
                LazyColumn {
                    items(settingsList) { item ->
                        SettingItem(title = item)
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureIcon(title: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = { /* Handle click */ },
            //modifier = Modifier.padding(8.dp)
        ){
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp)
            )
        }
        Text(
            text = title,
            color = Color.Black,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.width(80.dp)
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color.Black,
        fontSize = 16.sp,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun SettingItem(title: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        val color = if (title == "Xóa nhóm") Color.Red else Color.Black
        Text(text = title, color = color)
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
    }
}