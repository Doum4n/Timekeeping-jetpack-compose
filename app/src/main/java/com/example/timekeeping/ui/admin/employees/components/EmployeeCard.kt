package com.example.timekeeping.ui.admin.employees.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.timekeeping.R
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Name
import com.example.timekeeping.view_models.SalaryViewModel

@Composable
fun EmployeeCard(
    groupId: String = "",
    employee: Employee,
    isPending: Boolean = false,
    onClick: (String) -> Unit = {},
    onLinkClick: (String) -> Unit = {},
    onAcceptClick: () -> Unit = {},
    onRejectClick: () -> Unit = {},
    salaryViewModel: SalaryViewModel = hiltViewModel()
) {

    var salary by remember { mutableStateOf<Double?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var salaryType by remember { mutableStateOf("") }

    LaunchedEffect(employee.id) {
        salaryViewModel.getSalaryById(groupId, employee.id) {
            salary = it?.salary?.toDouble()
            salaryType = it?.salaryType ?: ""
            isLoading = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // --- Avatar + Name + Settings Icon ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(employee.avatarUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = employee.name.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { onClick(employee.id) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            }

            // --- Salary Info / Error / Loading ---
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                when {
                    isLoading -> Text("Đang tải lương...", style = MaterialTheme.typography.bodyMedium)
                    salary != null -> Text("${salary} VND", style = MaterialTheme.typography.bodyMedium)
                    errorMessage != null -> {
                        Text(errorMessage ?: "Lỗi không xác định", color = Color.Red)
                        Log.e("EmployeeCard", errorMessage ?: "Unknown error")
                    }
                }
                Text("Cách tính lương: $salaryType", style = MaterialTheme.typography.bodySmall)
            }

            // --- Action Buttons ---
            when {
                isPending -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onAcceptClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Chấp nhận")
                        }
                        Button(
                            onClick = onRejectClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Từ chối")
                        }
                    }
                }

                employee.userId.isEmpty() -> {
                    Button(
                        onClick = { onLinkClick(employee.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Liên kết")
                    }
                }

                else -> {
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Xem thông tin")
                    }
                }
            }
        }
    }

}


class EmployeePreviewParameterProvider : androidx.compose.ui.tooling.preview.PreviewParameterProvider<Employee> {
    override val values: Sequence<Employee> = sequenceOf(
        Employee(
            id = "1",
            name = Name(firstName = "Nguyễn Văn", lastName = "A"),
            userId = "123"
        ),
        Employee(
            id = "2",
            name = Name(firstName = "Nguyễn Văn", lastName = "B"),
            userId = ""
        ),

    )
}

@Preview(showBackground = true)
@Composable
fun EmployeeCardPreview(
    @PreviewParameter(EmployeePreviewParameterProvider::class) employee: Employee
) {
    EmployeeCard(employee = employee, isPending = true)

}