package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.expensetracker.data.local.ExpenseDatabase
import com.example.expensetracker.data.repository.BudgetRepository
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.ui.navigation.AppNavigation
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.example.expensetracker.viewmodel.BudgetViewModel
import com.example.expensetracker.viewmodel.BudgetViewModelFactory
import com.example.expensetracker.viewmodel.ExpenseViewModel
import com.example.expensetracker.viewmodel.ExpenseViewModelFactory

class MainActivity : ComponentActivity() {

    private val database by lazy {
        ExpenseDatabase.getDatabase(applicationContext)
    }

    private val expenseRepository by lazy {
        ExpenseRepository(database.expenseDao())
    }

    private val budgetRepository by lazy {
        BudgetRepository(database.budgetDao())
    }

    private val expenseViewModel: ExpenseViewModel by viewModels {
        ExpenseViewModelFactory(expenseRepository)
    }

    private val budgetViewModel: BudgetViewModel by viewModels {
        BudgetViewModelFactory(budgetRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ExpenseTrackerTheme {
                AppNavigation(
                    viewModel = expenseViewModel,
                    budgetViewModel = budgetViewModel
                )
            }
        }
    }
}