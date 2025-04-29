package com.example.timekeeping.navigation

sealed class Screen(val route: String) {

    // region Authentication
    object Login : Screen("login")
    object Register : Screen("register")
    // endregion

    // region General
    object Home : Screen("home")
    object Profile : Screen("profile")
    // endregion

    // region Group
    object GroupForm : Screen("groupForm")
    object EditGroup : Screen("editGroup/{groupId}") {
        fun createRoute(groupId: String) = "editGroup/$groupId"
    }
    object GroupDetail : Screen("groupDetail/{groupId}") {
        fun createRoute(groupId: String) = "groupDetail/$groupId"
    }
    object GroupSettings : Screen("groupSettings/{groupId}") {
        fun createRoute(groupId: String) = "groupSettings/$groupId"
    }
    // endregion

    // region Team
    object TeamManagement : Screen("teamManagement/{groupId}") {
        fun createRoute(groupId: String) = "teamManagement/$groupId"
    }
    object TeamForm : Screen("teamForm/{groupId}") {
        fun createRoute(groupId: String) = "teamForm/$groupId"
    }
    object TeamEditForm : Screen("teamEditForm/{groupId}/{teamId}") {
        fun createRoute(groupId: String, teamId: String) =
            "teamEditForm/$groupId/$teamId"
    }
    // endregion

    // region Employee
    object EmployeeManagement : Screen("employeeManagement/{groupId}") {
        fun createRoute(groupId: String) = "employeeManagement/$groupId"
    }
    object EmployeeForm : Screen("employeeForm/{groupId}") {
        fun createRoute(groupId: String) = "employeeForm/$groupId"
    }
    object EmployeeDetail : Screen("employeeDetail/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) =
            "employeeDetail/$groupId/$employeeId"
    }
    object EmployeeInfo : Screen("employeeInfo/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) =
            "employeeInfo/$groupId/$employeeId"
    }
    // endregion

    // region Bonus
    object BonusForm : Screen("bonusForm/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) =
            "bonusForm/$groupId/$employeeId"
    }
    object BonusInputForm : Screen("bonusInputForm/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) =
            "bonusInputForm/$groupId/$employeeId"
    }
    object BonusEditForm : Screen("bonusEditForm/{groupId}/{employeeId}/{adjustmentId}") {
        fun createRoute(groupId: String, employeeId: String, adjustmentId: String) =
            "bonusEditForm/$groupId/$employeeId/$adjustmentId"
    }
    // endregion

    // region Minus Money
    object MinusMoneyForm : Screen("minusMoneyForm/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) =
            "minusMoneyForm/$groupId/$employeeId"
    }
    object MinusMoneyInputForm : Screen("minusMoneyInputForm/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) =
            "minusMoneyInputForm/$groupId/$employeeId"
    }
    object MinusMoneyEditForm : Screen("minusMoneyEditForm/{groupId}/{employeeId}/{adjustmentId}") {
        fun createRoute(groupId: String, employeeId: String, adjustmentId: String) =
            "minusMoneyEditForm/$groupId/$employeeId/$adjustmentId"
    }
    // endregion

    // region Salary Advance
    object SalaryAdvanceForm : Screen("salaryAdvanceForm/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) =
            "salaryAdvanceForm/$groupId/$employeeId"
    }
    object SalaryAdvanceInputForm : Screen("salaryAdvanceInputForm/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) =
            "salaryAdvanceInputForm/$groupId/$employeeId"
    }
    object SalaryAdvanceEditForm : Screen("salaryAdvanceEditForm/{groupId}/{employeeId}/{adjustmentId}") {
        fun createRoute(groupId: String, employeeId: String, adjustmentId: String) =
            "salaryAdvanceEditForm/$groupId/$employeeId/$adjustmentId"
    }
    // endregion

    // region Payment
    object PaymentForm : Screen("payment/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) =
            "payment/$groupId/$employeeId"
    }
    object PaymentInputForm : Screen("paymentInputForm/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) =
            "paymentInputForm/$groupId/$employeeId"
    }
    object PaymentEditForm : Screen("paymentEditForm/{groupId}/{employeeId}/{paymentId}") {
        fun createRoute(groupId: String, employeeId: String, paymentId: String) =
            "paymentEditForm/$groupId/$employeeId/$paymentId"
    }
    // endregion

    // region Shift
    object ShiftManagement : Screen("shiftManagement/{groupId}") {
        fun createRoute(groupId: String) = "shiftManagement/$groupId"
    }
    object ShiftInputForm : Screen("shiftInputForm/{groupId}") {
        fun createRoute(groupId: String) = "shiftInputForm/$groupId"
    }
    object ShiftEditForm : Screen("shiftEditForm/{groupId}/{shiftId}") {
        fun createRoute(groupId: String, shiftId: String) =
            "shiftEditForm/$groupId/$shiftId"
    }
    // endregion

    // region Schedule
    object Schedule : Screen("assignment/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) =
            "assignment/$groupId/$employeeId"
    }
    // endregion

    // region Check-In
    object CheckIn : Screen("checkIn/{groupId}") {
        fun createRoute(groupId: String) = "checkIn/$groupId"
    }
    object CheckInManagement : Screen("checkInManagement/{groupId}") {
        fun createRoute(groupId: String) = "checkInManagement/$groupId"
    }
    // endregion

    // region Misc
    object RequestJoinGroup : Screen("requestJoinGroup")
    // endregion

    // region Grant Permission
    object GrantPermission : Screen("grantPermission/{groupId}/{employeeId}") {
        fun createRoute(groupId: String, employeeId: String) = "grantPermission/$groupId/$employeeId"
    }
    // endregion

    // region MyAccount
    object MyAccount : Screen("myAccount/{employeeId}") {
        fun createRoute(employeeId: String) = "myAccount/$employeeId"
    }
    object MyQRCode : Screen("myQRCode/{employeeId}") {
        fun createRoute(employeeId: String) = "myQRCode/$employeeId"
    }
    object EditAccountInfo : Screen("editAccountInfo/{employeeId}") {
        fun createRoute(employeeId: String) = "editAccountInfo/$employeeId"
    }
    // endregion

}