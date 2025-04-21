package com.example.timekeeping.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Login : Screen("login")
    object Register : Screen("register")

    object TeamManagement : Screen("teamManagement/{groupId}") {
        fun createRoute(groupId: String) = "teamManagement/$groupId"
    }

    object BonusForm : Screen("bonusForm/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) = "bonusForm/$groupId/$employeeId"
    }

    object BonusInputForm : Screen("bonusInputForm/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) = "bonusInputForm/$groupId/$employeeId"
    }

    object MinusMoneyForm : Screen("minusMoneyForm/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) = "minusMoneyForm/$groupId/$employeeId"
    }

    object MinusMoneyInputForm : Screen("minusMoneyInputForm/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) = "minusMoneyInputForm/$groupId/$employeeId"
    }

    object SalaryAdvanceForm : Screen("salaryAdvanceForm/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) = "salaryAdvanceForm/$groupId/$employeeId"
    }

    object SalaryAdvanceInputForm : Screen("salaryAdvanceInputForm/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) = "salaryAdvanceInputForm/$groupId/$employeeId"
    }

    object TeamForm : Screen("teamForm/{groupId}") {
        fun createRoute(groupId: String) = "teamForm/$groupId"
    }

    object GroupDetail : Screen("groupDetail/{groupId}") {
        fun createRoute(groupId: String) = "groupDetail/$groupId"
    }

    object EditGroup : Screen("editGroup/{groupId}") {
        fun createRoute(groupId: String) = "editGroup/$groupId"
    }

    object GroupSettings : Screen("groupSettings/{groupId}") {
        fun createRoute(groupId: String) = "groupSettings/$groupId"
    }

    object EmployeeManagement : Screen("employeeManagement/{groupId}") {
        fun createRoute(groupId: String) = "employeeManagement/$groupId"
    }

    object EmployeeDetail : Screen("employeeDetail/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) = "employeeDetail/$groupId/$employeeId"
    }

    object EmployeeInfo : Screen("employeeInfo/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) = "employeeInfo/$groupId/$employeeId"
    }

    object EmployeeForm : Screen("employeeForm/{groupId}"){
        fun createRoute(groupId: String) = "employeeForm/$groupId"
    }

    object Schedule : Screen("assignment/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) = "assignment/$groupId/$employeeId"
    }

    object CheckInManagement : Screen("checkInManagement/{groupId}") {
        fun createRoute(groupId: String) = "checkInManagement/$groupId"
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