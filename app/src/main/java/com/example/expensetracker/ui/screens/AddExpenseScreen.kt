package com.example.expensetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.viewmodel.ExpenseViewModel

@Composable
fun AddExpenseScreen(
    viewModel: ExpenseViewModel,
    onTransactionSaved: () -> Unit
) {

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    var isIncome by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Add Transaction",
            style = MaterialTheme.typography.headlineMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (!isIncome) {
                Button(
                    onClick = { isIncome = false },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Expense")
                }
            } else {
                OutlinedButton(
                    onClick = { isIncome = false },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Expense")
                }
            }

            if (isIncome) {
                Button(
                    onClick = { isIncome = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Income")
                }
            } else {
                OutlinedButton(
                    onClick = { isIncome = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Income")
                }
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {

                val amountValue = amount.toLongOrNull()

                if (
                    title.isNotBlank() &&
                    amountValue != null &&
                    amountValue > 0 &&
                    category.isNotBlank()
                ) {

                    val transaction = ExpenseEntity(
                        title = title.trim(),
                        amount = amountValue,
                        category = category.trim(),
                        type = if (isIncome) "INCOME" else "EXPENSE",
                        date = System.currentTimeMillis(),
                        note = note.trim()
                    )

                    viewModel.addTransaction(transaction)

                    onTransactionSaved()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Transaction")
        }
    }
}