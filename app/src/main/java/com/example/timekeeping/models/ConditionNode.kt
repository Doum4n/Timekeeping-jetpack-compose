package com.example.timekeeping.models

import com.example.timekeeping.utils.RuleEvaluator
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

suspend fun applyWageRules(
    groupId: String,
    comparisonMap: Map<String, Int>,
    originalValue: Int
): Int {
    return suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance().collection("rules")
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { snapshot ->
                var adjustedValue = originalValue
                for (doc in snapshot.documents) {
                    val data = doc.data ?: continue
                    val conditionMap = data["condition"] as? Map<String, Any> ?: continue
                    val condition = RuleEvaluator.mapToConditionNode(conditionMap)
                    val bonus = (data["bonus"] as? Number)?.toInt() ?: 0
                    if (RuleEvaluator.evaluate(condition, comparisonMap)) {
                        adjustedValue += bonus
                    }
                }
                continuation.resume(adjustedValue) // Kết thúc bất đồng bộ và trả giá trị
            }
            .addOnFailureListener {
                continuation.resume(originalValue) // Fallback nếu lỗi
            }
    }
}