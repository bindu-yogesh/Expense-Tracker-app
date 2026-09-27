package com.example.expensetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.ui.screens.AddExpenseScreen
import com.example.expensetracker.ui.screens.DashboardScreen
import com.example.expensetracker.viewmodel.ExpenseViewModel

@Composable
fun AppNavigation(
    viewModel: ExpenseViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {

        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel
            )
        }

        composable("add_transaction") {
            AddExpenseScreen(
                viewModel = viewModel,
                onTransactionSaved = {
                    navController.popBackStack()
                }
            )
        }
    }
}