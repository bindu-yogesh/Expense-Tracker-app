package com.example.expensetracker.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.local.BudgetEntity
import com.example.expensetracker.viewmodel.BudgetViewModel

private val EditBudgetBackground = Color(0xFF0B1120)
private val EditBudgetCard = Color(0xFF151F32)
private val EditBudgetBorder = Color(0xFF29364B)
private val EditBudgetText = Color(0xFFF8FAFC)
private val EditBudgetMuted = Color(0xFF94A3B8)
private val EditBudgetBlue = Color(0xFF60A5FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBudgetScreen(
    budget: BudgetEntity,
    budgetViewModel: BudgetViewModel,
    onBudgetUpdated: () -> Unit,
    onBack: () -> Unit
) {
    var amountText by remember(budget.id) {
        mutableStateOf(budget.amount.toString())
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    Scaffold(
        containerColor = EditBudgetBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Budget",
                        color = EditBudgetText,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = EditBudgetText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EditBudgetBackground,
                    titleContentColor = EditBudgetText,
                    navigationIconContentColor = EditBudgetText
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(EditBudgetBackground)
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        EditBudgetCard,
                        RoundedCornerShape(22.dp)
                    )
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Update your spending limit",
                    color = EditBudgetText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Change the monthly amount for this category.",
                    color = EditBudgetMuted,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "CATEGORY",
                    color = EditBudgetMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = budget.category,
                    color = EditBudgetBlue,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "MONTHLY LIMIT",
                    color = EditBudgetMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        amountText = input.filter { it.isDigit() }
                        errorMessage = null
                    },
                    label = {
                        Text("Budget amount")
                    },
                    placeholder = {
                        Text("Enter amount")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = EditBudgetBlue
                        )
                    },
                    prefix = {
                        Text("₹")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = EditBudgetText,
                        unfocusedTextColor = EditBudgetText,
                        focusedLabelColor = EditBudgetBlue,
                        unfocusedLabelColor = EditBudgetMuted,
                        focusedPlaceholderColor = EditBudgetMuted,
                        unfocusedPlaceholderColor = EditBudgetMuted,
                        focusedBorderColor = EditBudgetBlue,
                        unfocusedBorderColor = EditBudgetBorder,
                        focusedContainerColor = EditBudgetCard,
                        unfocusedContainerColor = EditBudgetCard,
                        focusedLeadingIconColor = EditBudgetBlue,
                        unfocusedLeadingIconColor = EditBudgetMuted,
                        focusedPrefixColor = EditBudgetText,
                        unfocusedPrefixColor = EditBudgetText,
                        cursorColor = EditBudgetBlue
                    )
                )

                Text(
                    text = "Keep your budget realistic and adjust it whenever your needs change.",
                    color = EditBudgetMuted,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(4.dp))

            Button(
                onClick = {
                    if (isSaving) {
                        return@Button
                    }

                    val amount = amountText.toLongOrNull()

                    if (amount == null || amount <= 0L) {
                        errorMessage = "Please enter a valid budget amount."
                        return@Button
                    }

                    isSaving = true

                    try {
                        budgetViewModel.updateBudget(
                            budget.copy(amount = amount)
                        )
                        onBudgetUpdated()
                    } catch (exception: Exception) {
                        isSaving = false
                        errorMessage = "Couldn't update the budget. Please try again."
                    }
                },
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF334155),
                    disabledContentColor = EditBudgetMuted
                )
            ) {
                Text(
                    text = if (isSaving) "Updating..." else "Update Budget",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))
        }
    }

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = {
                errorMessage = null
            },
            title = {
                Text(
                    text = "Couldn't update budget",
                    color = EditBudgetText,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = errorMessage.orEmpty(),
                    color = EditBudgetMuted
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        errorMessage = null
                    }
                ) {
                    Text(
                        text = "OK",
                        color = EditBudgetBlue
                    )
                }
            },
            containerColor = EditBudgetCard
        )
    }
}
