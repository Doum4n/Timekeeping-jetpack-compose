package com.example.timekeeping.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.CheckInScreen
import com.example.timekeeping.ui.employees.EmployeeManagementScreen
import com.example.timekeeping.HomeScreen
import com.example.timekeeping.RequestJoinGroupScreen
import com.example.timekeeping.ui.employees.MenuItem
import com.example.timekeeping.navigation.auth.addAuthScreens
import com.example.timekeeping.navigation.employee.addEmployeeScreen
import com.example.timekeeping.navigation.groups.addGroupFormScreen
import com.example.timekeeping.navigation.groups.addGroupScreen
import com.example.timekeeping.navigation.shifts.addShiftFormScreen
import com.example.timekeeping.navigation.shifts.addShiftScreen
import com.example.timekeeping.view_models.EmployeeViewModel
import com.example.timekeeping.view_models.GroupViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        addAuthScreens(navController)
        addShiftScreen(navController)
        addShiftFormScreen(navController)
        addGroupScreen(navController)
        addGroupFormScreen(navController)
        addEmployeeScreen(navController)

        addHomeScreen(navController)
        addCheckInScreen(navController)
        addProfileScreen()

        addRequestJoinGroup(navController)
    }
}

/* ------------------- Các hàm mở rộng cho từng màn hình ------------------- */


private fun NavGraphBuilder.addHomeScreen(navController: NavHostController) {
    composable(Screen.Home.route) {
        val viewModel: GroupViewModel = viewModel()
        HomeScreen(
            navController = navController,
            viewModel = viewModel
        )
    }
}

private fun NavGraphBuilder.addProfileScreen() {
    composable(Screen.Profile.route) {
        // ProfileScreen()
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