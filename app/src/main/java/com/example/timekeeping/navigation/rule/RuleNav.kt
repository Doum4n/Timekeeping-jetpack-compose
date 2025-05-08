package com.example.timekeeping.navigation.rule

import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.rule.RuleManagement
import com.example.timekeeping.view_models.RuleViewModel

fun NavGraphBuilder.addRuleNav(navController: NavController) {
    composable(
        route = Screen.RuleManagement.route,
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ){
        navBackStackEntry ->
        val groupId = navBackStackEntry.arguments?.getString("groupId") ?: ""

        val ruleViewModel = hiltViewModel<RuleViewModel>()

        RuleManagement(
            groupId = groupId,
            onBack = { navController.popBackStack() },
            onAddRuleClick = { navController.navigate(Screen.RuleInputScreen.createRoute(groupId)) },
            onEditRuleClick = { rule -> navController.navigate(Screen.RuleEditScreen.createRoute(groupId, rule.id)) },
            onDeleteRuleClick = {
                ruleViewModel.deleteRule(it.id, {
                    Toast.makeText(navController.context, "Xóa qui tắc thành công!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }, { exception ->
                    Toast.makeText(navController.context, "Xóa không thành công!: ${exception.message}", Toast.LENGTH_SHORT).show()
                })
            }
        )
    }
}