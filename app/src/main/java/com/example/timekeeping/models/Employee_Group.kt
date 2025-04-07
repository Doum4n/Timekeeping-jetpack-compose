package com.example.timekeeping.models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

enum class Role {
    ADMIN,
    MEMBER,
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
    var employeeId: String = "",
    var groupId: String = "",
    val isCreator: Boolean = false,
    val dayJoined: Date = Date(),
    var role: Role = Role.MEMBER,
    var status: Status = Status.PENDING,
) {
}