package com.example.expensetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.local.BudgetEntity
import com.example.expensetracker.viewmodel.BudgetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBudgetScreen(
    budget: BudgetEntity,
    budgetViewModel: BudgetViewModel,
    onBudgetUpdated: () -> Unit,
    onBack: () -> Unit
) {

    var amountText by remember {
        mutableStateOf(budget.amount.toString())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Edit Budget")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Category: ${budget.category}"
            )

            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it.filter { char ->
                        char.isDigit()
                    }
                },
                label = {
                    Text("Monthly Budget")
                },
                prefix = {
                    Text("₹")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {

                    val amount = amountText.toLongOrNull()

                    if (amount != null && amount > 0) {

                        val updatedBudget = budget.copy(
                            amount = amount
                        )

                        budgetViewModel.updateBudget(
                            updatedBudget
                        )

                        onBudgetUpdated()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Budget")
            }
        }
    }
}