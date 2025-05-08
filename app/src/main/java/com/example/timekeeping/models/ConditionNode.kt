package com.example.timekeeping.models

sealed class ConditionNode(val type: String) {
    data class FieldCondition(
        val field: String,
        val operator: String, // "==", "!=", ">", "<", ">=", "<="
        val value: Int
    ) : ConditionNode("field")

    data class AndCondition(
        val conditions: List<ConditionNode>
    ) : ConditionNode("and")

    data class OrCondition(
        val conditions: List<ConditionNode>
    ) : ConditionNode("or")
}

fun ConditionNode.toReadableString(): String {
    return when (this) {
        is ConditionNode.FieldCondition ->"${field} ${operator} ${value}"
        is ConditionNode.AndCondition -> conditions.joinToString(" AND ", "(", ")") { it.toReadableString() }
        is ConditionNode.OrCondition -> conditions.joinToString(" OR ", "(", ")") { it.toReadableString() }
    }
}