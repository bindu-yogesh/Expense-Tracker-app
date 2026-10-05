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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.data.expenseCategories
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.viewmodel.ExpenseViewModel

private val IncomeGreen = Color(0xFF16A34A)
private val IncomeGreenLight = Color(0xFFDCFCE7)

private val ExpenseRed = Color(0xFFDC2626)
private val ExpenseRedLight = Color(0xFFFEE2E2)

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
                    transaction.title.contains(
                        query,
                        ignoreCase = true
                    ) ||
                    transaction.category.contains(
                        query,
                        ignoreCase = true
                    )

        val matchesCategory =
            selectedCategory == "All Categories" ||
                    transaction.category == selectedCategory

        val matchesType =
            selectedType == "ALL" ||
                    transaction.type == selectedType

        matchesSearch &&
                matchesCategory &&
                matchesType
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            Column {
                Text(
                    text = "Transactions",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${transactions.size} transactions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            placeholder = {
                Text("Search transactions")
            },
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Box(
                modifier = Modifier.weight(1f)
            ) {

                Button(
                    onClick = {
                        categoryExpanded = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = when {
                            selectedCategory == "All Categories" ->
                                "Category"

                            else ->
                                selectedCategory
                        }
                    )
                }

                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = {
                        categoryExpanded = false
                    }
                ) {

                    DropdownMenuItem(
                        text = {
                            Text("All Categories")
                        },
                        onClick = {
                            selectedCategory = "All Categories"
                            categoryExpanded = false
                        }
                    )

                    expenseCategories.forEach { category ->

                        DropdownMenuItem(
                            text = {
                                Text(category)
                            },
                            onClick = {
                                selectedCategory = category
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.weight(1f)
            ) {

                Button(
                    onClick = {
                        typeExpanded = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = when (selectedType) {
                            "INCOME" -> "Income"
                            "EXPENSE" -> "Expense"
                            else -> "All Types"
                        }
                    )
                }

                DropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = {
                        typeExpanded = false
                    }
                ) {

                    DropdownMenuItem(
                        text = {
                            Text("All Types")
                        },
                        onClick = {
                            selectedType = "ALL"
                            typeExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Income")
                        },
                        onClick = {
                            selectedType = "INCOME"
                            typeExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Expense")
                        },
                        onClick = {
                            selectedType = "EXPENSE"
                            typeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // Transaction count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${filteredTransactions.size} found",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (filteredTransactions.isEmpty()) {

            EmptyTransactionState(
                hasTransactions = transactions.isNotEmpty()
            )

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = filteredTransactions,
                    key = { it.id }
                ) { transaction ->

                    TransactionHistoryItem(
                        transaction = transaction,
                        onEdit = {
                            onEdit(transaction.id)
                        },
                        onDelete = {
                            transactionToDelete = transaction
                        }
                    )
                }

                item {
                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )
                }
            }
        }
    }

    // Delete confirmation dialog
    if (transactionToDelete != null) {

        AlertDialog(
            onDismissRequest = {
                transactionToDelete = null
            },
            title = {
                Text(
                    text = "Delete Transaction?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete " +
                            "\"${transactionToDelete?.title}\"?"
                )
            },
            confirmButton = {

                TextButton(
                    onClick = {

                        transactionToDelete?.let { transaction ->
                            viewModel.deleteExpense(transaction)
                        }

                        transactionToDelete = null
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = ExpenseRed
                    )
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {
                        transactionToDelete = null
                    }
                ) {
                    Text("Cancel")
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

    val accentColor =
        if (isIncome) IncomeGreen else ExpenseRed

    val iconBackground =
        if (isIncome) IncomeGreenLight else ExpenseRedLight

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Transaction icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(iconBackground),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = if (isIncome) "↑" else "↓",
                        color = accentColor,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                // Transaction information
                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = transaction.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = transaction.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Amount
                Text(
                    text = if (isIncome) {
                        "+₹${transaction.amount}"
                    } else {
                        "-₹${transaction.amount}"
                    },
                    color = accentColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                IconButton(
                    onClick = onEdit
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit transaction",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = onDelete
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete transaction",
                        tint = ExpenseRed
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "₹",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = if (hasTransactions) {
                    "No matching transactions"
                } else {
                    "No transactions yet"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = if (hasTransactions) {
                    "Try changing your search or filters."
                } else {
                    "Your transactions will appear here."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}