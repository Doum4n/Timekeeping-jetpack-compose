package com.example.timekeeping.ui.employees.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.timekeeping.R
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Name

@Composable
fun EmployeeCard(
    employee: Employee,
    isPending: Boolean = false,
    onClick: (String) -> Unit = {},
    onLinkClick: (String) -> Unit = {},
    onAcceptClick: () -> Unit = {},
    onRejectClick: () -> Unit = {}
) {

    var salary by remember { mutableStateOf<Double?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(employee.id) {

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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row (
                    modifier = Modifier
                        .size(48.dp)
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        text = employee.name.fullName,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                IconButton(
                    onClick = {onClick(employee.id)},
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isLoading) {
                    Text(text = "Loading salary...")
                } else {
                    if (salary != null) {
                        Text(text = "${salary} VND")
                    } else if (errorMessage != null) {
                        Text(text = errorMessage ?: "Unknown error")
                        Log.e("EmployeeCard", errorMessage ?: "Unknown error")
                    }
                }
                Text(text = "Thông tin bổ sung 2")
            }

            if (isPending) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
            }else if (employee.userId.isEmpty()) {
                Button(
                    onClick =  {onLinkClick(employee.id)},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Liên kết")
                }
            }else {
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