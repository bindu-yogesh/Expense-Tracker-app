package com.example.expensetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    val expenses: StateFlow<List<ExpenseEntity>> =
        repository.allExpenses
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val totalIncome: StateFlow<Long> =
        expenses
            .map { list ->
                list
                    .filter { it.type == "INCOME" }
                    .sumOf { it.amount }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0L
            )

    val totalExpenses: StateFlow<Long> =
        expenses
            .map { list ->
                list
                    .filter { it.type == "EXPENSE" }
                    .sumOf { it.amount }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0L
            )

    val balance: StateFlow<Long> =
        kotlinx.coroutines.flow.combine(
            totalIncome,
            totalExpenses
        ) { income, expenses ->
            income - expenses
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0L
        )

    fun addExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.insertExpense(expense)
        }
    }

    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun deleteAllExpenses() {
        viewModelScope.launch {
            repository.deleteAllExpenses()
        }
    }
}