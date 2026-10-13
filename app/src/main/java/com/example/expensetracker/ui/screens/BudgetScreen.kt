package com.example.expensetracker.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.local.BudgetEntity
import com.example.expensetracker.ui.theme.ExpenseRed
import com.example.expensetracker.ui.theme.IncomeGreen
import com.example.expensetracker.ui.theme.PrimaryBlue
import com.example.expensetracker.ui.theme.Teal
import com.example.expensetracker.viewmodel.BudgetViewModel
import com.example.expensetracker.viewmodel.ExpenseViewModel
import java.util.Calendar
import kotlin.math.abs

private val BudgetScreenBackground = Color(0xFF0B1120)
private val BudgetCardBackground = Color(0xFF151F32)
private val BudgetBorder = Color(0xFF29364B)
private val BudgetMainText = Color(0xFFF8FAFC)
private val BudgetSecondaryText = Color(0xFF94A3B8)
private val BudgetProgressTrack = Color(0xFF29364B)
private val BudgetBlueLight = Color(0xFF93C5FD)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    budgetViewModel: BudgetViewModel,
    expenseViewModel: ExpenseViewModel,
    onBack: () -> Unit,
    onAddBudget: () -> Unit,
    onEditBudget: (Long) -> Unit
) {
    val expenses by expenseViewModel.expenses.collectAsState()

    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH) + 1
    val currentYear = calendar.get(Calendar.YEAR)

    val monthlyBudgetsState by budgetViewModel
        .getBudgetsForMonth(currentMonth, currentYear)
        .collectAsState()

    val monthlyBudgets = monthlyBudgetsState.orEmpty()

    var budgetToDelete by remember {
        mutableStateOf<BudgetEntity?>(null)
    }

    val monthlyExpenses = expenses.filter { expense ->
        val expenseCalendar = Calendar.getInstance().apply {
            timeInMillis = expense.date
        }

        expense.type == "EXPENSE" &&
                expenseCalendar.get(Calendar.MONTH) + 1 == currentMonth &&
                expenseCalendar.get(Calendar.YEAR) == currentYear
    }

    val totalBudget = monthlyBudgets.sumOf { it.amount }

    val totalSpent = monthlyBudgets.sumOf { budget ->
        monthlyExpenses
            .filter { it.category == budget.category }
            .sumOf { it.amount }
    }

    val totalRemaining = totalBudget - totalSpent

    Scaffold(
        containerColor = BudgetScreenBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Budgets",
                            fontWeight = FontWeight.Bold,
                            color = BudgetMainText
                        )

                        Text(
                            text = "Monthly spending limits",
                            fontSize = 12.sp,
                            color = BudgetSecondaryText
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = BudgetMainText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BudgetScreenBackground,
                    titleContentColor = BudgetMainText,
                    navigationIconContentColor = BudgetMainText
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBudget,
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Budget"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BudgetScreenBackground)
                .padding(paddingValues),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                BudgetOverviewCard(
                    totalBudget = totalBudget,
                    totalSpent = totalSpent,
                    remaining = totalRemaining
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
                item {
                    Text(
                        text = "Category Budgets",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = BudgetMainText
                    )
                }

                items(
                    items = monthlyBudgets,
                    key = { it.id }
                ) { budget ->
                    val spent = monthlyExpenses
                        .filter { it.category == budget.category }
                        .sumOf { it.amount }

                    BudgetCard(
                        budget = budget,
                        spentAmount = spent,
                        onEdit = { onEditBudget(budget.id) },
                        onDelete = { budgetToDelete = budget }
                    )
                }
            }
        }
    }

    budgetToDelete?.let { budget ->
        AlertDialog(
            onDismissRequest = { budgetToDelete = null },
            containerColor = BudgetCardBackground,
            titleContentColor = BudgetMainText,
            textContentColor = BudgetSecondaryText,
            title = {
                Text(
                    text = "Delete Budget?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete the ${budget.category} budget?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        budgetViewModel.deleteBudget(budget)
                        budgetToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ExpenseRed,
                        contentColor = Color.White
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { budgetToDelete = null },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BudgetBorder,
                        contentColor = BudgetMainText
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun BudgetOverviewCard(
    totalBudget: Long,
    totalSpent: Long,
    remaining: Long
) {
    val overallProgress = if (totalBudget > 0L) {
        (totalSpent.toFloat() / totalBudget.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val isOverBudget = remaining < 0L
    val statusColor = if (isOverBudget) {
        Color(0xFFFFB4AB)
    } else {
        BudgetBlueLight
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF17365D)
        ),
        border = BorderStroke(1.dp, Color(0xFF285184))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "THIS MONTH",
                color = Color(0xFFBFDBFE),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "₹$totalSpent",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "spent so far",
                color = Color(0xFFBFDBFE),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(20.dp))

            LinearProgressIndicator(
                progress = { overallProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp)
                    .clip(CircleShape),
                color = if (isOverBudget) {
                    Color(0xFFFF8A80)
                } else {
                    Color(0xFF93C5FD)
                },
                trackColor = Color.White.copy(alpha = 0.18f)
            )

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OverviewItem(
                    title = "Total budget",
                    value = "₹$totalBudget"
                )

                OverviewItem(
                    title = if (isOverBudget) "Over budget" else "Remaining",
                    value = "₹${abs(remaining)}",
                    alignment = Alignment.End,
                    valueColor = statusColor
                )
            }
        }
    }
}

@Composable
private fun OverviewItem(
    title: String,
    value: String,
    alignment: Alignment.Horizontal = Alignment.Start,
    valueColor: Color = Color.White
) {
    Column(horizontalAlignment = alignment) {
        Text(
            text = title,
            color = Color(0xFFBFDBFE),
            fontSize = 13.sp
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = value,
            color = valueColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BudgetCard(
    budget: BudgetEntity,
    spentAmount: Long,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val remaining = budget.amount - spentAmount
    val progress = if (budget.amount > 0L) {
        spentAmount.toFloat() / budget.amount.toFloat()
    } else {
        0f
    }

    val isOverBudget = spentAmount > budget.amount
    val isNearLimit = !isOverBudget && progress >= 0.8f

    val accentColor = when {
        isOverBudget -> ExpenseRed
        isNearLimit -> Color(0xFFFBBF24)
        else -> IncomeGreen
    }

    val statusText = when {
        isOverBudget -> "Budget exceeded"
        isNearLimit -> "Almost at limit"
        else -> "Within budget"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = BudgetCardBackground
        ),
        border = BorderStroke(1.dp, BudgetBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(accentColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = budget.category.take(1).uppercase(),
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Spacer(Modifier.size(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = budget.category,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = BudgetMainText
                    )

                    Spacer(Modifier.height(3.dp))

                    Text(
                        text = statusText,
                        color = accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Budget",
                        tint = BudgetBlueLight
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Budget",
                        tint = Color(0xFFF87171)
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Spent",
                        fontSize = 12.sp,
                        color = BudgetSecondaryText
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "₹$spentAmount",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BudgetMainText
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isOverBudget) "Over by" else "Remaining",
                        fontSize = 12.sp,
                        color = BudgetSecondaryText
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "₹${abs(remaining)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = accentColor,
                trackColor = BudgetProgressTrack
            )

            Spacer(Modifier.height(9.dp))

            Text(
                text = "Budget: ₹${budget.amount}",
                fontSize = 12.sp,
                color = BudgetSecondaryText
            )
        }
    }
}

@Composable
private fun EmptyBudgetCard(
    onAddBudget: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = BudgetCardBackground
        ),
        border = BorderStroke(1.dp, BudgetBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E3A5F)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = BudgetBlueLight,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "No budgets yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BudgetMainText
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Set spending limits for your categories and keep your expenses under control.",
                fontSize = 14.sp,
                color = BudgetSecondaryText
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onAddBudget,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )

                Spacer(Modifier.size(8.dp))

                Text("Create Budget")
            }
        }
    }
}

@Composable
private fun LoadingBudgetCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BudgetCardBackground
        ),
        border = BorderStroke(1.dp, BudgetBorder)
    ) {
        Text(
            text = "Loading your budgets…",
            modifier = Modifier.padding(24.dp),
            color = BudgetSecondaryText
        )
    }
}
