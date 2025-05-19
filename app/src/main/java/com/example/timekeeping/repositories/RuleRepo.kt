package com.example.timekeeping.repositories

import android.util.Log
import com.cloudinary.transformation.Condition
import com.example.timekeeping.models.ConditionNode
import com.example.timekeeping.models.Payroll
import com.example.timekeeping.models.Rule
import com.example.timekeeping.models.applyWageRules
import com.example.timekeeping.ui.admin.rule.SalaryFieldName
import com.example.timekeeping.utils.RuleEvaluator
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class RuleRepo @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    @OptIn(DelicateCoroutinesApi::class)
    fun createRule(rule: Rule, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("rules")
            .add(rule)
            .addOnSuccessListener {

//                val totalWageMap = mutableMapOf<String, Int>()
//                val totalNewWageMap = mutableMapOf<String, Int>()
//
//                firestore.collection("attendances")
//                    .whereEqualTo("groupId", rule.groupId)
//                    .get()
//                    .addOnSuccessListener { documents ->
//
//                        val countMap = mutableMapOf<String, Int>()
//
//                        for (document in documents) {
//                            val employeeId = document.getDocumentReference("employeeId")?.id
//                            if (employeeId != null) {
//                                countMap[employeeId] =
//                                    countMap.getOrDefault(employeeId, 0) + 1
//                            }
//                        }
//
//                        for ((employeeId, count) in countMap) {
//                            GlobalScope.launch {
//                                val comparisonMap = mapOf(
//                                    SalaryFieldName.NUMBER_OF_DAYS.label to count
//                                )
//
//                                try {
//                                    val finalWage = applyWageRules(
//                                        rule.groupId,
//                                        comparisonMap,
//                                        totalWageMap[employeeId]!!
//                                    )
//                                    totalNewWageMap[employeeId] = finalWage
//                                } catch (e: Exception) {
//                                    //onSuccess(totalWage) // Fallback nếu có lỗi
//                                }
//                            }
//                            Log.d(
//                                "AttendanceCount", "Employee $employeeId has $count attendances"
//                            )
//                        }
//
//                        firestore.collection("payrolls")
//                            .whereEqualTo("groupId", rule.groupId)
//                            .get()
//                            .addOnSuccessListener {
//                                for (document in it) {
//                                    val payroll = document.toObject(Payroll::class.java)
//                                    totalWageMap[payroll.employeeId] = payroll.totalWage
//
//                                    firestore.runTransaction {
//                                        it.update(
//                                            document.reference,
//                                            "totalWage",
//                                            totalWageMap[payroll.employeeId]
//                                        )
//                                    }
//                                }
//                            }
//                    }
//                    .addOnFailureListener { exception ->
//                        Log.w("Firestore", "Error getting documents: ", exception)
//                    }
                onSuccess()
            }
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
                val data =
                    doc.data ?: return@addOnSuccessListener onFailure(Exception("Rule not found"))
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
