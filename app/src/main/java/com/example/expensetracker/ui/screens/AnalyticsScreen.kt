package com.example.expensetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.viewmodel.ExpenseViewModel

@Composable
fun AnalyticsScreen(
    viewModel: ExpenseViewModel,
    onBack: () -> Unit
) {
    val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
    val totalExpenses by viewModel.totalExpenses.collectAsStateWithLifecycle()
    val balance by viewModel.balance.collectAsStateWithLifecycle()
    val expensesByCategory by viewModel.expensesByCategory
        .collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "← Back",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "Analytics",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            AnalyticsCard(
                title = "Income",
                amount = totalIncome,
                modifier = Modifier.weight(1f)
            )

            AnalyticsCard(
                title = "Expenses",
                amount = totalExpenses,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        AnalyticsCard(
            title = "Balance",
            amount = balance,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        Text(
            text = "Expenses by Category",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (expensesByCategory.isEmpty()) {

            Text(
                text = "No expense data available",
                style = MaterialTheme.typography.bodyLarge
            )

        } else {

            expensesByCategory
                .toList()
                .sortedByDescending { it.second }
                .forEach { (category, amount) ->

                    CategoryExpenseItem(
                        category = category,
                        amount = amount
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                }
        }
    }
}

@Composable
private fun AnalyticsCard(
    title: String,
    amount: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "₹$amount",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
private fun CategoryExpenseItem(
    category: String,
    amount: Long
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

            Text(
                text = category,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "₹$amount",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}