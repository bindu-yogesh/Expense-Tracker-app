package com.example.expensetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.viewmodel.ExpenseViewModel

@Composable
fun EditTransactionScreen(
    transaction: ExpenseEntity,
    viewModel: ExpenseViewModel,
    onTransactionUpdated: () -> Unit,
    onBack: () -> Unit
) {

    var title by remember {
        mutableStateOf(transaction.title)
    }

    var amount by remember {
        mutableStateOf(transaction.amount.toString())
    }

    var category by remember {
        mutableStateOf(transaction.category)
    }

    var note by remember {
        mutableStateOf(transaction.note)
    }

    var isIncome by remember {
        mutableStateOf(transaction.type == "INCOME")
    }

    Column(
        modifier = Modifier
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Edit Transaction",
            style = MaterialTheme.typography.headlineMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (!isIncome) {
                Button(
                    onClick = {
                        isIncome = false
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Expense")
                }
            } else {
                OutlinedButton(
                    onClick = {
                        isIncome = false
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Expense")
                }
            }

            if (isIncome) {
                Button(
                    onClick = {
                        isIncome = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Income")
                }
            } else {
                OutlinedButton(
                    onClick = {
                        isIncome = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Income")
                }
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
            },
            label = {
                Text("Title")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = amount,
            onValueChange = {
                amount = it
            },
            label = {
                Text("Amount")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        OutlinedTextField(
            value = category,
            onValueChange = {
                category = it
            },
            label = {
                Text("Category")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = note,
            onValueChange = {
                note = it
            },
            label = {
                Text("Note")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {

                val amountValue = amount.toLongOrNull()

                if (
                    title.isNotBlank() &&
                    amountValue != null &&
                    amountValue > 0 &&
                    category.isNotBlank()
                ) {

                    val updatedTransaction = transaction.copy(
                        title = title.trim(),
                        amount = amountValue,
                        category = category.trim(),
                        type = if (isIncome) {
                            "INCOME"
                        } else {
                            "EXPENSE"
                        },
                        note = note.trim()
                    )

                    viewModel.updateExpense(updatedTransaction)

                    onTransactionUpdated()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Transaction")
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}