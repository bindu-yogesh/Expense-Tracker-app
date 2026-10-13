
package com.example.expensetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.data.expenseCategories
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.viewmodel.ExpenseViewModel

private val HistoryBackground = Color(0xFF0B1120)
private val HistoryCard = Color(0xFF151F32)
private val HistoryBorder = Color(0xFF29364B)
private val HistoryText = Color(0xFFF8FAFC)
private val HistoryMuted = Color(0xFF94A3B8)
private val HistoryBlue = Color(0xFF60A5FA)
private val HistoryIncome = Color(0xFF4ADE80)
private val HistoryExpense = Color(0xFFF87171)

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

    var searchQuery by remember {
        mutableStateOf("")
    }

    var selectedCategory by remember {
        mutableStateOf("All Categories")
    }

    var categoryExpanded by remember {
        mutableStateOf(false)
    }

    var selectedType by remember {
        mutableStateOf("ALL")
    }

    var typeExpanded by remember {
        mutableStateOf(false)
    }

    val filteredTransactions = transactions.filter { transaction ->
        val query = searchQuery.trim()

        val matchesSearch =
            query.isEmpty() ||
                    transaction.title.contains(query, ignoreCase = true) ||
                    transaction.category.contains(query, ignoreCase = true)

        val matchesCategory =
            selectedCategory == "All Categories" ||
                    transaction.category == selectedCategory

        val matchesType =
            selectedType == "ALL" ||
                    transaction.type == selectedType

        matchesSearch && matchesCategory && matchesType
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HistoryBackground)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(HistoryCard)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = HistoryText
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Transactions",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = HistoryText
                )

                Spacer(Modifier.height(3.dp))

                Text(
                    text = "${transactions.size} transactions recorded",
                    style = MaterialTheme.typography.bodyMedium,
                    color = HistoryMuted
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(17.dp),
            placeholder = {
                Text(
                    text = "Search transactions",
                    color = HistoryMuted
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = HistoryMuted
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    TextButton(onClick = { searchQuery = "" }) {
                        Text("Clear", color = HistoryBlue)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = HistoryText,
                unfocusedTextColor = HistoryText,
                cursorColor = HistoryBlue,
                focusedBorderColor = HistoryBlue,
                unfocusedBorderColor = HistoryBorder,
                focusedContainerColor = HistoryCard,
                unfocusedContainerColor = HistoryCard,
                focusedLeadingIconColor = HistoryBlue,
                unfocusedLeadingIconColor = HistoryMuted
            )
        )

        Spacer(Modifier.height(14.dp))

        // Category and type filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(modifier = Modifier.weight(1.15f)) {
                Button(
                    onClick = { categoryExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HistoryCard,
                        contentColor = HistoryText
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = if (selectedCategory == "All Categories") {
                            "Category"
                        } else {
                            selectedCategory
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Categories") },
                        onClick = {
                            selectedCategory = "All Categories"
                            categoryExpanded = false
                        }
                    )

                    expenseCategories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                Button(
                    onClick = { typeExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HistoryCard,
                        contentColor = HistoryText
                    )
                ) {
                    Text(
                        text = when (selectedType) {
                            "INCOME" -> "Income"
                            "EXPENSE" -> "Expense"
                            else -> "All types"
                        },
                        maxLines = 1
                    )
                }

                DropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All types") },
                        onClick = {
                            selectedType = "ALL"
                            typeExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Income") },
                        onClick = {
                            selectedType = "INCOME"
                            typeExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Expense") },
                        onClick = {
                            selectedType = "EXPENSE"
                            typeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Results heading
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your activity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = HistoryText
            )

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(HistoryCard)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${filteredTransactions.size} found",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = HistoryBlue
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        if (filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                EmptyTransactionState(
                    hasTransactions = transactions.isNotEmpty()
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = filteredTransactions,
                    key = { it.id }
                ) { transaction ->
                    TransactionHistoryItem(
                        transaction = transaction,
                        onEdit = { onEdit(transaction.id) },
                        onDelete = {
                            transactionToDelete = transaction
                        }
                    )
                }

                item {
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }

    // Delete confirmation
    transactionToDelete?.let { transaction ->
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            containerColor = HistoryCard,
            titleContentColor = HistoryText,
            textContentColor = HistoryMuted,
            title = {
                Text(
                    text = "Delete transaction?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${transaction.title}\"?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteExpense(transaction)
                        transactionToDelete = null
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = HistoryExpense,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("Cancel", color = HistoryMuted)
                }
            }
        )
    }
}

@Composable
private fun TransactionHistoryItem(
    transaction: ExpenseEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isIncome = transaction.type == "INCOME"
    val accentColor = if (isIncome) HistoryIncome else HistoryExpense
    val iconBackground = accentColor.copy(alpha = 0.12f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = HistoryBorder,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = HistoryCard
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(iconBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isIncome) {
                            Icons.Default.ArrowUpward
                        } else {
                            Icons.Default.ArrowDownward
                        },
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(23.dp)
                    )
                }

                Spacer(Modifier.width(13.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = transaction.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = HistoryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = transaction.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = HistoryMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    text = if (isIncome) {
                        "+₹${transaction.amount}"
                    } else {
                        "-₹${transaction.amount}"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    maxLines = 1
                )
            }

            Spacer(Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(HistoryBorder)
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = HistoryBlue,
                        modifier = Modifier.size(17.dp)
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = "Edit",
                        color = HistoryBlue
                    )
                }

                TextButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = HistoryExpense,
                        modifier = Modifier.size(17.dp)
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = "Delete",
                        color = HistoryExpense
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyTransactionState(
    hasTransactions: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(HistoryCard)
                .border(
                    width = 1.dp,
                    color = HistoryBorder,
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = HistoryBlue,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = if (hasTransactions) {
                "No matching transactions"
            } else {
                "No transactions yet"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = HistoryText
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = if (hasTransactions) {
                "Try changing your search or filters."
            } else {
                "Your transactions will appear here once added."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = HistoryMuted
        )
    }
}
