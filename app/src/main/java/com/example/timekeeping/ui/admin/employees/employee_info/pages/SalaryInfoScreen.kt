package com.example.timekeeping.ui.admin.employees.employee_info.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.timekeeping.models.Employee

import com.example.timekeeping.models.Salary

@Composable
fun SalaryInfoScreen(
    salary: Salary,
    onSalaryChange: (Salary) -> Unit = {}
) {
    var selectedType by remember { mutableStateOf(salary.salaryType) }
    var salaryValue by remember { mutableStateOf(salary.salary.toString()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Text("Cách tính lương", style = MaterialTheme.typography.titleMedium)

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            listOf("Giờ", "Ca", "Tháng").forEach { type ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedType == type,
                        onClick = {
                            selectedType = type
                            onSalaryChange(salary.copy(salaryType = type))
                        }
                    )
                    Text(type)
                }
            }
        }

        Text("Lương 1 tháng", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = salaryValue,
            onValueChange = {
                salaryValue = it.filter { c -> c.isDigit() || c == ',' }
                val cleaned = salaryValue.replace(",", "")
                val numericValue = cleaned.toIntOrNull() ?: 0
                onSalaryChange(salary.copy(salary = numericValue))
            },
            trailingIcon = {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SalaryInputScreenPreview(){
    SalaryInfoScreen(
        salary = Salary(
            employeeId = "1",
            groupId = "1",
            salaryType = "Giờ",
            salary = 1000000,
            createdAt = java.util.Date(),
        )
    )
}


