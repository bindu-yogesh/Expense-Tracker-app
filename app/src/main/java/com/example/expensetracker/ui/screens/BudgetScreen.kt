package com.example.expensetracker.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.local.BudgetEntity
import com.example.expensetracker.ui.theme.ExpenseRed
import com.example.expensetracker.ui.theme.ExpenseRedLight
import com.example.expensetracker.ui.theme.IncomeGreen
import com.example.expensetracker.ui.theme.IncomeGreenLight
import com.example.expensetracker.ui.theme.PrimaryBlue
import com.example.expensetracker.ui.theme.PrimaryBlueLight
import com.example.expensetracker.ui.theme.Teal
import com.example.expensetracker.ui.theme.TealLight
import com.example.expensetracker.viewmodel.BudgetViewModel
import com.example.expensetracker.viewmodel.ExpenseViewModel
import java.util.Calendar
import kotlin.math.abs

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

    val monthlyBudgets by budgetViewModel
        .getBudgetsForMonth(currentMonth, currentYear)
        .collectAsState()

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
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Budgets",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Monthly spending limits",
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlueLight
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBudget,
                containerColor = PrimaryBlue,
                contentColor = androidx.compose.ui.graphics.Color.White
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
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                BudgetOverviewCard(
                    totalBudget = totalBudget,
                    totalSpent = totalSpent,
                    remaining = totalRemaining
                )
            }

            if (monthlyBudgets.isEmpty()) {
                item {
                    EmptyBudgetCard(
                        onAddBudget = onAddBudget
                    )
                }
            } else {
                item {
                    Text(
                        text = "Category Budgets",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
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
                        onEdit = {
                            onEditBudget(budget.id)
                        },
                        onDelete = {
                            budgetToDelete = budget
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }

    budgetToDelete?.let { budget ->

        AlertDialog(
            onDismissRequest = {
                budgetToDelete = null
            },
            title = {
                Text(
                    text = "Delete Budget?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete the ${budget.category} budget?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        budgetViewModel.deleteBudget(budget)
                        budgetToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ExpenseRed
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        budgetToDelete = null
                    }
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
    val overallProgress =
        if (totalBudget > 0) {
            (totalSpent.toFloat() / totalBudget.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

    val isOverBudget = remaining < 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryBlue
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = "This Month",
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "₹$totalSpent spent",
                color = androidx.compose.ui.graphics.Color.White,
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
                color = androidx.compose.ui.graphics.Color.White,
                trackColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.25f)
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
                    value = "₹${abs(remaining)}"
                )
            }
        }
    }
}

@Composable
private fun OverviewItem(
    title: String,
    value: String
) {
    Column {
        Text(
            text = title,
            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.75f),
            fontSize = 13.sp
        )

        Text(
            text = value,
            color = androidx.compose.ui.graphics.Color.White,
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

    val progress =
        if (budget.amount > 0) {
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

    val lightAccentColor = when {
        isOverBudget -> ExpenseRedLight
        isNearLimit -> TealLight
        else -> IncomeGreenLight
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = lightAccentColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
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
                        .clip(CircleShape)
                        .background(accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = budget.category.take(1),
                        color = androidx.compose.ui.graphics.Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = budget.category,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (isOverBudget) {
                            "Budget exceeded"
                        } else if (isNearLimit) {
                            "Almost at limit"
                        } else {
                            "Within budget"
                        },
                        color = accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Budget",
                        tint = accentColor
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
                        fontSize = 12.sp
                    )

                    Text(
                        text = "₹$spentAmount",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = if (isOverBudget) "Over by" else "Remaining",
                        fontSize = 12.sp
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
                trackColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Budget: ₹${budget.amount}",
                fontSize = 12.sp
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
            containerColor = PrimaryBlueLight
        )
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
                    tint = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No budgets yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Set spending limits for your categories and keep your expenses under control.",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onAddBudget,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
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