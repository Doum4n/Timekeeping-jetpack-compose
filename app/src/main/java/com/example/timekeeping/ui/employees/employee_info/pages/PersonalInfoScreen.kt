package com.example.timekeeping.ui.employees.employee_info.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Name

@Composable
fun PersonalInfoScreen(
    employee: Employee,
    onEmployeeChange: (Employee) -> Unit = {},
) {
    // Lắng nghe thay đổi từ employee và đồng bộ lại state
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // Khi employee thay đổi (do load từ viewmodel), cập nhật các state tương ứng
    LaunchedEffect(employee) {
        fullName = employee.name.fullName
        email = employee.email
        phone = employee.phone
        address = employee.address
    }

    // Khi người dùng sửa form, cập nhật dữ liệu cho bên ngoài
    LaunchedEffect(fullName, email, phone, address) {
        onEmployeeChange(
            employee.copy(
                name = Name().form(fullName),
                email = email,
                phone = phone,
                address = address
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Họ tên") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Số điện thoại") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Địa chỉ") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
