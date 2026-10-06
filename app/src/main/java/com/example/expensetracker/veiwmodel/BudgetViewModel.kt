package com.example.expensetracker.viewmodel

//import com.example.expensetracker.ui.screens.EditBudgetScreen
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

    val budgets: StateFlow<List<BudgetEntity>> =
        repository.allBudgets
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun addBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.insertBudget(budget)
        }
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

    fun getBudgetsForMonth(
        month: Int,
        year: Int
    ): StateFlow<List<BudgetEntity>> {

        return repository
            .getBudgetsForMonth(month, year)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
}