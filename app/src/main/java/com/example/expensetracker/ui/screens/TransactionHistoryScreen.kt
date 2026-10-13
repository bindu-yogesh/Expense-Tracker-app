
package com.example.expensetracker.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.data.expenseCategories
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val HistoryBackground = Color(0xFFF3F8FC)
private val HistoryCard = Color(0xFFFFFFFF)
private val HistoryBorder = Color(0xFFE0EAF3)
private val HistoryText = Color(0xFF243B53)
private val HistoryMuted = Color(0xFF71849A)
private val HistoryBlue = Color(0xFF5AA9E6)
private val HistoryDeepBlue = Color(0xFF356F9F)
private val HistoryLightBlue = Color(0xFFE4F2FF)
private val HistoryGreen = Color(0xFF219A75)
private val HistoryRed = Color(0xFFE16D75)

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

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All Categories") }
    var selectedType by remember { mutableStateOf("ALL") }
    var categoryExpanded by remember { mutableStateOf(false) }

    val filteredTransactions = remember(
        transactions,
        searchQuery,
        selectedCategory,
        selectedType
    ) {
        transactions
            .filter { transaction ->
                val query = searchQuery.trim()

                val matchesSearch = query.isEmpty() ||
                        transaction.title.contains(query, ignoreCase = true) ||
                        transaction.category.contains(query, ignoreCase = true) ||
                        transaction.note.contains(query, ignoreCase = true)

                val matchesCategory =
                    selectedCategory == "All Categories" ||
                            transaction.category == selectedCategory

                val matchesType =
                    selectedType == "ALL" ||
                            transaction.type.equals(selectedType, ignoreCase = true)

                matchesSearch && matchesCategory && matchesType
            }
            .sortedByDescending { it.date }
    }

    val filteredIncome = filteredTransactions
        .filter { it.type.equals("INCOME", ignoreCase = true) }
        .sumOf { it.amount }

    val filteredExpenses = filteredTransactions
        .filter { it.type.equals("EXPENSE", ignoreCase = true) }
        .sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HistoryBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 20.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = HistoryText
                )
            }

            Spacer(Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Transactions",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = HistoryText
                )

                Text(
                    text = "Every rupee, clearly tracked",
                    style = MaterialTheme.typography.bodyMedium,
                    color = HistoryMuted
                )
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = HistoryLightBlue
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = HistoryDeepBlue,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = "${transactions.size}",
                        fontWeight = FontWeight.Bold,
                        color = HistoryDeepBlue
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 4.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Search
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    placeholder = {
                        Text(
                            "Search title, category or note",
                            color = HistoryMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = HistoryMuted
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = HistoryMuted
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = HistoryText,
                        unfocusedTextColor = HistoryText,
                        focusedContainerColor = HistoryCard,
                        unfocusedContainerColor = HistoryCard,
                        focusedBorderColor = HistoryBlue,
                        unfocusedBorderColor = HistoryBorder,
                        cursorColor = HistoryBlue,
                        focusedLeadingIconColor = HistoryBlue,
                        unfocusedLeadingIconColor = HistoryMuted
                    )
                )
            }

            // Income and expense summary for the current filter
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HistorySummaryCard(
                        title = "Income",
                        amount = filteredIncome,
                        icon = Icons.Default.ArrowUpward,
                        amountColor = HistoryGreen,
                        modifier = Modifier.weight(1f)
                    )

                    HistorySummaryCard(
                        title = "Expenses",
                        amount = filteredExpenses,
                        icon = Icons.Default.ArrowDownward,
                        amountColor = HistoryRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Transaction type filters
            item {
                Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    Text(
                        text = "Transaction type",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = HistoryText
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HistoryTypeChip(
                            label = "All",
                            selected = selectedType == "ALL",
                            onClick = { selectedType = "ALL" }
                        )

                        HistoryTypeChip(
                            label = "Expenses",
                            selected = selectedType == "EXPENSE",
                            onClick = { selectedType = "EXPENSE" }
                        )

                        HistoryTypeChip(
                            label = "Income",
                            selected = selectedType == "INCOME",
                            onClick = { selectedType = "INCOME" }
                        )
                    }
                }
            }

            // Category filter
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = HistoryText
                    )

                    Box {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            color = HistoryCard,
                            border = BorderStroke(1.dp, HistoryBorder),
                            onClick = { categoryExpanded = true }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 15.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Category,
                                    contentDescription = null,
                                    tint = HistoryBlue
                                )

                                Spacer(Modifier.width(10.dp))

                                Text(
                                    text = selectedCategory,
                                    modifier = Modifier.weight(1f),
                                    color = HistoryText,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = "Choose category",
                                    tint = HistoryMuted
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            containerColor = HistoryCard
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Categories", color = HistoryText) },
                                leadingIcon = {
                                    if (selectedCategory == "All Categories") {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = HistoryBlue
                                        )
                                    }
                                },
                                onClick = {
                                    selectedCategory = "All Categories"
                                    categoryExpanded = false
                                }
                            )

                            expenseCategories.forEach { category ->
                                DropdownMenuItem(
                                    text = {
                                        Text(category, color = HistoryText)
                                    },
                                    leadingIcon = {
                                        if (selectedCategory == category) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = null,
                                                tint = HistoryBlue
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedCategory = category
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Result count and reset filters
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Your activity",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = HistoryText
                        )

                        Text(
                            text = "${filteredTransactions.size} transaction(s) found",
                            style = MaterialTheme.typography.bodySmall,
                            color = HistoryMuted
                        )
                    }

                    if (
                        searchQuery.isNotEmpty() ||
                        selectedCategory != "All Categories" ||
                        selectedType != "ALL"
                    ) {
                        TextButton(
                            onClick = {
                                searchQuery = ""
                                selectedCategory = "All Categories"
                                selectedType = "ALL"
                            }
                        ) {
                            Text(
                                "Reset filters",
                                color = HistoryDeepBlue
                            )
                        }
                    }
                }
            }

            // Transaction list
            if (filteredTransactions.isEmpty()) {
                item {
                    EmptyTransactionHistory(
                        hasTransactions = transactions.isNotEmpty(),
                        onReset = {
                            searchQuery = ""
                            selectedCategory = "All Categories"
                            selectedType = "ALL"
                        }
                    )
                }
            } else {
                items(
                    items = filteredTransactions,
                    key = { it.id }
                ) { transaction ->
                    TransactionHistoryItem(
                        transaction = transaction,
                        onEdit = { onEdit(transaction.id) },
                        onDelete = { transactionToDelete = transaction }
                    )
                }
            }
        }
    }

    // Confirm before deleting a transaction
    transactionToDelete?.let { transaction ->
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            containerColor = HistoryCard,
            title = {
                Text(
                    "Delete transaction?",
                    color = HistoryText,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete \"${transaction.title}\"? This action cannot be undone.",
                    color = HistoryMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteExpense(transaction)
                        transactionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HistoryRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("Cancel", color = HistoryDeepBlue)
                }
            }
        )
    }
}

@Composable
private fun HistorySummaryCard(
    title: String,
    amount: Long,
    icon: ImageVector,
    amountColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HistoryCard),
        border = BorderStroke(1.dp, HistoryBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(amountColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = amountColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = title,
                    color = HistoryMuted,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Text(
                text = "₹$amount",
                color = amountColor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
private fun HistoryTypeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else {
            null
        },
        shape = RoundedCornerShape(12.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = HistoryLightBlue,
            selectedLabelColor = HistoryDeepBlue,
            selectedLeadingIconColor = HistoryDeepBlue,
            containerColor = HistoryCard,
            labelColor = HistoryMuted,
            iconColor = HistoryMuted
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = HistoryBorder,
            selectedBorderColor = HistoryBlue
        )
    )
}

@Composable
private fun TransactionHistoryItem(
    transaction: ExpenseEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isIncome = transaction.type.equals("INCOME", ignoreCase = true)
    val amountColor = if (isIncome) HistoryGreen else HistoryRed
    val formattedDate = remember(transaction.date) {
        SimpleDateFormat(
            "dd MMM yyyy • hh:mm a",
            Locale.getDefault()
        ).format(Date(transaction.date))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = HistoryCard),
        border = BorderStroke(1.dp, HistoryBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            if (isIncome) {
                                HistoryGreen.copy(alpha = 0.12f)
                            } else {
                                HistoryLightBlue
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isIncome) {
                            Icons.Default.ArrowUpward
                        } else {
                            Icons.Default.ArrowDownward
                        },
                        contentDescription = if (isIncome) "Income" else "Expense",
                        tint = if (isIncome) HistoryGreen else HistoryDeepBlue
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = HistoryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = transaction.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = HistoryMuted
                    )
                }

                Spacer(Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isIncome) {
                            "+₹${transaction.amount}"
                        } else {
                            "-₹${transaction.amount}"
                        },
                        color = amountColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = if (isIncome) "Income" else "Expense",
                        color = amountColor,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = HistoryMuted,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    text = formattedDate,
                    color = HistoryMuted,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.weight(1f)
                )
            }

            if (transaction.note.isNotBlank()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = HistoryBackground
                ) {
                    Text(
                        text = transaction.note,
                        modifier = Modifier.padding(10.dp),
                        color = HistoryMuted,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = HistoryDeepBlue
                    )

                    Spacer(Modifier.width(5.dp))

                    Text("Edit", color = HistoryDeepBlue)
                }

                TextButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = HistoryRed
                    )

                    Spacer(Modifier.width(5.dp))

                    Text("Delete", color = HistoryRed)
                }
            }
        }
    }
}

@Composable
private fun EmptyTransactionHistory(
    hasTransactions: Boolean,
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = HistoryCard),
        border = BorderStroke(1.dp, HistoryBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(HistoryLightBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (hasTransactions) {
                        Icons.Default.Search
                    } else {
                        Icons.Default.ReceiptLong
                    },
                    contentDescription = null,
                    tint = HistoryDeepBlue,
                    modifier = Modifier.size(30.dp)
                )
            }

            Text(
                text = if (hasTransactions) {
                    "No matching transactions"
                } else {
                    "Nothing here yet"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = HistoryText
            )

            Text(
                text = if (hasTransactions) {
                    "Try another search or clear your filters to see more transactions."
                } else {
                    "Your income and expenses will appear here once you add a transaction."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = HistoryMuted
            )

            if (hasTransactions) {
                TextButton(onClick = onReset) {
                    Text("Clear filters", color = HistoryDeepBlue)
                }
            }
        }
    }
}
