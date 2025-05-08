package com.example.timekeeping.repositories

import android.util.Log
import com.cloudinary.transformation.Condition
import com.example.timekeeping.models.ConditionNode
import com.example.timekeeping.models.Rule
import com.example.timekeeping.utils.RuleEvaluator
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class RuleRepo @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun createRule(rule: Rule, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("rules")
            .add(rule)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    private fun conditionToMap(node: ConditionNode): Map<String, Any> {
        return when (node) {
            is ConditionNode.FieldCondition -> mapOf(
                "type" to "field",
                "field" to node.field,
                "operator" to node.operator,
                "value" to node.value as Any
            )

            is ConditionNode.AndCondition -> mapOf(
                "type" to "and",
                "conditions" to node.conditions.map { conditionToMap(it) }
            )

            is ConditionNode.OrCondition -> mapOf(
                "type" to "or",
                "conditions" to node.conditions.map { conditionToMap(it) }
            )
        }
    }

    fun getRules(groupId: String, onSuccess: (List<Rule>) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("rules")
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { result ->
                val rules = result.documents.map { doc ->
                    val data = doc.data ?: return@map null

//                    val conditionNodes = when (val rawCondition = data["condition"]) {
//                        is Map<*, *> -> listOf(RuleEvaluator.mapToConditionNode(data["type"] as String, rawCondition as Map<String, Any>))
//                        is List<*> -> rawCondition.map {
//                            RuleEvaluator.mapToConditionNode(data["type"] as String, it as Map<String, Any>)
//                        }
//                        else -> emptyList()
//                    }

                    Rule(
                        id = doc.id,
                        groupId = groupId,
                        name = data["name"] as String,
                        condition = RuleEvaluator.mapToConditionNode(data["condition"] as Map<String, Any>),
                        bonus = (data["bonus"] as Number).toInt()
                    )
                }.filterNotNull()
                onSuccess(rules)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun deleteRule(ruleId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("rules").document(ruleId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateRule(rule: Rule, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("rules").document(rule.id)
            .set(rule)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getRule(ruleId: String, onSuccess: (Rule) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("rules").document(ruleId)
            .get()
            .addOnSuccessListener { doc ->
                val data = doc.data ?: return@addOnSuccessListener onFailure(Exception("Rule not found"))
                val rule = Rule(
                    id = doc.id,
                    groupId = data["groupId"] as String,
                    name = data["name"] as String,
                    condition = RuleEvaluator.mapToConditionNode(data["condition"] as Map<String, Any>),
                    bonus = (data["bonus"] as Number).toInt()
                )
                Log.d("RuleRepo", "Fetched rule: $rule")
                onSuccess(rule)
            }.addOnFailureListener { onFailure(it) }
    }
}
