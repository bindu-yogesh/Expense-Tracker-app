package com.example.expensetracker.ui.screens

import androidx.compose.material3.TextButton
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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.viewmodel.ExpenseViewModel

@Composable
fun TransactionHistoryScreen(
    viewModel: ExpenseViewModel,
    onBack: () -> Unit
) {
    val transactions by viewModel.expenses.collectAsStateWithLifecycle()

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
                        transaction = transaction
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionHistoryItem(
    transaction: ExpenseEntity
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

            Text(
                text = if (transaction.type == "INCOME") {
                    "+₹${transaction.amount}"
                } else {
                    "-₹${transaction.amount}"
                },
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}