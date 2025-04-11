package com.example.timekeeping.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Login : Screen("login")
    object Register : Screen("register")

    object TeamManagement : Screen("teamManagement/{groupId}") {
        fun createRoute(groupId: String) = "teamManagement/$groupId"
    }

    object TeamForm : Screen("teamForm/{groupId}") {
        fun createRoute(groupId: String) = "teamForm/$groupId"
    }

    object GroupDetail : Screen("groupDetail/{groupId}") {
        fun createRoute(groupId: String) = "groupDetail/$groupId"
    }

    object GroupSettings : Screen("groupSettings/{groupId}") {
        fun createRoute(groupId: String) = "groupSettings/$groupId"
    }

    object EmployeeManagement : Screen("employeeManagement/{groupId}") {
        fun createRoute(groupId: String) = "employeeManagement/$groupId"
    }


    object EmployeeForm : Screen("employeeForm/{groupId}"){
        fun createRoute(groupId: String) = "employeeForm/$groupId"
    }

    object Schedule : Screen("assignment/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) = "assignment/$groupId/$employeeId"
    }
    object GroupForm : Screen("groupForm")

    object ShiftManagement : Screen("shiftManagement/{groupId}") {
        fun createRoute(groupId: String) = "shiftManagement/$groupId"
    }

    object ShiftInputForm : Screen("shiftInputForm/{groupId}") {
        fun createRoute(groupId: String) = "shiftInputForm/$groupId"
    }

    object ShiftEditForm : Screen("shiftEditForm/{groupId}/{shiftId}") {
        fun createRoute(groupId: String, shiftId: String) = "shiftEditForm/$groupId/$shiftId"
    }

    object CheckIn : Screen("checkIn/{groupId}") {
        fun createRoute(groupId: String) = "checkIn/$groupId"
    }

    object RequestJoinGroup : Screen("requestJoinGroup")
}