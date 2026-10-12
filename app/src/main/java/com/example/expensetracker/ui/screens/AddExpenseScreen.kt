
package com.example.expensetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.expenseCategories
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.viewmodel.ExpenseViewModel

private val ScreenBackground = Color(0xFFF8FAFC)
private val FieldBackground = Color.White
private val MainText = Color(0xFF0F172A)
private val SecondaryText = Color(0xFF64748B)
private val BorderColor = Color(0xFFCBD5E1)
private val AccentBlue = Color(0xFF2563EB)

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
    var categoryExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .imePadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add Transaction",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MainText
        )

        Text(
            text = "Record your income or expense",
            style = MaterialTheme.typography.bodyLarge,
            color = SecondaryText
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Income / Expense selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF26384D)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { isIncome = false },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isIncome) AccentBlue
                        else Color(0xFF26384D),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Expense",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = { isIncome = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isIncome) AccentBlue
                        else Color(0xFF26384D),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Income",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Amount
        OutlinedTextField(
            value = amount,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                    amount = newValue
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Amount")
            },
            placeholder = {
                Text("Enter amount")
            },
            prefix = {
                Text(
                    text = "₹",
                    color = MainText
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MainText,
                unfocusedTextColor = MainText,
                disabledTextColor = SecondaryText,
                focusedContainerColor = FieldBackground,
                unfocusedContainerColor = FieldBackground,
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = BorderColor,
                focusedLabelColor = AccentBlue,
                unfocusedLabelColor = SecondaryText,
                focusedPlaceholderColor = SecondaryText,
                unfocusedPlaceholderColor = SecondaryText,
                cursorColor = AccentBlue
            )
        )

        // Title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Title")
            },
            placeholder = {
                Text("e.g. Grocery shopping")
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MainText,
                unfocusedTextColor = MainText,
                disabledTextColor = SecondaryText,
                focusedContainerColor = FieldBackground,
                unfocusedContainerColor = FieldBackground,
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = BorderColor,
                focusedLabelColor = AccentBlue,
                unfocusedLabelColor = SecondaryText,
                focusedPlaceholderColor = SecondaryText,
                unfocusedPlaceholderColor = SecondaryText,
                cursorColor = AccentBlue
            )
        )

        // Category
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MainText
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { categoryExpanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MainText
                )
            ) {
                Text(
                    text = if (category.isEmpty()) {
                        "Select Category"
                    } else {
                        category
                    },
                    modifier = Modifier.weight(1f),
                    color = if (category.isEmpty()) SecondaryText else MainText
                )

                Text("⌄", color = SecondaryText)
            }


            DropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = {
                    categoryExpanded = false
                },
                containerColor = Color.White
            ) {
                expenseCategories.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                                color = Color(0xFF0F172A),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        onClick = {
                            category = item
                            categoryExpanded = false
                        }
                    )
                }
            }

        }

        // Note
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            label = {
                Text("Note")
            },
            placeholder = {
                Text("Optional")
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MainText,
                unfocusedTextColor = MainText,
                disabledTextColor = SecondaryText,
                focusedContainerColor = FieldBackground,
                unfocusedContainerColor = FieldBackground,
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = BorderColor,
                focusedLabelColor = AccentBlue,
                unfocusedLabelColor = SecondaryText,
                focusedPlaceholderColor = SecondaryText,
                unfocusedPlaceholderColor = SecondaryText,
                cursorColor = AccentBlue
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Save Transaction
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
                        category = category,
                        type = if (isIncome) "INCOME" else "EXPENSE",
                        date = System.currentTimeMillis(),
                        note = note.trim()
                    )

                    viewModel.addTransaction(transaction)
                    onTransactionSaved()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentBlue,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Save Transaction",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
