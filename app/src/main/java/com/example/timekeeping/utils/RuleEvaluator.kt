package com.example.timekeeping.utils

import com.example.timekeeping.models.ConditionNode

// file: domain/rules/RuleEvaluator.kt

object RuleEvaluator {

    // Hàm để so sánh giá trị với condition
    fun evaluate(condition: ConditionNode, comparisonMap: Map<String, Int>): Boolean {
        return when (condition) {
            is ConditionNode.FieldCondition -> evaluateFieldCondition(condition, comparisonMap)
            is ConditionNode.AndCondition -> evaluateAndCondition(condition, comparisonMap)
            is ConditionNode.OrCondition -> evaluateOrCondition(condition, comparisonMap)
        }
    }

    // Hàm để xử lý FieldCondition
    private fun evaluateFieldCondition(
        condition: ConditionNode.FieldCondition,
        comparisonMap: Map<String, Int>
    ): Boolean {
        val fieldValue = comparisonMap[condition.field] ?: return false

        return when (condition.operator) {
            ">" -> fieldValue > condition.value
            "<" -> fieldValue < condition.value
            "=" -> fieldValue == condition.value
            ">=" -> fieldValue >= condition.value
            "<=" -> fieldValue <= condition.value
            "!=" -> fieldValue != condition.value
            else -> false
        }
    }

    // Hàm để xử lý AndCondition
    private fun evaluateAndCondition(
        condition: ConditionNode.AndCondition,
        comparisonMap: Map<String, Int>
    ): Boolean {
        // Kiểm tra tất cả các điều kiện con trong AndCondition đều thỏa mãn
        return condition.conditions.all { evaluate(it, comparisonMap) }
    }

    // Hàm để xử lý OrCondition
    private fun evaluateOrCondition(
        condition: ConditionNode.OrCondition,
        comparisonMap: Map<String, Int>
    ): Boolean {
        // Kiểm tra ít nhất một điều kiện con trong OrCondition thỏa mãn
        return condition.conditions.any { evaluate(it, comparisonMap) }
    }


    fun mapToConditionNode(map: Map<String, Any>): ConditionNode {
        val type = map["type"] as? String ?: throw IllegalArgumentException("Missing type")
        return when (type) {
            "field" -> ConditionNode.FieldCondition(
                field = map["field"] as String,
                operator = map["operator"] as String,
                value = (map["value"] as Number).toInt()
            )
            "and" -> ConditionNode.AndCondition(
                conditions = (map["conditions"] as List<Map<String, Any>>).map { mapToConditionNode(it) }
            )
            "or" -> ConditionNode.OrCondition(
                conditions = (map["conditions"] as List<Map<String, Any>>).map { mapToConditionNode(it) }
            )
            else -> throw IllegalArgumentException("Unknown condition type: $type")
        }
    }
}
