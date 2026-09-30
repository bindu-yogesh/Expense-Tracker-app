package com.example.expensetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.expensetracker.ui.screens.AddExpenseScreen
import com.example.expensetracker.ui.screens.AnalyticsScreen
import com.example.expensetracker.ui.screens.DashboardScreen
import com.example.expensetracker.ui.screens.EditTransactionScreen
import com.example.expensetracker.ui.screens.TransactionHistoryScreen
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

        // Dashboard
        composable("dashboard") {

            DashboardScreen(
                viewModel = viewModel,

                onAddTransaction = {
                    navController.navigate("add_transaction")
                },

                onViewAllTransactions = {
                    navController.navigate("transaction_history")
                },

                onViewAnalytics = {
                    navController.navigate("analytics")
                }
            )
        }

        // Analytics
        composable("analytics") {

            AnalyticsScreen(
                viewModel = viewModel,

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Add Transaction
        composable("add_transaction") {

            AddExpenseScreen(
                viewModel = viewModel,

                onTransactionSaved = {
                    navController.popBackStack()
                }
            )
        }

        // Transaction History
        composable("transaction_history") {

            TransactionHistoryScreen(
                viewModel = viewModel,

                onBack = {
                    navController.popBackStack()
                },

                onEdit = { transactionId ->
                    navController.navigate(
                        "edit_transaction/$transactionId"
                    )
                }
            )
        }

        // Edit Transaction
        composable(
            route = "edit_transaction/{transactionId}",

            arguments = listOf(
                navArgument("transactionId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val transactionId =
                backStackEntry.arguments?.getLong("transactionId")

            val transactions by viewModel.expenses
                .collectAsStateWithLifecycle()

            val transaction = transactions.find {
                it.id == transactionId
            }

            if (transaction != null) {

                EditTransactionScreen(
                    transaction = transaction,
                    viewModel = viewModel,

                    onTransactionUpdated = {
                        navController.popBackStack()
                    },

                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}