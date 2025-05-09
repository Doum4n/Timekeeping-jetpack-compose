package com.example.timekeeping.models

import com.example.timekeeping.utils.DateTimeMap
import java.time.LocalDateTime
import java.time.temporal.TemporalAmount

enum class RequestType(val displayName: String) {
    ADVANCE_SALARY("Ứng lương"),
    LEAVE("Xin nghỉ phép"),
}

data class Request(
    val id: String = "",
    var employeeId: String = "",
    var groupId: String = "",
    var type: String = "",
    var amount: Int = 0,
    var reason: String = "",
    var status: String = "Chờ duyệt", // pending, approved, rejected
    var createdAt: DateTimeMap = DateTimeMap.from(LocalDateTime.now()),
){
    fun toMapWithAmount(): Map<String, Any> {
        return mapOf(
            "type" to type,
            "amount" to amount,
            "reason" to reason,
            "createdAt" to createdAt
        )
    }

    fun toMapWithReason(): Map<String, Any> {
        return mapOf(
            "type" to type,
            "amount" to amount,
            "reason" to reason,
            "createdAt" to createdAt
        )
    }
}