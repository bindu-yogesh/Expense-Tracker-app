
package com.example.expensetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
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

private val AddBackground = Color(0xFF0B1120)
private val AddCard = Color(0xFF151F32)
private val AddBorder = Color(0xFF29364B)
private val AddText = Color(0xFFF8FAFC)
private val AddMuted = Color(0xFF94A3B8)
private val AddBlue = Color(0xFF2563EB)
private val AddIncome = Color(0xFF4ADE80)
private val AddExpense = Color(0xFFF87171)

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
            .background(AddBackground)
            .imePadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "NEW TRANSACTION",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF93C5FD),
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Add transaction",
            style = MaterialTheme.typography.headlineLarge,
            color = AddText,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Record your income or expenses.",
            style = MaterialTheme.typography.bodyLarge,
            color = AddMuted
        )

        // Transaction type selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = AddCard
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                AddBorder
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "TRANSACTION TYPE",
                    style = MaterialTheme.typography.labelMedium,
                    color = AddMuted,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TypeOption(
                        title = "Expense",
                        selected = !isIncome,
                        icon = Icons.Default.ArrowDownward,
                        selectedColor = AddExpense,
                        modifier = Modifier.weight(1f),
                        onClick = { isIncome = false }
                    )

                    TypeOption(
                        title = "Income",
                        selected = isIncome,
                        icon = Icons.Default.ArrowUpward,
                        selectedColor = AddIncome,
                        modifier = Modifier.weight(1f),
                        onClick = { isIncome = true }
                    )
                }
            }
        }

        // Amount input
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FieldLabel("Amount")

            OutlinedTextField(
                value = amount,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        amount = newValue
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("0", color = AddMuted) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = null,
                        tint = AddMuted
                    )
                },
                prefix = {
                    Text(
                        text = "₹ ",
                        color = AddText,
                        fontWeight = FontWeight.Bold
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                shape = RoundedCornerShape(16.dp),
                colors = addFieldColors()
            )
        }

        // Title input
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FieldLabel("Title")

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("e.g. Grocery shopping", color = AddMuted)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = AddMuted
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = addFieldColors()
            )
        }

        // Category selector
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FieldLabel("Category")

            Box {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = AddCard,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        AddBorder
                    ),
                    onClick = { categoryExpanded = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 17.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = AddMuted
                        )

                        Spacer(Modifier.width(12.dp))

                        Text(
                            text = category.ifEmpty { "Select a category" },
                            modifier = Modifier.weight(1f),
                            color = if (category.isEmpty()) AddMuted else AddText,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = "⌄",
                            color = AddMuted,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }

                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                    containerColor = AddCard
                ) {
                    expenseCategories.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = item,
                                    color = AddText
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
        }

        // Optional note
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FieldLabel("Note (optional)")
            }

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = {
                    Text("Add extra details...", color = AddMuted)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Notes,
                        contentDescription = null,
                        tint = AddMuted
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = addFieldColors()
            )
        }

        Spacer(Modifier.height(2.dp))

        // Save transaction
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
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AddBlue,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "Save transaction",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = AddText
    )
}

@Composable
private fun TypeOption(
    title: String,
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val background = if (selected) {
        selectedColor.copy(alpha = 0.12f)
    } else {
        AddBackground
    }

    val border = if (selected) selectedColor else AddBorder
    val contentColor = if (selected) selectedColor else AddMuted

    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = background,
        border = androidx.compose.foundation.BorderStroke(1.dp, border)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor
            )

            Spacer(Modifier.width(7.dp))

            Text(
                text = title,
                color = contentColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun addFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = AddText,
    unfocusedTextColor = AddText,
    cursorColor = Color(0xFF93C5FD),
    focusedBorderColor = Color(0xFF60A5FA),
    unfocusedBorderColor = AddBorder,
    focusedLabelColor = Color(0xFF93C5FD),
    unfocusedLabelColor = AddMuted,
    focusedContainerColor = AddCard,
    unfocusedContainerColor = AddCard,
    focusedLeadingIconColor = AddMuted,
    unfocusedLeadingIconColor = AddMuted
)
