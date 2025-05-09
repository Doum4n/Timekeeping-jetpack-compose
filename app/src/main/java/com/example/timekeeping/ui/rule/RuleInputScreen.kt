package com.example.timekeeping.ui.rule

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.ConditionNode
import com.example.timekeeping.models.Rule
import com.example.timekeeping.ui.components.TopBarClassic
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
    onSave: (Rule) -> Unit, // callback khi lưu
    ruleViewModel: RuleViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var bonus by remember { mutableStateOf("") }

    // Cấu hình bảng và trường
    val tableOptions = listOf("Salary") // Các bảng có sẵn
    val typeOptions = listOf("field", "and", "or") // Các kiểu có sẵn
    var selectedTable by remember { mutableStateOf(tableOptions[0]) }
    var selectedField by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(typeOptions[0]) }

    var conditions = remember { mutableStateListOf<ConditionNode>() }
    var mapConditions = remember { mutableStateMapOf<String, ConditionNode>() }

    val fieldOptions = remember(selectedTable) {
        // Cập nhật các field theo bảng đã chọn
        when (selectedTable) {
            "Salary" -> SalaryFieldName.entries.map { it.label }.toList()
            else -> listOf()
        }
    }

    var operator by remember { mutableStateOf("==") }
    var value by remember { mutableStateOf("") }

    val operatorOptions = listOf("==", "!=", ">", "<", ">=", "<=")

    LaunchedEffect(ruleId) {
        if(ruleId.isNotEmpty()) {
            ruleViewModel.getRule(
                ruleId, onSuccess = { rule ->
                    name = rule.name
                    bonus = rule.bonus.toString()
                    selectedField = when (rule.condition) {
                        is ConditionNode.FieldCondition -> rule.condition.field
                        else -> ""
                    }
                    selectedTable = when(selectedField){
                        SalaryFieldName.NUMBER_OF_DAYS.label -> "Salary"
                        SalaryFieldName.NUMBER_OF_HOURS.label -> "Salary"
                        SalaryFieldName.NUMBER_OF_OVERTIME_HOURS.label -> "Salary"
                        else -> ""
                    }
                    selectedType = when (rule.condition) {
                        is ConditionNode.FieldCondition -> "field"
                        is ConditionNode.AndCondition -> "and"
                        is ConditionNode.OrCondition -> "or"
                    }
                    operator = when (rule.condition) {
                        is ConditionNode.FieldCondition -> rule.condition.operator
                        else -> ""
                    }
                    value = when (rule.condition) {
                        is ConditionNode.FieldCondition -> rule.condition.value.toString()
                        else -> ""
                    }
                    conditions.clear()
                    conditions.addAll(when (rule.condition) {
                        is ConditionNode.AndCondition -> { rule.condition.conditions }
                        is ConditionNode.OrCondition -> { rule.condition.conditions }
                        else -> listOf(rule.condition)
                    })
                },
                onFailure = {
                    Toast.makeText(context, "Failed to load rule: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopBarClassic(
                title = "Tạo quy định",
                onBackClick = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onSave(
                    when(selectedType){
                        "field" -> Rule(
                            id = ruleId,
                            groupId = groupId,
                            name = name,
                            condition = conditions.first(),
                            bonus = bonus.toInt()
                        )
                        "and" ->  Rule(
                            id = ruleId,
                            groupId = groupId,
                            name = name,
                            condition = ConditionNode.AndCondition(conditions.toList()), // wrap tại đây
                            bonus = bonus.toInt()
                        )
                        "or" -> Rule(
                            id = ruleId,
                            groupId = groupId,
                            name = name,
                            condition = ConditionNode.OrCondition(conditions.toList()), // wrap tại đây
                            bonus = bonus.toInt()
                        )
                        else -> throw IllegalArgumentException("Invalid type")
                    }

                )
            }) {
                Icon(Icons.Default.Check, contentDescription = "Lưu")
            }
        }
    ) { paddingValues ->
        LazyColumn (
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            item {
                // Nhập tên quy tắc
                TextField(value = name, onValueChange = { name = it }, label = { Text("Tên quy tắc") })
            }

            item {
                // Nhập số tiền thưởng
                TextField(value = bonus, onValueChange = { bonus = it }, label = { Text("Tiền thưởng (VNĐ)") })
            }

            item{
                Text("Chọn loại:")
                var expandedType by remember { mutableStateOf(false) }
                Box {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        label = { Text("Loại") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedType = true },
                        enabled = false,
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    )
                    DropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                        typeOptions.forEach {
                            DropdownMenuItem(onClick = {
                                selectedType = it
                                expandedType = false
                            }, text = { Text(it) })
                        }
                    }
                }
            }

            item{
                conditions.forEach{condition ->
                    TypeItem(
                        condition,
                        { conditions[conditions.indexOf(condition)] = it },
                        tableOptions,
                        fieldOptions,
                        operatorOptions,
                    )
                }
            }

            item{
                Button(
                    onClick = {
                        conditions.add(
                            ConditionNode.FieldCondition(
                                field = selectedField,
                                operator = operator,
                                value = value.toInt()
                            )
                        )
                    }
                ) {
                    Text("Thêm điều kiện")
                }
            }

        }
    }
}

@Composable
private fun TypeItem(
    condition: ConditionNode? = null,
    onConditionChange: (ConditionNode.FieldCondition) -> Unit,
    tableOptions: List<String>,
    fieldOptions: List<String>,
    operatorOptions: List<String>
) {
    // State lưu giá trị đã chọn
    var selectedTable by remember { mutableStateOf("") }
    var selectedField by remember { mutableStateOf("") }
    var selectedOperator by remember { mutableStateOf("") }
    var inputValue by remember { mutableStateOf("") }

    LaunchedEffect(condition) {
        if (condition != null && condition is ConditionNode.FieldCondition) {
            selectedTable = when (condition.field) {
                SalaryFieldName.NUMBER_OF_DAYS.label -> "salary"
                SalaryFieldName.NUMBER_OF_HOURS.label -> "salary"
                SalaryFieldName.NUMBER_OF_OVERTIME_HOURS.label -> "salary"
                else -> ""
            }
            selectedField = condition.field
            selectedOperator = condition.operator
            inputValue = condition.value.toString()
        }
        Log.d("RuleInputScreen", "condition: $condition")
    }

    // Dropdown chọn bảng
    Text("Chọn bảng:")
    var expandedTable by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = selectedTable,
            onValueChange = {},
            label = { Text("Bảng") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandedTable = true },
            enabled = false,
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        )
        DropdownMenu(expanded = expandedTable, onDismissRequest = { expandedTable = false }) {
            tableOptions.forEach {
                DropdownMenuItem(onClick = {
                    selectedTable = it
                    selectedField = "" // Reset trường nếu đổi bảng
                    expandedTable = false
                }, text = { Text(it) })
            }
        }
    }

    Divider()

    // Dropdown chọn field
    Text("Chọn trường:")
    var expandedField by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = selectedField,
            onValueChange = {},
            label = { Text("Trường") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandedField = true },
            enabled = false,
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        )
        DropdownMenu(expanded = expandedField, onDismissRequest = { expandedField = false }) {
            fieldOptions.forEach {
                DropdownMenuItem(onClick = {
                    selectedField = it
                    expandedField = false
                    onConditionChange(
                        ConditionNode.FieldCondition(selectedField, selectedOperator, inputValue.toInt())
                    )

                }, text = { Text(it) })
            }
        }
    }

    Divider()

    // Dropdown chọn toán tử
    Text("Chọn toán tử:")
    var expandedOperator by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = selectedOperator,
            onValueChange = {},
            label = { Text("Toán tử") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandedOperator = true },
            enabled = false,
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        )
        DropdownMenu(expanded = expandedOperator, onDismissRequest = { expandedOperator = false }) {
            operatorOptions.forEach {
                DropdownMenuItem(onClick = {
                    selectedOperator = it
                    expandedOperator = false
                    onConditionChange(
                        ConditionNode.FieldCondition(selectedField, selectedOperator, inputValue.toInt())
                    )

                }, text = { Text(it) })
            }
        }
    }

    Divider()

    // Nhập giá trị
    TextField(
        value = inputValue,
        onValueChange = {
            inputValue = it
            onConditionChange(
                ConditionNode.FieldCondition(selectedField, selectedOperator, inputValue.toInt())
            )
        },
        label = { Text("Giá trị") }
    )
}