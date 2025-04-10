package com.example.timekeeping.navigation.groups

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.timekeeping.ui.groups.GroupFormScreen
import com.example.timekeeping.navigation.Screen
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
}