package com.example.expensetracker.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.expensetracker.ui.screens.AddBudgetScreen
import com.example.expensetracker.ui.screens.AddExpenseScreen
import com.example.expensetracker.ui.screens.AnalyticsScreen
import com.example.expensetracker.ui.screens.BudgetScreen
import com.example.expensetracker.ui.screens.DashboardScreen
import com.example.expensetracker.ui.screens.EditBudgetScreen
import com.example.expensetracker.ui.screens.EditTransactionScreen
import com.example.expensetracker.ui.screens.TransactionHistoryScreen
import com.example.expensetracker.viewmodel.BudgetViewModel
import com.example.expensetracker.viewmodel.ExpenseViewModel

private val AppBackground = Color(0xFF0B1120)
private val BottomBarBackground = Color(0xFF101827)
private val AccentPurple = Color(0xFF8B5CF6)
private val MutedIcon = Color(0xFF94A3B8)
private val SelectedBackground = Color(0xFF292044)

private data class BottomDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val bottomDestinations = listOf(
    BottomDestination("dashboard", "Home", Icons.Default.Home),
    BottomDestination(
        "transaction_history",
        "Transactions",
        Icons.Default.ReceiptLong
    ),
    BottomDestination("budget", "Budgets", Icons.Default.Wallet),
    BottomDestination("analytics", "Analytics", Icons.Default.Analytics)
)

@Composable
fun AppNavigation(
    viewModel: ExpenseViewModel,
    budgetViewModel: BudgetViewModel
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val bottomRoutes = bottomDestinations.map { it.route }
    val showBottomBar = currentDestination?.route in bottomRoutes

    Scaffold(
        containerColor = AppBackground,
        bottomBar = {
            if (showBottomBar) {
                CustomBottomBar(
                    selectedRoute = currentDestination?.route,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(
                                navController.graph.findStartDestination().id
                            ) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onAddClick = {
                        navController.navigate("add_transaction")
                    }
                )
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
                .padding(paddingValues)
        ) {
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
                    },
                    onViewBudgets = {
                        navController.navigate("budget")
                    }
                )
            }

            composable("analytics") {
                AnalyticsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("budget") {
                BudgetScreen(
                    budgetViewModel = budgetViewModel,
                    expenseViewModel = viewModel,
                    onBack = { navController.popBackStack() },
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
            ) { entry ->
                val budgetId = entry.arguments?.getLong("budgetId")

                val budgets by budgetViewModel.budgets
                    .collectAsStateWithLifecycle()

                val budget = budgets.orEmpty().find {
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
            ) { entry ->
                val transactionId =
                    entry.arguments?.getLong("transactionId")

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
}

@Composable
private fun CustomBottomBar(
    selectedRoute: String?,
    onNavigate: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BottomBarBackground)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavigationItem(
            destination = bottomDestinations[0],
            selected = selectedRoute == bottomDestinations[0].route,
            onClick = { onNavigate(bottomDestinations[0].route) },
            modifier = Modifier.weight(1f)
        )

        BottomNavigationItem(
            destination = bottomDestinations[1],
            selected = selectedRoute == bottomDestinations[1].route,
            onClick = { onNavigate(bottomDestinations[1].route) },
            modifier = Modifier.weight(1f)
        )

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FloatingActionButton(
                onClick = onAddClick,
                modifier = Modifier.size(52.dp),
                shape = CircleShape,
                containerColor = AccentPurple,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add transaction",
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Add",
                color = MutedIcon,
                fontSize = 11.sp
            )
        }

        BottomNavigationItem(
            destination = bottomDestinations[2],
            selected = selectedRoute == bottomDestinations[2].route,
            onClick = { onNavigate(bottomDestinations[2].route) },
            modifier = Modifier.weight(1f)
        )

        BottomNavigationItem(
            destination = bottomDestinations[3],
            selected = selectedRoute == bottomDestinations[3].route,
            onClick = { onNavigate(bottomDestinations[3].route) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BottomNavigationItem(
    destination: BottomDestination,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemColor = if (selected) AccentPurple else MutedIcon

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 54.dp, height = 30.dp)
                .background(
                    color = if (selected) {
                        SelectedBackground
                    } else {
                        Color.Transparent
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = destination.icon,
                contentDescription = destination.label,
                tint = itemColor,
                modifier = Modifier.size(21.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = destination.label,
            color = itemColor,
            fontSize = 10.sp,
            fontWeight = if (selected) {
                FontWeight.SemiBold
            } else {
                FontWeight.Normal
            },
            maxLines = 1
        )
    }
}