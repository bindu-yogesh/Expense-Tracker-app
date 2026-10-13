
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.ui.theme.ExpenseRed
import com.example.expensetracker.ui.theme.IncomeGreen
import com.example.expensetracker.viewmodel.ExpenseViewModel

private val DashboardBackground = Color(0xFF080D1B)
private val DashboardCard = Color(0xFF101A30)
private val DashboardBorder = Color(0xFF203254)
private val DashboardText = Color(0xFFF8FAFC)
private val DashboardMuted = Color(0xFF9BAAC7)
private val DashboardPurple = Color(0xFF8B5CF6)
private val DashboardBlue = Color(0xFF3B82F6)

@Composable
fun DashboardScreen(
    viewModel: ExpenseViewModel,
    onAddTransaction: () -> Unit,
    onViewAllTransactions: () -> Unit,
    onViewAnalytics: () -> Unit,
    onViewBudgets: () -> Unit
) {
    val balance by viewModel.balance.collectAsStateWithLifecycle()
    val income by viewModel.totalIncome.collectAsStateWithLifecycle()
    val expenses by viewModel.totalExpenses.collectAsStateWithLifecycle()
    val transactions by viewModel.expenses.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
    ) {
        Spacer(Modifier.height(18.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "YOUR FINANCES",
                    color = DashboardMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    text = "Expense Tracker",
                    color = DashboardText,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Track  ·  Manage  ·  Save",
                    color = DashboardMuted,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                DashboardPurple,
                                Color(0xFF5534C7)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "₹",
                    color = Color.White,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(22.dp))

        // Balance card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    Color(0xFF7557F5).copy(alpha = 0.65f),
                    RoundedCornerShape(23.dp)
                ),
            shape = RoundedCornerShape(23.dp),
            colors = CardDefaults.cardColors(
                containerColor = DashboardPurple
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF713DF0),
                                Color(0xFF4330A9),
                                Color(0xFF29245F)
                            )
                        )
                    )
                    .padding(21.dp)
            ) {
                Column {
                    Text(
                        text = "Total Balance",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(7.dp))

                    Text(
                        text = formatRupees(balance),
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFA7F3D0))
                        )

                        Spacer(Modifier.width(7.dp))

                        Text(
                            text = "Your available balance",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "OVERVIEW",
            color = DashboardMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )

        Spacer(Modifier.height(11.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                title = "Total Income",
                amount = income,
                icon = Icons.Default.ArrowUpward,
                iconColor = IncomeGreen,
                modifier = Modifier.weight(1f)
            )

            SummaryCard(
                title = "Total Expenses",
                amount = expenses,
                icon = Icons.Default.ArrowDownward,
                iconColor = ExpenseRed,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(26.dp))

        // Recent transactions heading
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Recent Transactions",
                    color = DashboardText,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Your latest money activity",
                    color = DashboardMuted,
                    fontSize = 12.sp
                )
            }

            TextButton(onClick = onViewAllTransactions) {
                Text(
                    text = "See all",
                    color = Color(0xFFB69BFF),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        if (transactions.isEmpty()) {
            EmptyTransactions(
                onAddTransaction = onAddTransaction
            )
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        DashboardBorder,
                        RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DashboardCard
                )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    transactions.take(5).forEachIndexed { index, transaction ->
                        TransactionItem(transaction)

                        if (index < minOf(transactions.size, 5) - 1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(DashboardBorder)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        // Primary action
        Button(
            onClick = onAddTransaction,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(17.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DashboardPurple,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "Add Transaction",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onViewBudgets,
                modifier = Modifier
                    .weight(1f)
                    .height(49.dp),
                shape = RoundedCornerShape(15.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFFB69BFF)
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    text = "Budgets",
                    color = DashboardText
                )
            }

            OutlinedButton(
                onClick = onViewAnalytics,
                modifier = Modifier
                    .weight(1f)
                    .height(49.dp),
                shape = RoundedCornerShape(15.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFFB69BFF)
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    text = "Analytics",
                    color = DashboardText
                )
            }
        }

        Spacer(Modifier.height(26.dp))
    }
}

@Composable
private fun SummaryCard(
    title: String,
    amount: Long,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.border(
            1.dp,
            iconColor.copy(alpha = 0.22f),
            RoundedCornerShape(19.dp)
        ),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(
            containerColor = iconColor.copy(alpha = 0.09f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = DashboardMuted,
                    fontSize = 12.sp
                )

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = formatRupees(amount),
                color = iconColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TransactionItem(transaction: ExpenseEntity) {
    val isIncome = transaction.type == "INCOME"
    val amountColor = if (isIncome) IncomeGreen else ExpenseRed
    val categoryColor = categoryAccent(transaction.category)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(43.dp)
                .clip(CircleShape)
                .background(categoryColor.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = categoryIcon(transaction.category),
                contentDescription = transaction.category,
                tint = categoryColor,
                modifier = Modifier.size(21.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.title,
                color = DashboardText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = transaction.category,
                color = DashboardMuted,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(6.dp))

        Text(
            text = "${if (isIncome) "+" else "−"}${formatRupees(transaction.amount)}",
            color = amountColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
private fun EmptyTransactions(
    onAddTransaction: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                DashboardBorder,
                RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = DashboardCard
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(19.dp))
                    .background(Color(0xFF28204B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = Color(0xFFB69BFF),
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = "No transactions yet",
                color = DashboardText,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Start tracking your money by adding your first transaction.",
                color = DashboardMuted,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onAddTransaction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DashboardPurple
                ),
                shape = RoundedCornerShape(13.dp)
            ) {
                Text("Add Transaction")
            }
        }
    }
}

private fun formatRupees(amount: Long): String {
    return if (amount < 0L) {
        "-₹${amount.toString().removePrefix("-")}"
    } else {
        "₹$amount"
    }
}

private fun categoryAccent(category: String): Color {
    return when (category.lowercase()) {
        "food", "food & dining", "food and dining" -> Color(0xFFFF914D)
        "transport", "travel" -> Color(0xFF4D8DFF)
        "shopping" -> Color(0xFFC084FC)
        "bills", "utilities" -> Color(0xFF2DD4BF)
        "health", "medical" -> Color(0xFF34D399)
        "education" -> Color(0xFF8B5CF6)
        "entertainment" -> Color(0xFFF472B6)
        "salary", "income" -> IncomeGreen
        else -> Color(0xFF93A4C7)
    }
}

private fun categoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "food", "food & dining", "food and dining" -> Icons.Default.Restaurant
        "transport", "travel" -> Icons.Default.DirectionsBus
        "shopping" -> Icons.Default.ShoppingBag
        "bills", "utilities" -> Icons.Default.Payments
        "health", "medical" -> Icons.Default.Favorite
        "education" -> Icons.Default.School
        "salary", "income" -> Icons.Default.ArrowUpward
        "entertainment" -> Icons.Default.Category
        else -> Icons.Default.MoreHoriz
    }
}
