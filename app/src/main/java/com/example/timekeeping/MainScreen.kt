package com.example.timekeeping

import ShiftManagementScreen
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.timekeeping.view_models.EmployeeViewModel
import com.example.timekeeping.view_models.GroupViewModel
import com.example.timekeeping.view_models.ShiftViewModel
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    // Chỉ khởi tạo navController một lần ở cấp cao nhất
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("App Title") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                // Truyền navController đã khởi tạo vào AppNavigation
                AppNavigation(navController = navController)
            }
        }
    )
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Profile : Screen("profile")
    object GroupDetail : Screen("groupDetail/{groupId}") {
        fun createRoute(groupId: String) = "groupDetail/$groupId"
    }
    object EmployeeManagement : Screen("employeeManagement/{groupId}") {
        fun createRoute(groupId: String) = "employeeManagement/$groupId"
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
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            val viewModel: GroupViewModel = viewModel()
            HomeScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable(Screen.Profile.route) {
            // ProfileScreen()
        }
        composable(
            Screen.CheckIn.route,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) {
            val groupId = it.arguments?.getString("groupId") ?: ""
             CheckInScreen(
                 groupId = groupId,
                 onBackClick = { navController.popBackStack() }
             )
        }
        composable(Screen.GroupForm.route) {
            val auth = FirebaseAuth.getInstance()
            GroupFormScreen(
                onSubmit = { group ->
                    GroupViewModel().createGroup(group)
                    navController.popBackStack()
                },
                creatorId = auth.currentUser?.uid ?: ""
            )
        }
        composable(
            Screen.ShiftInputForm.route,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) {
            val groupId = it.arguments?.getString("groupId") ?: ""

            ShiftInputForm(
                onSave = { shift ->
                    ShiftViewModel(groupId = groupId).create(shift)
                    navController.popBackStack()
                },
                groupId = groupId,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            Screen.ShiftManagement.route,
            arguments = listOf(
                navArgument("groupId") { type = NavType.StringType },
            )
        ) {
            val groupId = it.arguments?.getString("groupId") ?: ""
            ShiftManagementScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = ShiftViewModel(
                    groupId = groupId
                ),
                onAddShiftClick = {
                    navController.navigate(Screen.ShiftInputForm.createRoute(groupId))
                },
                onEditClick = { shiftId ->
                    navController.navigate(Screen.ShiftEditForm.createRoute(groupId, shiftId))
                }
            )
        }
        composable(
            Screen.ShiftEditForm.route,
            arguments = listOf(
                navArgument("groupId") { type = NavType.StringType },
                navArgument("shiftId") { type = NavType.StringType })
        ){
            val groupId = it.arguments?.getString("groupId") ?: ""
            val shiftId = it.arguments?.getString("shiftId") ?: ""

            Log.d("ShiftEditForm", "groupId: $groupId, shiftId: $shiftId")

            ShiftInputForm(
                onSave = { shift ->
                    ShiftViewModel(groupId = groupId).update(shiftId, shift)
                    navController.popBackStack()
                },
                groupId = groupId,
                shiftId = shiftId,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            Screen.GroupDetail.route,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) {
             val groupId = it.arguments?.getString("groupId") ?: ""
             GroupDetailScreen(
                 groupId = groupId,
                 onEmployeeManagementClick = { navController.navigate(Screen.EmployeeManagement.createRoute(groupId)) },
                 onBackClick = { navController.popBackStack() },
                 onShiftManagementClick = { navController.navigate(Screen.ShiftManagement.createRoute(groupId)) },
                 onCheckInClick = { navController.navigate(Screen.CheckIn.createRoute(groupId)) }
             )
        }
        composable(
            Screen.EmployeeManagement.route,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) {
            val groupId = it.arguments?.getString("groupId") ?: ""
            EmployeeManagementScreen(
                onBackClick = { navController.popBackStack() },
                onMenuItemClick = { menuItem ->
                    when (menuItem) {
                        MenuItem.SEARCH -> { /* Handle search */
                        }

                        MenuItem.MORE -> {}
                    }
                },
                viewModel = EmployeeViewModel(groupId)
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 8.dp
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = when (screen) {
                            is Screen.Home -> Icons.Default.Home
                            is Screen.Profile -> Icons.Default.Person
                            else -> Icons.Default.Home
                        },
                        contentDescription = screen.route
                    )
                },
                label = { Text(screen.route) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Cấu hình navigation hợp lý
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}