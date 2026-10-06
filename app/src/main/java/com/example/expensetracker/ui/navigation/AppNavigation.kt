package com.example.expensetracker.ui.navigation

import com.example.expensetracker.ui.screens.EditBudgetScreen
import com.example.expensetracker.ui.screens.AddBudgetScreen
import com.example.expensetracker.ui.screens.BudgetScreen
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
import com.example.expensetracker.viewmodel.BudgetViewModel
import com.example.expensetracker.viewmodel.ExpenseViewModel

@Composable
fun AppNavigation(
    viewModel: ExpenseViewModel,
    budgetViewModel: BudgetViewModel
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {

        composable("dashboard") {DashboardScreen(
            viewModel = viewModel,
            onAddTransaction = {
                navController.navigate("add_transaction")
            },
            onViewAllTransactions = {
                navController.navigate("transaction_history")
            },
            onViewAnalytics = {
                navController.navigate("analytics")
            },
            onViewBudgets = {
                navController.navigate("budget")
            }
        )
        }

        composable("analytics") {
            AnalyticsScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("budget") {
            BudgetScreen(
                budgetViewModel = budgetViewModel,
                expenseViewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                },
                onAddBudget = {
                    navController.navigate("add_budget")
                },
                onEditBudget = { budgetId ->
                    navController.navigate("edit_budget/$budgetId")
                }
            )
        }

        composable(
            route = "edit_budget/{budgetId}",
            arguments = listOf(
                navArgument("budgetId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val budgetId =
                backStackEntry.arguments?.getLong("budgetId")

            val budgets by budgetViewModel.budgets
                .collectAsStateWithLifecycle()

            val budget = budgets.find {
                it.id == budgetId
            }

            if (budget != null) {

                EditBudgetScreen(
                    budget = budget,
                    budgetViewModel = budgetViewModel,
                    onBudgetUpdated = {
                        navController.popBackStack()
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable("add_budget") {
            AddBudgetScreen(
                budgetViewModel = budgetViewModel,
                onBudgetSaved = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
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