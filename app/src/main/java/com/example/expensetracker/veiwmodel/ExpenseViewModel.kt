package com.example.expensetracker.viewmodel

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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

    init {
        viewModelScope.launch {
            expenses.collect { list ->
                println("----- ALL TRANSACTIONS -----")
                list.forEach {
                    println(
                        "ID=${it.id}, title=${it.title}, " +
                                "amount=${it.amount}, type=${it.type}"
                    )
                }
            }
        }
    }

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
    val expensesByCategory: StateFlow<Map<String, Long>> =
        expenses
            .map { list ->
                list
                    .filter { it.type == "EXPENSE" }
                    .groupBy { it.category }
                    .mapValues { (_, transactions) ->
                        transactions.sumOf { it.amount }
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyMap()
            )
    val dailyExpenses: StateFlow<List<Pair<String, Long>>> =
        expenses
            .map { list ->
                val dateFormat = java.text.SimpleDateFormat(
                    "EEE",
                    java.util.Locale.getDefault()
                )

                (6 downTo 0).map { daysAgo ->
                    val day = java.util.Calendar.getInstance().apply {
                        add(java.util.Calendar.DAY_OF_YEAR, -daysAgo)
                        set(java.util.Calendar.HOUR_OF_DAY, 0)
                        set(java.util.Calendar.MINUTE, 0)
                        set(java.util.Calendar.SECOND, 0)
                        set(java.util.Calendar.MILLISECOND, 0)
                    }

                    val startOfDay = day.timeInMillis

                    val endOfDay = java.util.Calendar.getInstance().apply {
                        timeInMillis = startOfDay
                        add(java.util.Calendar.DAY_OF_YEAR, 1)
                    }.timeInMillis

                    val amount = list
                        .filter {
                            it.type == "EXPENSE" &&
                                    it.date >= startOfDay &&
                                    it.date < endOfDay
                        }
                        .sumOf { it.amount }

                    dateFormat.format(day.time) to amount
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    val balance: StateFlow<Long> =
        combine(
            totalIncome,
            totalExpenses
        ) { income, expenses ->
            income - expenses
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0L
        )

    fun addTransaction(transaction: ExpenseEntity) {
        viewModelScope.launch {
            repository.insertExpense(transaction)
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