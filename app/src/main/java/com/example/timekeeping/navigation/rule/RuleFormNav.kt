package com.example.timekeeping.navigation.rule

import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.ui.rule.RuleInputScreen
import com.example.timekeeping.view_models.RuleViewModel

fun NavGraphBuilder.addRuleInputNav(navController: NavController) {
    composable(
        route = Screen.RuleInputScreen.route,
        arguments = listOf(
            navArgument("groupId") { type = NavType.StringType }
        )
    ){ backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""

        val ruleViewModel = hiltViewModel<RuleViewModel>()

        RuleInputScreen(
            groupId = groupId,
            onBack = navController::popBackStack,
            onSave = { rule ->
                ruleViewModel.createRule(rule, {
                    Toast.makeText(
                        navController.context,
                        "Rule created successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }, { exception ->
                    Toast.makeText(
                        navController.context,
                        "Failed to create rule: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                })
                navController.popBackStack()
            }
        )
    }

    composable(
        route = Screen.RuleEditScreen.route,
        arguments = listOf(
            navArgument("groupId") { type = NavType.StringType },
            navArgument("ruleId") { type = NavType.StringType }
            )
    ) { backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        val ruleId = backStackEntry.arguments?.getString("ruleId") ?: ""

        val ruleViewModel = hiltViewModel<RuleViewModel>()

        RuleInputScreen(
                ruleId = ruleId,
                groupId = groupId,
                onBack = navController::popBackStack,
                onSave = { rule ->
                    ruleViewModel.updateRule(rule, {
                        Toast.makeText(navController.context, "Rule updated successfully", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }, { exception ->
                        Toast.makeText(navController.context, "Failed to update rule: ${exception.message}", Toast.LENGTH_SHORT).show()
                    })
                }
        )
    }
}