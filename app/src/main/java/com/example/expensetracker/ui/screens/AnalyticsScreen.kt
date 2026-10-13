
package com.example.expensetracker.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.viewmodel.ExpenseViewModel

private val AnalyticsBackground = Color(0xFF0B1120)
private val AnalyticsCard = Color(0xFF151F32)
private val AnalyticsBorder = Color(0xFF29364B)
private val AnalyticsText = Color(0xFFF8FAFC)
private val AnalyticsMuted = Color(0xFF94A3B8)
private val AnalyticsBlue = Color(0xFF60A5FA)
private val AnalyticsGreen = Color(0xFF4ADE80)
private val AnalyticsRed = Color(0xFFF87171)

private val DonutColors = listOf(
    Color(0xFFF59E0B),
    Color(0xFF3B82F6),
    Color(0xFF8B5CF6),
    Color(0xFF2DD4BF),
    Color(0xFFF87171),
    Color(0xFF60A5FA),
    Color(0xFFA78BFA),
    Color(0xFF34D399)
)

@Composable
fun AnalyticsScreen(
    viewModel: ExpenseViewModel,
    onBack: () -> Unit
) {
    val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
    val totalExpenses by viewModel.totalExpenses.collectAsStateWithLifecycle()
    val balance by viewModel.balance.collectAsStateWithLifecycle()

    val expensesByCategory by viewModel.expensesByCategory
        .collectAsStateWithLifecycle()

    val dailyExpenses by viewModel.dailyExpenses
        .collectAsStateWithLifecycle()

    val sortedCategories = expensesByCategory
        .toList()
        .filter { it.second > 0L }
        .sortedByDescending { it.second }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AnalyticsBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    end = 20.dp,
                    top = 16.dp,
                    bottom = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = AnalyticsText
                )
            }

            Spacer(Modifier.size(8.dp))

            Column {
                Text(
                    text = "Analytics",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = AnalyticsText
                )

                Text(
                    text = "Understand your spending",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AnalyticsMuted
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 4.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Income and expenses
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AnalyticsCard(
                        title = "Income",
                        amount = totalIncome,
                        icon = Icons.Default.TrendingUp,
                        iconColor = AnalyticsGreen,
                        modifier = Modifier.weight(1f)
                    )

                    AnalyticsCard(
                        title = "Expenses",
                        amount = totalExpenses,
                        icon = Icons.Default.TrendingDown,
                        iconColor = AnalyticsRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Current balance
            item {
                BalanceCard(balance = balance)
            }

            // 7-day spending trend
            item {
                SpendingTrendCard(dailyExpenses = dailyExpenses)
            }

            // Circular category chart
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AnalyticsCard
                    ),
                    border = BorderStroke(1.dp, AnalyticsBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF1E3A5F)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BarChart,
                                    contentDescription = null,
                                    tint = AnalyticsBlue
                                )
                            }

                            Spacer(Modifier.size(12.dp))

                            Column {
                                Text(
                                    text = "Spending Overview",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AnalyticsText
                                )

                                Text(
                                    text = "See where your money goes",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AnalyticsMuted
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        if (sortedCategories.isNotEmpty()) {
                            DonutChart(
                                expensesByCategory = sortedCategories
                            )
                        } else {
                            EmptyAnalyticsState()
                        }
                    }
                }
            }

            // Category breakdown
            if (sortedCategories.isNotEmpty()) {
                item {
                    Text(
                        text = "Category breakdown",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AnalyticsText
                    )
                }

                items(
                    items = sortedCategories,
                    key = { it.first }
                ) { (category, amount) ->
                    CategoryExpenseItem(
                        category = category,
                        amount = amount
                    )
                }
            }
        }
    }
}

@Composable
private fun SpendingTrendCard(
    dailyExpenses: List<Pair<String, Long>>
) {
    val maxAmount = dailyExpenses
        .maxOfOrNull { it.second }
        ?.coerceAtLeast(1L) ?: 1L

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = AnalyticsCard
        ),
        border = BorderStroke(1.dp, AnalyticsBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "7-Day Spending Trend",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AnalyticsText
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "Track your daily expenses",
                style = MaterialTheme.typography.bodySmall,
                color = AnalyticsMuted
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(155.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                dailyExpenses.forEach { (day, amount) ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = when {
                                amount <= 0L -> "—"
                                amount >= 1000L ->
                                    "₹${"%.1f".format(amount / 1000.0)}k"
                                else -> "₹$amount"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = AnalyticsMuted,
                            maxLines = 1
                        )

                        Spacer(Modifier.height(6.dp))

                        val barHeight = (
                                amount.toFloat() / maxAmount.toFloat()
                                ).coerceIn(0f, 1f) * 95f

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(95.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.65f)
                                    .height(
                                        barHeight.coerceAtLeast(
                                            if (amount > 0L) 5f else 2f
                                        ).dp
                                    )
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 8.dp,
                                            topEnd = 8.dp
                                        )
                                    )
                                    .background(
                                        if (amount > 0L) {
                                            AnalyticsBlue
                                        } else {
                                            AnalyticsBorder
                                        }
                                    )
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelSmall,
                            color = AnalyticsMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DonutChart(
    expensesByCategory: List<Pair<String, Long>>
) {
    val total = expensesByCategory.sumOf { it.second }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circular chart with total in the centre
        Box(
            modifier = Modifier
                .weight(1.1f)
                .size(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val strokeWidth = 22.dp.toPx()
                val diameter = size.minDimension - strokeWidth

                val topLeft = Offset(
                    (size.width - diameter) / 2f,
                    (size.height - diameter) / 2f
                )

                if (total > 0L) {
                    var startAngle = -90f

                    expensesByCategory.forEachIndexed { index, entry ->
                        val sweepAngle =
                            entry.second.toFloat() / total.toFloat() * 360f

                        drawArc(
                            color = DonutColors[index % DonutColors.size],
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(diameter, diameter),
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Butt
                            )
                        )

                        startAngle += sweepAngle
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "₹$total",
                    color = AnalyticsText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Total Expenses",
                    color = AnalyticsMuted,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        // Category legend with amounts and percentages
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            expensesByCategory.forEachIndexed { index, entry ->
                val (category, amount) = entry

                val percentage = if (total > 0L) {
                    amount.toDouble() / total.toDouble() * 100
                } else {
                    0.0
                }

                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(
                                DonutColors[index % DonutColors.size]
                            )
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = category,
                            color = AnalyticsMuted,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1
                        )

                        Text(
                            text = "₹$amount",
                            color = AnalyticsText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "${"%.0f".format(percentage)}%",
                            color = AnalyticsMuted,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsCard(
    title: String,
    amount: Long,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = AnalyticsCard
        ),
        border = BorderStroke(1.dp, AnalyticsBorder)
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = AnalyticsMuted
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "₹$amount",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AnalyticsText
            )
        }
    }
}

@Composable
private fun BalanceCard(balance: Long) {
    val isPositive = balance >= 0
    val balanceColor = if (isPositive) AnalyticsGreen else AnalyticsRed

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = AnalyticsCard
        ),
        border = BorderStroke(1.dp, AnalyticsBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(balanceColor)
                )

                Spacer(Modifier.size(9.dp))

                Text(
                    text = "CURRENT BALANCE",
                    style = MaterialTheme.typography.labelMedium,
                    color = AnalyticsMuted,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "₹$balance",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = balanceColor
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = if (isPositive) {
                    "Your income is keeping your balance positive."
                } else {
                    "Your recorded expenses exceed your income."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = AnalyticsMuted
            )
        }
    }
}

@Composable
private fun CategoryExpenseItem(
    category: String,
    amount: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = AnalyticsCard
        ),
        border = BorderStroke(1.dp, AnalyticsBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1E3A5F)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.firstOrNull()?.uppercase() ?: "?",
                    color = AnalyticsBlue,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.size(12.dp))

            Text(
                text = category,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = AnalyticsText
            )

            Text(
                text = "₹$amount",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AnalyticsText
            )
        }
    }
}

@Composable
private fun EmptyAnalyticsState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.BarChart,
                contentDescription = null,
                modifier = Modifier.size(42.dp),
                tint = AnalyticsMuted
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "No expense data yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = AnalyticsText
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "Add some expenses to see your insights.",
                style = MaterialTheme.typography.bodyMedium,
                color = AnalyticsMuted
            )
        }
    }
}
