package com.example.timekeeping.models

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

enum class Role {
    ADMIN,
    EMPLOYEE,
    // Thêm các vai trò khác nếu cần
}

enum class Status {
    PENDING,
    ACCEPTED,
    UNAUTHORIZED,
    REJECTED, // Có thể thêm trạng thái này nếu cần hoặc xóa cả Employee_Group khi bị từ chối
    // Thêm các trạng thái khác nếu cần
}

class Employee_Group (
    var employeeId: DocumentReference = FirebaseFirestore.getInstance().collection("groups").document(""),
    var groupId: DocumentReference = FirebaseFirestore.getInstance().collection("groups").document(""),
    val isCreator: Boolean = false,
    val dayJoined: Date = Date(),
    var role: Role = Role.EMPLOYEE,
    var status: Status = Status.PENDING,
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "employeeId" to employeeId,
            "groupId" to groupId,
            "isCreator" to isCreator,
            "dayJoined" to dayJoined,
            "role" to role.toString(),
            "status" to status.toString(),
        )
    }
}