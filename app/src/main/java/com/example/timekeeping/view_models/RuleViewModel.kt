package com.example.timekeeping.view_models

import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.ConditionNode
import com.example.timekeeping.models.Rule
import com.example.timekeeping.repositories.RuleRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RuleViewModel @Inject constructor(
    private val ruleRepo: RuleRepo
) : ViewModel() {

    fun createRule(rule: Rule, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        ruleRepo.createRule(rule, onSuccess, onFailure);
    }

    fun getRule(ruleId: String, onSuccess: (Rule) -> Unit, onFailure: (Exception) -> Unit) {
        ruleRepo.getRule(ruleId, onSuccess, onFailure);
    }

    fun getRules(groupId: String, onSuccess: (List<Rule>) -> Unit, onFailure: (Exception) -> Unit) {
        ruleRepo.getRules(groupId, onSuccess, onFailure);
    }

    fun updateRule(rule: Rule, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        ruleRepo.updateRule(rule, onSuccess, onFailure);
    }

    fun deleteRule(ruleId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        ruleRepo.deleteRule(ruleId, onSuccess, onFailure);
    }
}