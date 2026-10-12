package com.example.expensetracker.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.local.BudgetEntity
import com.example.expensetracker.data.repository.BudgetRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BudgetViewModel(
    private val repository: BudgetRepository
) : ViewModel() {

    val budgets: StateFlow<List<BudgetEntity>?> =
        repository.allBudgets.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    /**
     * Insert only after the caller has completed validation.
     * Because this is suspend, the caller can wait for Room
     * to finish before navigating away.
     */
    suspend fun addBudget(budget: BudgetEntity) {
        repository.insertBudget(budget)
    }

    suspend fun getBudgetForCategory(
        category: String,
        month: Int,
        year: Int
    ): BudgetEntity? {
        return repository.getBudgetForCategory(
            category = category,
            month = month,
            year = year
        )
    }

    fun updateBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.updateBudget(budget)
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
        }
    }

    private val monthlyBudgetFlows =
        mutableMapOf<Pair<Int, Int>, StateFlow<List<BudgetEntity>?>>()
    fun getBudgetsForMonth(
        month: Int,
        year: Int
    ): StateFlow<List<BudgetEntity>?> {
        return monthlyBudgetFlows.getOrPut(month to year) {
            repository
                .getBudgetsForMonth(month, year)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.Eagerly,
                    initialValue = null
                )
        }
    }
}
