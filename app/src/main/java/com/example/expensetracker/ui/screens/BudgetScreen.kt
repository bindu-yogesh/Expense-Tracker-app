
package com.example.expensetracker.ui.screens
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.expensetracker.ui.theme.PrimaryBlueLight
import com.example.expensetracker.ui.theme.Teal
import com.example.expensetracker.viewmodel.BudgetViewModel
import com.example.expensetracker.viewmodel.ExpenseViewModel
import java.util.Calendar
import kotlin.math.abs

private val ScreenBackground = Color(0xFFF8FAFC)
private val CardBackground = Color.White
private val MainText = Color(0xFF0F172A)
private val SecondaryText = Color(0xFF475569)
private val ProgressTrack = Color(0xFFE2E8F0)

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
        containerColor = ScreenBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Budgets",
                            fontWeight = FontWeight.Bold,
                            color = MainText
                        )
                        Text(
                            text = "Monthly spending limits",
                            fontSize = 12.sp,
                            color = SecondaryText
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MainText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ScreenBackground,
                    titleContentColor = MainText,
                    navigationIconContentColor = MainText
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBudget,
                containerColor = PrimaryBlue,
                contentColor = Color.White
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
                .background(ScreenBackground)
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
                // Loading: don't show the empty-budget message yet.
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
                        color = MainText
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
            containerColor = CardBackground,
            titleContentColor = MainText,
            textContentColor = SecondaryText,
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
                        containerColor = ProgressTrack,
                        contentColor = MainText
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryBlue
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "This Month",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "₹$totalSpent spent",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { overallProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.25f)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OverviewItem(
                    title = "Budget",
                    value = "₹$totalBudget"
                )

                OverviewItem(
                    title = if (isOverBudget) "Over Budget" else "Remaining",
                    value = "₹${abs(remaining)}",
                    alignment = Alignment.End
                )
            }
        }
    }
}

@Composable
private fun OverviewItem(
    title: String,
    value: String,
    alignment: Alignment.Horizontal = Alignment.Start
) {
    Column(horizontalAlignment = alignment) {
        Text(
            text = title,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 13.sp
        )

        Text(
            text = value,
            color = Color.White,
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
        isNearLimit -> Teal
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
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = budget.category.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = budget.category,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MainText
                    )

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
                        tint = PrimaryBlue
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Budget",
                        tint = ExpenseRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Spent",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )

                    Text(
                        text = "₹$spentAmount",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MainText
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isOverBudget) "Over by" else "Remaining",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )

                    Text(
                        text = "₹${abs(remaining)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp)
                    .clip(CircleShape),
                color = accentColor,
                trackColor = ProgressTrack
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Budget: ₹${budget.amount}",
                fontSize = 12.sp,
                color = SecondaryText
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
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No budgets yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MainText
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Set spending limits for your categories and keep your expenses under control.",
                fontSize = 14.sp,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(18.dp))

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

                Spacer(modifier = Modifier.size(6.dp))

                Text("Create Budget")
            }
        }
    }
}
