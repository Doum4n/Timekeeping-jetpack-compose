package com.example.timekeeping.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.CheckInScreen
import com.example.timekeeping.RequestJoinGroupScreen
import com.example.timekeeping.navigation.account.addMyAccountNav
import com.example.timekeeping.navigation.admin.approvalRequest.addApprovalRequestNav
import com.example.timekeeping.navigation.auth.addAuthScreens
import com.example.timekeeping.navigation.admin.checkin.addCheckInNav
import com.example.timekeeping.navigation.admin.employee.addEmployeeFormScreen
import com.example.timekeeping.navigation.admin.employee.addEmployeeScreen
import com.example.timekeeping.navigation.admin.grant_permission.addGrantPermissionNav
import com.example.timekeeping.navigation.admin.groups.addGroupFormScreen
import com.example.timekeeping.navigation.admin.groups.addGroupScreen
import com.example.timekeeping.navigation.admin.payment.addPaymentFormNav
import com.example.timekeeping.navigation.admin.payment.addPaymentNav
import com.example.timekeeping.navigation.admin.payroll.addPayrollNav
import com.example.timekeeping.navigation.admin.request.addRequestAdvanceNav
import com.example.timekeeping.navigation.admin.request.addRequestFormNav
import com.example.timekeeping.navigation.admin.rule.addRuleInputNav
import com.example.timekeeping.navigation.admin.rule.addRuleNav
import com.example.timekeeping.navigation.admin.schedule.addScheduleScreen
import com.example.timekeeping.navigation.admin.shifts.addShiftFormScreen
import com.example.timekeeping.navigation.admin.shifts.addShiftScreen
import com.example.timekeeping.navigation.admin.team.addTeamFormScreen
import com.example.timekeeping.navigation.admin.team.addTeamScreen
import com.example.timekeeping.navigation.employee.addAttendanceNav
import com.example.timekeeping.ui.account.MyAccountScreen
import com.example.timekeeping.ui.home.HomeScreen
import com.example.timekeeping.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        addHomeScreen(navController)
        addAuthScreens(navController)
        addShiftScreen(navController)
        addShiftFormScreen(navController)
        addGroupScreen(navController)
        addGroupFormScreen(navController)
        addEmployeeScreen(navController)
        addEmployeeFormScreen(navController)
        addScheduleScreen(navController)
        addTeamScreen(navController)
        addTeamFormScreen(navController)
        addCheckInNav(navController)

        addPayrollNav(navController)

        addPaymentNav(navController)
        addPaymentFormNav(navController)

        addGrantPermissionNav(navController)

        addRuleNav(navController)
        addRuleInputNav(navController)

        addRequestAdvanceNav(navController)
        addRequestFormNav(navController)

        addAttendanceNav(navController)

        addApprovalRequestNav(navController)

        addCheckInScreen(navController)
        addProfileScreen(navController)
        addMyAccountNav(navController)

        addRequestJoinGroup(navController)
    }
}

private fun NavGraphBuilder.addHomeScreen(navController: NavHostController) {
    composable(Screen.Home.route) {
        HomeScreen(navController = navController)
    }
}

private fun NavGraphBuilder.addProfileScreen(navController: NavHostController) {
    composable(Screen.Profile.route) {
        MyAccountScreen(
            onLogout = {
                navController.navigate(Screen.Login.route)
                FirebaseAuth.getInstance().signOut()
                SessionManager.forgetLoginSession()
            },
            onShowCode = { employeeId ->
                navController.navigate(Screen.MyQRCode.createRoute(employeeId))
            },
            onEdit = { navController.navigate(Screen.EditAccountInfo.createRoute(SessionManager.getEmployeeId() ?: "")) },
            onChangePassword = {
                navController.navigate(Screen.ChangePassword.route)
            }
        )
    }
}

private fun NavGraphBuilder.addCheckInScreen(navController: NavHostController) {
    composable(
        route = Screen.CheckIn.route,
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ) { backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        CheckInScreen(
            groupId = groupId,
            onBackClick = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.addRequestJoinGroup(navController: NavHostController){
    composable(
        route = Screen.RequestJoinGroup.route,
    ){
        RequestJoinGroupScreen(
            onBackClick = { navController.popBackStack() }
        )
    }
}