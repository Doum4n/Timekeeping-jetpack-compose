package com.example.timekeeping.ui.rule

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.ConditionNode
import com.example.timekeeping.models.Rule
import com.example.timekeeping.models.toReadableString
import com.example.timekeeping.ui.components.TopBarClassic
import com.example.timekeeping.ui.components.TopBarWithAddAction
import com.example.timekeeping.view_models.RuleViewModel

@Composable
fun RuleManagement(
    groupId: String,
    viewModel: RuleViewModel = hiltViewModel(),
    onAddRuleClick: () -> Unit,
    onEditRuleClick: (Rule) -> Unit,
    onDeleteRuleClick: (Rule) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    var rules by remember { mutableStateOf(listOf<Rule>()) }

    LaunchedEffect(groupId) {
        viewModel.getRules(groupId,
            onSuccess = { rules = it },
            onFailure = {
                Toast.makeText(context, "Lấy danh sách quy tắc thất bại", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(
        topBar = {
            TopBarWithAddAction(
                title = "Quản lý quy tắc",
                onBackClick = onBack,
                onAddShiftClick = onAddRuleClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
//            contentPadding = paddingValues
        ) {
            items(rules.size) { index ->
                val rule = rules[index]
                RuleItem(
                    rule = rule,
                    onEditClick = onEditRuleClick,
                    onDeleteClick = onDeleteRuleClick
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center) {
                    Button(onClick = onAddRuleClick) {
                        Text("➕ Thêm quy tắc")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun RuleItem(
    rule: Rule,
    onEditClick: (Rule) -> Unit,
    onDeleteClick: (Rule) -> Unit
) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = androidx.compose.material3.CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = rule.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

//                Row {
                    IconButton(onClick = { onEditClick(rule) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { onDeleteClick(rule) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
//                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            RuleRow(label = "🔍 Điều kiện:", value =rule.condition.toReadableString())
            RuleRow(label = "💰 Thưởng:", value = "${rule.bonus}₫")
        }
    }
}

@Composable
fun RuleRow(label: String, value: String) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(2f)
        )
    }
}