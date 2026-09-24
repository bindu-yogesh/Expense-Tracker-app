package com.example.expensetracker

import com.example.expensetracker.ui.screens.DashboardScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.expensetracker.data.local.ExpenseDatabase
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.example.expensetracker.viewmodel.ExpenseViewModel
import com.example.expensetracker.viewmodel.ExpenseViewModelFactory

class MainActivity : ComponentActivity() {

    private val database by lazy {
        ExpenseDatabase.getDatabase(applicationContext)
    }

    private val repository by lazy {
        ExpenseRepository(database.expenseDao())
    }

    private val viewModel: ExpenseViewModel by viewModels {
        ExpenseViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ExpenseTrackerTheme {
                // Dashboard will be added here
                DashboardScreen(viewModel = viewModel)
            }
        }
    }
}