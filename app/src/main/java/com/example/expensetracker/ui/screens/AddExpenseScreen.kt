
package com.example.expensetracker.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.expenseCategories
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.viewmodel.ExpenseViewModel
import java.text.NumberFormat
import java.util.Locale

// Baby-blue and white theme
private val AddBackground = Color(0xFFF3F8FC)
private val AddCard = Color(0xFFFFFFFF)
private val AddBorder = Color(0xFFE0EAF3)
private val AddText = Color(0xFF243B53)
private val AddMuted = Color(0xFF71849A)
private val AddBlue = Color(0xFF5AA9E6)
private val AddDeepBlue = Color(0xFF356F9F)
private val AddLightBlue = Color(0xFFE4F2FF)
private val AddIncome = Color(0xFF219A75)
private val AddExpense = Color(0xFFE16D75)
private val AddError = Color(0xFFC74350)

@Composable
fun AddExpenseScreen(
    viewModel: ExpenseViewModel,
    onTransactionSaved: () -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var isIncome by rememberSaveable { mutableStateOf(false) }

    var categoryExpanded by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val amountValue = amount.toLongOrNull()

    val titleError = submitted && title.isBlank()
    val amountError = submitted && (amountValue == null || amountValue <= 0L)
    val categoryError = submitted && category.isBlank()

    val transactionColor = if (isIncome) AddIncome else AddExpense

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AddBackground)
            .imePadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Header
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AddLightBlue
            ) {
                Text(
                    text = "NEW TRANSACTION",
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 7.dp
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = AddDeepBlue,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Add transaction",
                style = MaterialTheme.typography.headlineLarge,
                color = AddText,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Keep your finances organized, one entry at a time.",
                style = MaterialTheme.typography.bodyMedium,
                color = AddMuted
            )
        }

        // Transaction type selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = AddCard
            ),
            border = BorderStroke(1.dp, AddBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "TRANSACTION TYPE",
                    style = MaterialTheme.typography.labelMedium,
                    color = AddMuted,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TypeOption(
                        title = "Expense",
                        subtitle = "Money spent",
                        selected = !isIncome,
                        icon = Icons.Default.ArrowDownward,
                        selectedColor = AddExpense,
                        modifier = Modifier.weight(1f),
                        onClick = { isIncome = false }
                    )

                    TypeOption(
                        title = "Income",
                        subtitle = "Money received",
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
        FormSection(
            title = "Amount",
            subtitle = "Enter the amount in rupees"
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = { newValue ->
                    if (
                        newValue.isEmpty() ||
                        (newValue.all { it.isDigit() } &&
                                newValue.length <= 18)
                    ) {
                        amount = newValue
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("0", color = AddMuted)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = null
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
                isError = amountError,
                supportingText = if (amountError) {
                    {
                        Text(
                            "Enter a valid amount greater than ₹0.",
                            color = AddError
                        )
                    }
                } else {
                    null
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                shape = RoundedCornerShape(16.dp),
                colors = addFieldColors()
            )

            if (amountValue != null && amountValue > 0L) {
                Text(
                    text = "Amount: ${
                        NumberFormat.getCurrencyInstance(
                            Locale("en", "IN")
                        ).format(amountValue)
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = transactionColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Title input
        FormSection(
            title = "Transaction title",
            subtitle = "Give this transaction a recognizable name"
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "e.g. Grocery shopping",
                        color = AddMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null
                    )
                },
                singleLine = true,
                isError = titleError,
                supportingText = if (titleError) {
                    {
                        Text(
                            "Please enter a transaction title.",
                            color = AddError
                        )
                    }
                } else {
                    null
                },
                shape = RoundedCornerShape(16.dp),
                colors = addFieldColors()
            )
        }

        // Category selector
        FormSection(
            title = "Category",
            subtitle = "Choose the category that fits best"
        ) {
            Box {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = AddCard,
                    border = BorderStroke(
                        1.dp,
                        if (categoryError) AddError else AddBorder
                    ),
                    onClick = {
                        categoryExpanded = true
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 17.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = AddDeepBlue
                        )

                        Spacer(Modifier.width(12.dp))

                        Text(
                            text = category.ifEmpty {
                                "Select a category"
                            },
                            modifier = Modifier.weight(1f),
                            color = if (category.isEmpty()) {
                                AddMuted
                            } else {
                                AddText
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = if (categoryExpanded) "⌃" else "⌄",
                            color = AddMuted,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }

                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = {
                        categoryExpanded = false
                    },
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
                            leadingIcon = {
                                if (category == item) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = AddBlue
                                    )
                                }
                            },
                            onClick = {
                                category = item
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            if (categoryError) {
                Text(
                    text = "Please select a category.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AddError
                )
            }
        }

        // Optional note
        FormSection(
            title = "Note",
            subtitle = "Add details to help you remember this transaction"
        ) {
            OutlinedTextField(
                value = note,
                onValueChange = {
                    if (it.length <= 300) note = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = {
                    Text(
                        "e.g. Weekly groceries from the supermarket",
                        color = AddMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Notes,
                        contentDescription = null
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = addFieldColors(),
                maxLines = 4,
                supportingText = {
                    Text(
                        "${note.length}/300",
                        modifier = Modifier.fillMaxWidth(),
                        color = AddMuted
                    )
                }
            )
        }

        // Date information
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = AddLightBlue
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = AddDeepBlue,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Transaction date",
                        color = AddDeepBlue,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Recorded using the current date and time",
                        color = AddDeepBlue,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Save button
        Button(
            onClick = {
                submitted = true

                val validAmount = amount.toLongOrNull()

                if (
                    title.isNotBlank() &&
                    validAmount != null &&
                    validAmount > 0L &&
                    category.isNotBlank() &&
                    !isSaving
                ) {
                    isSaving = true

                    val transaction = ExpenseEntity(
                        title = title.trim(),
                        amount = validAmount,
                        category = category,
                        type = if (isIncome) "INCOME" else "EXPENSE",
                        date = System.currentTimeMillis(),
                        note = note.trim()
                    )

                    viewModel.addTransaction(transaction)
                    onTransactionSaved()
                }
            },
            enabled = !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(17.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AddBlue,
                contentColor = Color.White,
                disabledContainerColor = AddBlue.copy(alpha = 0.55f),
                disabledContentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )

            Spacer(Modifier.width(9.dp))

            Text(
                text = if (isSaving) {
                    "Saving transaction..."
                } else {
                    "Save transaction"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Your transaction will appear in your dashboard and history.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            style = MaterialTheme.typography.bodySmall,
            color = AddMuted
        )
    }
}

@Composable
private fun FormSection(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = AddText
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = AddMuted
        )

        content()
    }
}

@Composable
private fun TypeOption(
    title: String,
    subtitle: String,
    selected: Boolean,
    icon: ImageVector,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val containerColor = if (selected) {
        selectedColor.copy(alpha = 0.09f)
    } else {
        AddCard
    }

    val borderColor = if (selected) {
        selectedColor
    } else {
        AddBorder
    }

    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = BorderStroke(
            if (selected) 1.5.dp else 1.dp,
            borderColor
        )
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 15.dp
            ),
            verticalArrangement = Arrangement.spacedBy(7.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(selectedColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = selectedColor
                )
            }

            Text(
                text = title,
                color = if (selected) selectedColor else AddText,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = subtitle,
                color = AddMuted,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun addFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = AddText,
    unfocusedTextColor = AddText,
    cursorColor = AddBlue,
    focusedBorderColor = AddBlue,
    unfocusedBorderColor = AddBorder,
    errorBorderColor = AddError,
    errorCursorColor = AddError,
    focusedLabelColor = AddDeepBlue,
    unfocusedLabelColor = AddMuted,
    focusedContainerColor = AddCard,
    unfocusedContainerColor = AddCard,
    focusedLeadingIconColor = AddDeepBlue,
    unfocusedLeadingIconColor = AddMuted
)
