
package com.example.expensetracker.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

private val EditBackground = Color(0xFF0B1120)
private val EditCard = Color(0xFF151F32)
private val EditBorder = Color(0xFF29364B)
private val EditText = Color(0xFFF8FAFC)
private val EditMuted = Color(0xFF94A3B8)
private val EditBlue = Color(0xFF2563EB)
private val EditIncome = Color(0xFF4ADE80)
private val EditExpense = Color(0xFFF87171)

@Composable
fun EditTransactionScreen(
    transaction: ExpenseEntity,
    viewModel: ExpenseViewModel,
    onTransactionUpdated: () -> Unit,
    onBack: () -> Unit
) {
    var title by remember(transaction.id) {
        mutableStateOf(transaction.title)
    }
    var amount by remember(transaction.id) {
        mutableStateOf(transaction.amount.toString())
    }
    var category by remember(transaction.id) {
        mutableStateOf(transaction.category)
    }
    var note by remember(transaction.id) {
        mutableStateOf(transaction.note)
    }
    var isIncome by remember(transaction.id) {
        mutableStateOf(transaction.type == "INCOME")
    }
    var categoryExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EditBackground)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "TRANSACTION DETAILS",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF93C5FD),
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Edit transaction",
            style = MaterialTheme.typography.headlineLarge,
            color = EditText,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Make changes to your saved transaction.",
            style = MaterialTheme.typography.bodyLarge,
            color = EditMuted
        )

        // Income / Expense selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = EditCard
            ),
            border = BorderStroke(1.dp, EditBorder)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "TRANSACTION TYPE",
                    style = MaterialTheme.typography.labelMedium,
                    color = EditMuted,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    EditTypeOption(
                        title = "Expense",
                        selected = !isIncome,
                        icon = Icons.Default.ArrowDownward,
                        selectedColor = EditExpense,
                        modifier = Modifier.weight(1f),
                        onClick = { isIncome = false }
                    )

                    EditTypeOption(
                        title = "Income",
                        selected = isIncome,
                        icon = Icons.Default.ArrowUpward,
                        selectedColor = EditIncome,
                        modifier = Modifier.weight(1f),
                        onClick = { isIncome = true }
                    )
                }
            }
        }

        // Amount
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            EditFieldLabel("Amount")

            OutlinedTextField(
                value = amount,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        amount = newValue
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("0", color = EditMuted) },
                leadingIcon = {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = null,
                        tint = EditMuted
                    )
                },
                prefix = {
                    Text("₹ ", color = EditText, fontWeight = FontWeight.Bold)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                shape = RoundedCornerShape(16.dp),
                colors = editFieldColors()
            )
        }

        // Title
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            EditFieldLabel("Title")

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("e.g. Grocery shopping", color = EditMuted)
                },
                leadingIcon = {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = EditMuted
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = editFieldColors()
            )
        }

        // Category
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            EditFieldLabel("Category")

            Surface(
                modifier = Modifier.fillMaxWidth(),
                onClick = { categoryExpanded = true },
                shape = RoundedCornerShape(16.dp),
                color = EditCard,
                border = BorderStroke(1.dp, EditBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 17.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        tint = EditMuted
                    )

                    Spacer(Modifier.padding(start = 4.dp))

                    Text(
                        text = category.ifEmpty { "Select a category" },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        color = if (category.isEmpty()) EditMuted else EditText,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text("⌄", color = EditMuted)
                }
            }

            DropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false },
                containerColor = EditCard
            ) {
                expenseCategories.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(item, color = EditText)
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
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            EditFieldLabel("Note (optional)")

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = {
                    Text("Add extra details...", color = EditMuted)
                },
                leadingIcon = {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.Notes,
                        contentDescription = null,
                        tint = EditMuted
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = editFieldColors()
            )
        }

        Spacer(Modifier.height(2.dp))

        // Save changes
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
                        category = category,
                        type = if (isIncome) "INCOME" else "EXPENSE",
                        note = note.trim()
                    )

                    viewModel.updateExpense(updatedTransaction)
                    onTransactionUpdated()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = EditBlue,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Save changes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, EditBorder),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = EditText
            )
        ) {
            Text("Cancel")
        }

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun EditFieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = EditText
    )
}

@Composable
private fun EditTypeOption(
    title: String,
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (selected) selectedColor.copy(alpha = 0.12f) else EditBackground,
        border = BorderStroke(
            1.dp,
            if (selected) selectedColor else EditBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) selectedColor else EditMuted
            )

            Spacer(Modifier.padding(start = 3.dp))

            Text(
                text = title,
                color = if (selected) selectedColor else EditMuted,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun editFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = EditText,
    unfocusedTextColor = EditText,
    cursorColor = Color(0xFF93C5FD),
    focusedBorderColor = Color(0xFF60A5FA),
    unfocusedBorderColor = EditBorder,
    focusedLabelColor = Color(0xFF93C5FD),
    unfocusedLabelColor = EditMuted,
    focusedContainerColor = EditCard,
    unfocusedContainerColor = EditCard,
    focusedLeadingIconColor = EditMuted,
    unfocusedLeadingIconColor = EditMuted
)
