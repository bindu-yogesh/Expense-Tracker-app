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
import androidx.compose.material3.MaterialTheme
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        TextButton(
            onClick = onBack
        ) {
            Text("← Back")
        }

        Text(
            text = "All Transactions",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (transactions.isEmpty()) {

            Text(
                text = "No transactions yet",
                style = MaterialTheme.typography.bodyLarge
            )

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = transactions,
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

                Spacer(modifier = Modifier.height(4.dp))

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