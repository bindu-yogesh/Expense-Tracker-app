package com.example.expensetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.data.expenseCategories
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.viewmodel.ExpenseViewModel

@Composable
fun TransactionHistoryScreen(
    viewModel: ExpenseViewModel,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit
) {
    val transactions by viewModel.expenses.collectAsStateWithLifecycle()

    var transactionToDelete by remember {
        mutableStateOf<ExpenseEntity?>(null)
    }

    var searchQuery by remember {
        mutableStateOf("")
    }

    var selectedCategory by remember {
        mutableStateOf("All Categories")
    }

    var categoryExpanded by remember {
        mutableStateOf(false)
    }

    var selectedType by remember {
        mutableStateOf("ALL")
    }

    var typeExpanded by remember {
        mutableStateOf(false)
    }

    // Filter transactions
    val filteredTransactions = transactions.filter { transaction ->

        val query = searchQuery.trim()

        val matchesSearch =
            query.isEmpty() ||
                    transaction.title.contains(
                        query,
                        ignoreCase = true
                    ) ||
                    transaction.category.contains(
                        query,
                        ignoreCase = true
                    )

        val matchesCategory =
            selectedCategory == "All Categories" ||
                    transaction.category == selectedCategory

        val matchesType =
            selectedType == "ALL" ||
                    transaction.type == selectedType

        matchesSearch &&
                matchesCategory &&
                matchesType
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        // Back button
        TextButton(
            onClick = onBack
        ) {
            Text("← Back")
        }

        Text(
            text = "All Transactions",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            label = {
                Text("Search transactions")
            },
            placeholder = {
                Text("Search by title or category")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // Category filter
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedButton(
                onClick = {
                    categoryExpanded = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedCategory)
            }

            DropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = {
                    categoryExpanded = false
                }
            ) {

                DropdownMenuItem(
                    text = {
                        Text("All Categories")
                    },
                    onClick = {
                        selectedCategory = "All Categories"
                        categoryExpanded = false
                    }
                )

                expenseCategories.forEach { category ->

                    DropdownMenuItem(
                        text = {
                            Text(category)
                        },
                        onClick = {
                            selectedCategory = category
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // Income / Expense filter
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedButton(
                onClick = {
                    typeExpanded = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    when (selectedType) {
                        "INCOME" -> "Income"
                        "EXPENSE" -> "Expense"
                        else -> "All Types"
                    }
                )
            }

            DropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = {
                    typeExpanded = false
                }
            ) {

                DropdownMenuItem(
                    text = {
                        Text("All Types")
                    },
                    onClick = {
                        selectedType = "ALL"
                        typeExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Income")
                    },
                    onClick = {
                        selectedType = "INCOME"
                        typeExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Expense")
                    },
                    onClick = {
                        selectedType = "EXPENSE"
                        typeExpanded = false
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // Transaction list
        if (filteredTransactions.isEmpty()) {

            Text(
                text = if (transactions.isEmpty()) {
                    "No transactions yet"
                } else {
                    "No matching transactions"
                },
                style = MaterialTheme.typography.bodyLarge
            )

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = filteredTransactions,
                    key = { it.id }
                ) { transaction ->

                    TransactionHistoryItem(
                        transaction = transaction,

                        onEdit = {
                            onEdit(transaction.id)
                        },

                        onDelete = {
                            transactionToDelete = transaction
                        }
                    )
                }
            }
        }
    }

    // Delete confirmation dialog
    if (transactionToDelete != null) {

        AlertDialog(
            onDismissRequest = {
                transactionToDelete = null
            },

            title = {
                Text("Delete Transaction?")
            },

            text = {
                Text(
                    "Are you sure you want to delete " +
                            "\"${transactionToDelete?.title}\"?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        transactionToDelete?.let { transaction ->
                            viewModel.deleteExpense(transaction)
                        }

                        transactionToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        transactionToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TransactionHistoryItem(
    transaction: ExpenseEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {

                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column {

                Text(
                    text = if (transaction.type == "INCOME") {
                        "+₹${transaction.amount}"
                    } else {
                        "-₹${transaction.amount}"
                    },
                    style = MaterialTheme.typography.titleMedium
                )

                TextButton(
                    onClick = onEdit
                ) {
                    Text("Edit")
                }

                TextButton(
                    onClick = onDelete
                ) {
                    Text("Delete")
                }
            }
        }
    }
}