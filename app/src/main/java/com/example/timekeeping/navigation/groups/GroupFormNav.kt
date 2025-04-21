package com.example.timekeeping.navigation.groups

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.ui.groups.GroupFormScreen
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.groups.form.EditGroupForm
import com.example.timekeeping.view_models.GroupViewModel
import com.google.firebase.auth.FirebaseAuth

fun NavGraphBuilder.addGroupFormScreen(navController: NavHostController) {
    composable(Screen.GroupForm.route) {
        val auth = FirebaseAuth.getInstance()
        GroupFormScreen(
            onSubmit = { group ->
//                GroupViewModel().createGroup(group)
                navController.popBackStack()
            },
            creatorId = auth.currentUser?.uid ?: "",
            //onBackClick = { navController.popBackStack() }
        )
    }

    composable(
        route = Screen.EditGroup.route,
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ){
        backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""

        val groupViewModel : GroupViewModel = hiltViewModel()

        EditGroupForm(
            onSave = { group ->
                groupViewModel.updateGroup(group)
                navController.popBackStack()
            },
            groupId = groupId,
            onBackClick = { navController.popBackStack() }
        )
    }
}