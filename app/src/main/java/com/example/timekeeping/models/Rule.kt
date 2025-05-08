package com.example.timekeeping.models

data class Rule(
    val id: String = "",
    val groupId: String = "",
    val name: String = "",
    val condition: ConditionNode = ConditionNode.FieldCondition("", "", 0),
    val bonus: Int = 0
)