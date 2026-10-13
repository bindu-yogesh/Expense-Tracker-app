
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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

// Premium light baby-blue theme
private val DashboardBackground = Color(0xFFF3F8FC)
private val DashboardCard = Color(0xFFFFFFFF)
private val DashboardBorder = Color(0xFFE0EAF3)
private val DashboardText = Color(0xFF243B53)
private val DashboardMuted = Color(0xFF71849A)
private val DashboardBlue = Color(0xFF5AA9E6)
private val DashboardLightBlue = Color(0xFFE4F2FF)
private val DashboardDeepBlue = Color(0xFF356F9F)
private val DashboardGreen = Color(0xFF16866A)
private val DashboardRed = Color(0xFFD95768)

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
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "YOUR FINANCES",
                    color = DashboardDeepBlue,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Expense Tracker",
                    color = DashboardText,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.6).sp
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "A clearer view of your money.",
                    color = DashboardMuted,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(49.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFFB9DDFB),
                                Color(0xFF82BDEB)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = "Expense Tracker",
                    tint = Color(0xFF214F75),
                    modifier = Modifier.size(25.dp)
                )
            }
        }

        Spacer(Modifier.height(25.dp))

        // Main balance card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color(0xFFBBDCF5),
                    shape = RoundedCornerShape(25.dp)
                ),
            shape = RoundedCornerShape(25.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFE0F1FF),
                                Color(0xFFC7E5FC),
                                Color(0xFFB7DDF8)
                            )
                        )
                    )
                    .padding(22.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL BALANCE",
                            color = Color(0xFF416987),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.4.sp
                        )

                        Spacer(Modifier.weight(1f))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.75f))
                                .padding(9.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = DashboardDeepBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = formatRupees(balance),
                        color = Color(0xFF203E59),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.8).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(
                                    Color(0xFF16866A),
                                    CircleShape
                                )
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = "Your current available balance",
                            color = Color(0xFF416987),
                            fontSize = 12.sp
                        )
                    }

                    Spacer(Modifier.height(21.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(
                                Color(0xFF6E9DBD).copy(alpha = 0.25f)
                            )
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = DashboardGreen,
                                    modifier = Modifier.size(15.dp)
                                )

                                Spacer(Modifier.width(5.dp))

                                Text(
                                    text = "Income",
                                    color = Color(0xFF416987),
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(Modifier.height(7.dp))

                            Text(
                                text = formatRupees(income),
                                color = Color(0xFF203E59),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(38.dp)
                                .background(
                                    Color(0xFF6E9DBD).copy(alpha = 0.3f)
                                )
                        )

                        Spacer(Modifier.width(17.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = DashboardRed,
                                    modifier = Modifier.size(15.dp)
                                )

                                Spacer(Modifier.width(5.dp))

                                Text(
                                    text = "Expenses",
                                    color = Color(0xFF416987),
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(Modifier.height(7.dp))

                            Text(
                                text = formatRupees(expenses),
                                color = Color(0xFF203E59),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(27.dp))

        // Overview
        Text(
            text = "Financial overview",
            color = DashboardText,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.3).sp
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Your money at a glance",
            color = DashboardMuted,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(15.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                title = "Total Income",
                amount = income,
                icon = Icons.Default.ArrowUpward,
                iconColor = DashboardGreen,
                modifier = Modifier.weight(1f)
            )

            SummaryCard(
                title = "Total Expenses",
                amount = expenses,
                icon = Icons.Default.ArrowDownward,
                iconColor = DashboardRed,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(28.dp))

        // Recent transactions
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Recent transactions",
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
                    color = DashboardDeepBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(9.dp))

        if (transactions.isEmpty()) {
            EmptyTransactions(
                onAddTransaction = onAddTransaction
            )
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = DashboardBorder,
                        shape = RoundedCornerShape(21.dp)
                    ),
                shape = RoundedCornerShape(21.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DashboardCard
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp)
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

        Spacer(Modifier.height(27.dp))

        // Primary action
        Button(
            onClick = onAddTransaction,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5AA9E6),
                contentColor = Color(0xFF153B59)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 2.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(21.dp)
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
                    .height(50.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DashboardDeepBlue
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFFCADDEB)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.width(7.dp))

                Text(
                    text = "Budgets",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            OutlinedButton(
                onClick = onViewAnalytics,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DashboardDeepBlue
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFFCADDEB)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.width(7.dp))

                Text(
                    text = "Analytics",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(28.dp))
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
            width = 1.dp,
            color = DashboardBorder,
            shape = RoundedCornerShape(20.dp)
        ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = DashboardCard
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (iconColor == DashboardGreen)
                            Color(0xFFE2F6EF)
                        else
                            Color(0xFFFFE9EC)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(Modifier.height(13.dp))

            Text(
                text = title,
                color = DashboardMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(7.dp))

            Text(
                text = formatRupees(amount),
                color = DashboardText,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: ExpenseEntity
) {
    val isIncome = transaction.type == "INCOME"
    val amountColor = if (isIncome) IncomeGreen else ExpenseRed
    val categoryColor = categoryAccent(transaction.category)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(categoryColor.copy(alpha = 0.12f)),
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

            Spacer(Modifier.height(5.dp))

            Text(
                text = transaction.category,
                color = DashboardMuted,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(8.dp))

        Text(
            text = "${if (isIncome) "+" else "−"}${formatRupees(transaction.amount)}",
            color = amountColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
                width = 1.dp,
                color = DashboardBorder,
                shape = RoundedCornerShape(22.dp)
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = DashboardCard
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(RoundedCornerShape(19.dp))
                    .background(DashboardLightBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = DashboardDeepBlue,
                    modifier = Modifier.size(29.dp)
                )
            }

            Spacer(Modifier.height(15.dp))

            Text(
                text = "No transactions yet",
                color = DashboardText,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(7.dp))

            Text(
                text = "Start tracking your money by adding your first transaction.",
                color = DashboardMuted,
                fontSize = 12.sp,
                lineHeight = 19.sp
            )

            Spacer(Modifier.height(17.dp))

            Button(
                onClick = onAddTransaction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DashboardBlue,
                    contentColor = Color(0xFF153B59)
                ),
                shape = RoundedCornerShape(13.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    text = "Add Transaction",
                    fontWeight = FontWeight.SemiBold
                )
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
        "food", "food & dining", "food and dining" ->
            Color(0xFFE99A52)

        "transport", "travel" ->
            Color(0xFF4B93CC)

        "shopping" ->
            Color(0xFF9672D5)

        "bills", "utilities" ->
            Color(0xFF219E91)

        "health", "medical" ->
            Color(0xFF299C79)

        "education" ->
            Color(0xFF688ED1)

        "salary", "income" ->
            IncomeGreen

        "entertainment" ->
            Color(0xFFD76A9B)

        else ->
            Color(0xFF7E91A7)
    }
}

private fun categoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "food", "food & dining", "food and dining" ->
            Icons.Default.Restaurant

        "transport", "travel" ->
            Icons.Default.DirectionsBus

        "shopping" ->
            Icons.Default.ShoppingBag

        "bills", "utilities" ->
            Icons.Default.Payments

        "health", "medical" ->
            Icons.Default.Favorite

        "education" ->
            Icons.Default.School

        "salary", "income" ->
            Icons.Default.ArrowUpward

        "entertainment" ->
            Icons.Default.Category

        else ->
            Icons.Default.MoreHoriz
    }
}
