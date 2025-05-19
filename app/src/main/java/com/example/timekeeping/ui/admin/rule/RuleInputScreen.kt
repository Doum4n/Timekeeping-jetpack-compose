package com.example.timekeeping.ui.admin.rule

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.ConditionNode
import com.example.timekeeping.models.Rule
import com.example.timekeeping.ui.admin.components.TopBarClassic
import com.example.timekeeping.view_models.RuleViewModel

enum class SalaryFieldName(val label: String){
    NUMBER_OF_DAYS("Số ngày làm"),
    NUMBER_OF_HOURS("Số giờ làm"),
    NUMBER_OF_OVERTIME_HOURS("Số giờ tăng ca"),
    NUMBER_OF_ABSENT_DAYS("Số ngày nghỉ"),
}

fun String.toSalaryFieldName(): SalaryFieldName? {
    return SalaryFieldName.entries.find { it.label == this }
}

@Composable
fun RuleInputScreen(
    ruleId: String = "",
    groupId: String = "",
    onBack: () -> Unit,
    onSave: (Rule) -> Unit,
    ruleViewModel: RuleViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var bonus by remember { mutableStateOf("") }

    val tableOptions = listOf("Salary")
    val typeOptions = listOf("field", "and", "or")
    var selectedTable by remember { mutableStateOf(tableOptions[0]) }
    var selectedField by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(typeOptions[0]) }

    var conditions = remember { mutableStateListOf<ConditionNode>() }

    val fieldOptions = remember(selectedTable) {
        SalaryFieldName.entries.map { it.label }
    }

    var operator by remember { mutableStateOf("==") }
    var value by remember { mutableStateOf(0) }

    val operatorOptions = listOf("==", "!=", ">", "<", ">=", "<=")

    LaunchedEffect(ruleId) {
        if (ruleId.isNotEmpty()) {
            ruleViewModel.getRule(
                ruleId,
                onSuccess = { rule ->
                    name = rule.name
                    bonus = rule.bonus.toString()
                    selectedField = (rule.condition as? ConditionNode.FieldCondition)?.field ?: ""
                    selectedType = when (rule.condition) {
                        is ConditionNode.FieldCondition -> "field"
                        is ConditionNode.AndCondition -> "and"
                        is ConditionNode.OrCondition -> "or"
                    }
                    operator = (rule.condition as? ConditionNode.FieldCondition)?.operator ?: "=="
                    value = (rule.condition as? ConditionNode.FieldCondition)?.value ?: 0
                    conditions.clear()
                    conditions.addAll(
                        when (rule.condition) {
                            is ConditionNode.AndCondition -> rule.condition.conditions
                            is ConditionNode.OrCondition -> rule.condition.conditions
                            else -> listOf(rule.condition)
                        }
                    )
                },
                onFailure = {
                    Toast.makeText(context, "Không tải được quy tắc", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopBarClassic(
                title = "Tạo Quy Tắc",
                onBackClick = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val rule = when (selectedType) {
                    "field" -> Rule(ruleId, groupId, name, conditions.first(), bonus.toIntOrNull() ?: 0)
                    "and" -> Rule(ruleId, groupId, name, ConditionNode.AndCondition(conditions), bonus.toIntOrNull() ?: 0)
                    "or" -> Rule(ruleId, groupId, name, ConditionNode.OrCondition(conditions), bonus.toIntOrNull() ?: 0)
                    else -> throw IllegalArgumentException("Invalid type")
                }
                onSave(rule)
            }) {
                Icon(Icons.Default.Check, contentDescription = "Lưu")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Thông tin quy tắc", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                Spacer(Modifier.padding(4.dp))
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên quy tắc") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = bonus,
                    onValueChange = { bonus = it.filter { c -> c.isDigit() } },
                    label = { Text("Tiền thưởng (VNĐ)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Text("Loại điều kiện", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))

                var expanded by remember { mutableStateOf(false) }

                Box {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        label = { Text("Loại") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true },
                        enabled = false,
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        typeOptions.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    selectedType = it
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Text("Các điều kiện", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            }

            items(conditions.size) { index ->
                val condition = conditions[index]
                androidx.compose.material3.Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = androidx.compose.material3.CardDefaults.elevatedCardElevation(4.dp)
                ) {
                    TypeItem(
                        condition = condition,
                        onConditionChange = {
                            conditions[index] = it
                        },
                        tableOptions = tableOptions,
                        fieldOptions = fieldOptions,
                        operatorOptions = operatorOptions
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        conditions.add(
                            ConditionNode.FieldCondition(
                                field = selectedField,
                                operator = operator,
                                value = value
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("➕ Thêm điều kiện")
                }
            }
        }
    }
}

@Composable
fun TypeItem(
    condition: ConditionNode,
    onConditionChange: (ConditionNode) -> Unit,
    tableOptions: List<String>,
    fieldOptions: List<String>,
    operatorOptions: List<String>
) {
    if (condition !is ConditionNode.FieldCondition) return

    var selectedField by remember { mutableStateOf(condition.field) }
    var selectedOperator by remember { mutableStateOf(condition.operator) }
    var inputValue by remember { mutableStateOf(condition.value.toString()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Điều kiện", style = MaterialTheme.typography.titleSmall)

        Spacer(Modifier.height(8.dp))

        // Field Dropdown
        DropdownSelector(
            label = "Trường",
            options = fieldOptions,
            selectedOption = selectedField,
            onOptionSelected = {
                selectedField = it
                onConditionChange(condition.copy(field = it))
            }
        )

        Spacer(Modifier.height(8.dp))

        // Operator Dropdown
        DropdownSelector(
            label = "Toán tử",
            options = operatorOptions,
            selectedOption = selectedOperator,
            onOptionSelected = {
                selectedOperator = it
                onConditionChange(condition.copy(operator = it))
            }
        )

        Spacer(Modifier.height(8.dp))

        // Value Input
        OutlinedTextField(
            value = inputValue,
            onValueChange = {
                inputValue = it.filter { c -> c.isDigit() }
                onConditionChange(condition.copy(value = inputValue.toIntOrNull() ?: 0))
            },
            label = { Text("Giá trị") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            enabled = false,
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}