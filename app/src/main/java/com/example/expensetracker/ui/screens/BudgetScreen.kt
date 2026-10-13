
package com.example.expensetracker.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.local.BudgetEntity
import com.example.expensetracker.viewmodel.BudgetViewModel
import com.example.expensetracker.viewmodel.ExpenseViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

// Baby-blue + white theme
private val BudgetBackground = Color(0xFFF3F8FC)
private val BudgetCardBackground = Color(0xFFFFFFFF)
private val BudgetPurple = Color(0xFF5AA9E6)
private val BudgetGreen = Color(0xFF219A75)
private val BudgetAmber = Color(0xFFE6A23C)
private val BudgetRed = Color(0xFFE16D75)
private val BudgetMuted = Color(0xFF71849A)
private val BudgetWhite = Color(0xFF243B53)
private val BudgetBorder = Color(0xFFE0EAF3)
private val BudgetTrack = Color(0xFFE8EFF6)

private fun formatRupees(amount: Long): String {
    return NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        .format(amount)
}

private data class SpendingComparison(
    val category: String,
    val currentSpent: Long,
    val previousSpent: Long,
    val percentageChange: Int?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    budgetViewModel: BudgetViewModel,
    expenseViewModel: ExpenseViewModel,
    onBack: () -> Unit,
    onAddBudget: () -> Unit,
    onEditBudget: (BudgetEntity) -> Unit
) {
    val expenses by expenseViewModel.expenses.collectAsState()

    val currentMonthCalendar = Calendar.getInstance()
    val currentMonth = currentMonthCalendar.get(Calendar.MONTH) + 1
    val currentYear = currentMonthCalendar.get(Calendar.YEAR)

    val previousMonthCalendar =
        (currentMonthCalendar.clone() as Calendar).apply {
            add(Calendar.MONTH, -1)
        }

    val previousMonth = previousMonthCalendar.get(Calendar.MONTH) + 1
    val previousYear = previousMonthCalendar.get(Calendar.YEAR)

    val monthFormatter = remember {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    }

    val currentMonthName = monthFormatter.format(currentMonthCalendar.time)
    val previousMonthName = monthFormatter.format(previousMonthCalendar.time)

    val monthlyBudgetsState by budgetViewModel
        .getBudgetsForMonth(currentMonth, currentYear)
        .collectAsState()

    val monthlyBudgets = monthlyBudgetsState.orEmpty()

    fun isInMonth(timestamp: Long, month: Int, year: Int): Boolean {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }
        return calendar.get(Calendar.MONTH) + 1 == month &&
                calendar.get(Calendar.YEAR) == year
    }

    val currentMonthExpenses = expenses.filter {
        it.type.equals("EXPENSE", ignoreCase = true) &&
                isInMonth(it.date, currentMonth, currentYear)
    }

    val previousMonthExpenses = expenses.filter {
        it.type.equals("EXPENSE", ignoreCase = true) &&
                isInMonth(it.date, previousMonth, previousYear)
    }

    val totalBudget = monthlyBudgets.sumOf { it.amount }
    val totalSpent = currentMonthExpenses.sumOf { it.amount }
    val totalRemaining = totalBudget - totalSpent

    fun spentForCategory(category: String): Long {
        return currentMonthExpenses
            .filter { it.category == category }
            .sumOf { it.amount }
    }

    val spendingComparisons = remember(
        currentMonthExpenses,
        previousMonthExpenses
    ) {
        val currentByCategory = currentMonthExpenses
            .groupBy { it.category }
            .mapValues { (_, transactions) ->
                transactions.sumOf { it.amount }
            }

        val previousByCategory = previousMonthExpenses
            .groupBy { it.category }
            .mapValues { (_, transactions) ->
                transactions.sumOf { it.amount }
            }

        (currentByCategory.keys + previousByCategory.keys)
            .distinct()
            .mapNotNull { category ->
                val current = currentByCategory[category] ?: 0L
                val previous = previousByCategory[category] ?: 0L

                if (current == 0L && previous == 0L) {
                    null
                } else {
                    val change = if (previous > 0L) {
                        (
                                (
                                        (current.toDouble() - previous.toDouble()) /
                                                previous.toDouble()
                                        ) * 100.0
                                ).roundToInt()
                    } else {
                        null
                    }

                    SpendingComparison(
                        category = category,
                        currentSpent = current,
                        previousSpent = previous,
                        percentageChange = change
                    )
                }
            }
            .sortedByDescending { it.currentSpent }
    }

    val withinBudgetCount = monthlyBudgets.count { budget ->
        val spent = spentForCategory(budget.category)
        budget.amount > 0L && spent < budget.amount * 0.8
    }

    val nearLimitCount = monthlyBudgets.count { budget ->
        val spent = spentForCategory(budget.category)
        budget.amount > 0L &&
                spent >= budget.amount * 0.8 &&
                spent <= budget.amount
    }

    val exceededBudgetCount = monthlyBudgets.count { budget ->
        budget.amount > 0L &&
                spentForCategory(budget.category) > budget.amount
    }

    val smartInsights = monthlyBudgets.mapNotNull { budget ->
        val spent = spentForCategory(budget.category)
        val limit = budget.amount

        if (limit <= 0L) {
            null
        } else {
            val percentage = spent.toDouble() / limit.toDouble() * 100.0

            when {
                spent > limit ->
                    "${budget.category}: You've exceeded your budget by " +
                            "${formatRupees(spent - limit)}. Consider reducing spending."

                percentage >= 80.0 ->
                    "${budget.category}: You've used ${percentage.toInt()}% " +
                            "of your budget. ${formatRupees(limit - spent)} remains."

                else -> null
            }
        }
    }

    var budgetToDelete by remember {
        mutableStateOf<BudgetEntity?>(null)
    }

    Scaffold(
        containerColor = BudgetBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Budget Insights",
                        color = BudgetWhite,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back",
                            tint = BudgetWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BudgetBackground
                )
            )
        },
        floatingActionButton = {
            Button(
                onClick = onAddBudget,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BudgetPurple,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Text(
                    text = "Add Budget",
                    modifier = Modifier.padding(start = 6.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp
                    )
                ) {
                    Text(
                        text = currentMonthName,
                        style = MaterialTheme.typography.titleMedium,
                        color = BudgetMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BudgetOverviewCard(
                        totalBudget = totalBudget,
                        totalSpent = totalSpent,
                        remaining = totalRemaining
                    )
                }
            }

            item {
                BudgetInsightsCard(
                    withinBudgetCount = withinBudgetCount,
                    nearLimitCount = nearLimitCount,
                    exceededBudgetCount = exceededBudgetCount,
                    hasBudgets = monthlyBudgets.isNotEmpty()
                )
            }

            item {
                MonthComparisonCard(
                    currentMonthName = currentMonthName,
                    previousMonthName = previousMonthName,
                    comparisons = spendingComparisons
                )
            }

            item {
                SmartSpendingInsightsCard(
                    insights = smartInsights,
                    hasBudgets = monthlyBudgets.isNotEmpty()
                )
            }

            item {
                Text(
                    text = "Your Category Budgets",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = BudgetWhite,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (monthlyBudgetsState == null) {
                item {
                    LoadingBudgetCard()
                }
            } else if (monthlyBudgets.isEmpty()) {
                item {
                    EmptyBudgetCard(onAddBudget = onAddBudget)
                }
            } else {
                items(
                    items = monthlyBudgets,
                    key = { it.id }
                ) { budget ->
                    BudgetCard(
                        budget = budget,
                        spentAmount = spentForCategory(budget.category),
                        onEdit = { onEditBudget(budget) },
                        onDelete = { budgetToDelete = budget }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    budgetToDelete?.let { budget ->
        AlertDialog(
            onDismissRequest = { budgetToDelete = null },
            containerColor = BudgetCardBackground,
            titleContentColor = BudgetWhite,
            textContentColor = BudgetMuted,
            title = {
                Text("Delete Budget?")
            },
            text = {
                Text(
                    "Are you sure you want to delete the " +
                            "${budget.category} budget? This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        budgetViewModel.deleteBudget(budget)
                        budgetToDelete = null
                    }
                ) {
                    Text("Delete", color = BudgetRed)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { budgetToDelete = null }
                ) {
                    Text("Cancel", color = BudgetMuted)
                }
            }
        )
    }
}

@Composable
private fun MonthComparisonCard(
    currentMonthName: String,
    previousMonthName: String,
    comparisons: List<SpendingComparison>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = BudgetCardBackground
        ),
        border = BorderStroke(1.dp, BudgetBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = BudgetPurple
                )
                Column {
                    Text(
                        text = "Month-over-Month Spending",
                        color = BudgetWhite,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$currentMonthName vs $previousMonthName",
                        color = BudgetMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (comparisons.isEmpty()) {
                Text(
                    text = "Add expense transactions in either month to see category comparisons.",
                    color = BudgetMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                comparisons.forEach { comparison ->
                    SpendingComparisonRow(comparison)
                }
            }
        }
    }
}

@Composable
private fun SpendingComparisonRow(
    comparison: SpendingComparison
) {
    val change = comparison.percentageChange

    val message: String
    val messageColor: Color

    val icon = when {
        change == null && comparison.currentSpent > 0L ->
            Icons.Default.TrendingUp

        change != null && change > 0 ->
            Icons.Default.TrendingUp

        change != null && change < 0 ->
            Icons.Default.TrendingDown

        else -> Icons.Default.Lightbulb
    }

    when {
        comparison.previousSpent == 0L && comparison.currentSpent > 0L -> {
            message = "${comparison.category}: " +
                    "${formatRupees(comparison.currentSpent)} spent this month; " +
                    "no spending last month."
            messageColor = BudgetAmber
        }

        change != null && change > 0 -> {
            message = "${comparison.category}: You spent $change% more " +
                    "(${formatRupees(comparison.currentSpent - comparison.previousSpent)} increase)."
            messageColor = BudgetRed
        }

        change != null && change < 0 -> {
            message = "${comparison.category}: You spent ${abs(change)}% less " +
                    "than last month."
            messageColor = BudgetGreen
        }

        else -> {
            message = "${comparison.category}: Spending is unchanged at " +
                    "${formatRupees(comparison.currentSpent)}."
            messageColor = BudgetMuted
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = messageColor
        )
        Text(
            text = message,
            color = messageColor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun BudgetOverviewCard(
    totalBudget: Long,
    totalSpent: Long,
    remaining: Long
) {
    val progress = if (totalBudget > 0L) {
        (totalSpent.toFloat() / totalBudget.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val isOverBudget = totalSpent > totalBudget

    val progressColor = when {
        isOverBudget -> BudgetRed
        progress >= 0.8f -> BudgetAmber
        else -> BudgetGreen
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = BudgetCardBackground
        ),
        border = BorderStroke(1.dp, BudgetBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "MONTHLY OVERVIEW",
                color = BudgetMuted,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = formatRupees(totalSpent),
                color = BudgetWhite,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Total spent this month",
                color = BudgetMuted,
                style = MaterialTheme.typography.bodyMedium
            )

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = progressColor,
                trackColor = BudgetTrack
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total budget",
                        color = BudgetMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = formatRupees(totalBudget),
                        color = BudgetWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isOverBudget) "Over budget" else "Remaining",
                        color = if (isOverBudget) BudgetRed else BudgetMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = formatRupees(abs(remaining)),
                        color = if (isOverBudget) BudgetRed else BudgetGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (totalBudget == 0L && totalSpent > 0L) {
                Text(
                    text = "Add a budget to track your monthly spending.",
                    color = BudgetAmber,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun BudgetInsightsCard(
    withinBudgetCount: Int,
    nearLimitCount: Int,
    exceededBudgetCount: Int,
    hasBudgets: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = BudgetCardBackground
        ),
        border = BorderStroke(1.dp, BudgetBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Budget Insights",
                color = BudgetWhite,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InsightCount("Within budget", withinBudgetCount, BudgetGreen)
                InsightCount("Near limit", nearLimitCount, BudgetAmber)
                InsightCount("Exceeded", exceededBudgetCount, BudgetRed)
            }

            val message = when {
                !hasBudgets ->
                    "Create a monthly budget to start tracking your spending."

                exceededBudgetCount > 0 ->
                    "$exceededBudgetCount category budget(s) exceeded. Review your spending."

                nearLimitCount > 0 ->
                    "$nearLimitCount category budget(s) have reached at least 80% of their limit."

                else ->
                    "Great work! No category is near or over its budget."
            }

            val messageColor = when {
                !hasBudgets -> BudgetMuted
                exceededBudgetCount > 0 -> BudgetRed
                nearLimitCount > 0 -> BudgetAmber
                else -> BudgetGreen
            }

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (exceededBudgetCount > 0 || nearLimitCount > 0) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Budget warning",
                        tint = messageColor
                    )
                }

                Text(
                    text = message,
                    color = messageColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun InsightCount(
    label: String,
    count: Int,
    color: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = count.toString(),
            color = color,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = BudgetMuted,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun SmartSpendingInsightsCard(
    insights: List<String>,
    hasBudgets: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = BudgetCardBackground
        ),
        border = BorderStroke(1.dp, BudgetBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = BudgetPurple
                )
                Text(
                    text = "Budget Alerts",
                    color = BudgetWhite,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            when {
                !hasBudgets -> {
                    Text(
                        text = "Create a monthly budget to receive alerts when spending approaches your limits.",
                        color = BudgetMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                insights.isEmpty() -> {
                    Text(
                        text = "You're on track! No category has reached 80% of its budget yet.",
                        color = BudgetGreen,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                else -> {
                    insights.forEach { insight ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = BudgetAmber
                            )
                            Text(
                                text = insight,
                                color = BudgetWhite,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BudgetCard(
    budget: BudgetEntity,
    spentAmount: Long,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val progress = if (budget.amount > 0L) {
        spentAmount.toFloat() / budget.amount.toFloat()
    } else {
        0f
    }

    val remaining = budget.amount - spentAmount

    val progressColor = when {
        spentAmount > budget.amount -> BudgetRed
        progress >= 0.8f -> BudgetAmber
        else -> BudgetGreen
    }

    val status = when {
        spentAmount > budget.amount -> "Budget exceeded"
        progress >= 0.8f -> "Almost at limit"
        else -> "Within budget"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BudgetCardBackground
        ),
        border = BorderStroke(
            width = 1.dp,
            color = BudgetBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = budget.category,
                        color = BudgetWhite,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = status,
                        color = progressColor,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit budget",
                        tint = BudgetPurple
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete budget",
                        tint = BudgetRed
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Spent ${formatRupees(spentAmount)}",
                    color = BudgetMuted,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Limit ${formatRupees(budget.amount)}",
                    color = BudgetWhite,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp),
                color = progressColor,
                trackColor = BudgetTrack
            )

            Text(
                text = if (remaining < 0L) {
                    "${formatRupees(abs(remaining))} over budget"
                } else {
                    "${formatRupees(remaining)} remaining"
                },
                color = if (remaining < 0L) BudgetRed else BudgetGreen,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun EmptyBudgetCard(
    onAddBudget: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BudgetCardBackground
        ),
        border = BorderStroke(1.dp, BudgetBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = BudgetPurple
            )

            Text(
                text = "No budgets for this month",
                color = BudgetWhite,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Set a category budget to track spending and get useful insights.",
                color = BudgetMuted,
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = onAddBudget,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BudgetPurple,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Create your first budget")
            }
        }
    }
}

@Composable
private fun LoadingBudgetCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = BudgetPurple)
    }
}
