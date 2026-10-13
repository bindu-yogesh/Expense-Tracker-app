
package com.example.expensetracker.ui.screens

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
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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

private val EditBudgetBackground = Color(0xFFF3F8FC)
private val EditBudgetCard = Color(0xFFFFFFFF)
private val EditBudgetBorder = Color(0xFFE0EAF3)
private val EditBudgetText = Color(0xFF243B53)
private val EditBudgetMuted = Color(0xFF71849A)
private val EditBudgetBlue = Color(0xFF5AA9E6)
private val EditBudgetLightBlue = Color(0xFFE4F2FF)
private val EditBudgetDeepBlue = Color(0xFF356F9F)

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
                    containerColor = EditBudgetBackground
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
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = EditBudgetLightBlue,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Savings,
                    contentDescription = null,
                    tint = EditBudgetDeepBlue
                )

                Text(
                    text = "Stay in control of your spending",
                    color = EditBudgetText,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 30.sp
                )

                Text(
                    text = "Adjust your monthly limit to match your current needs.",
                    color = EditBudgetMuted,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = EditBudgetCard
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    EditBudgetBorder
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text(
                        text = "Budget details",
                        color = EditBudgetText,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "CATEGORY",
                            color = EditBudgetMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = budget.category,
                            color = EditBudgetDeepBlue,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                focusedLabelColor = EditBudgetDeepBlue,
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
                            text = "Set a realistic limit for this category each month.",
                            color = EditBudgetMuted,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (isSaving) return@Button

                    val amount = amountText.toLongOrNull()

                    if (amount == null || amount <= 0L) {
                        errorMessage = "Please enter a valid budget amount greater than zero."
                        return@Button
                    }

                    isSaving = true

                    budgetViewModel.updateBudget(
                        budget.copy(amount = amount)
                    )

                    onBudgetUpdated()
                },
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(17.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EditBudgetBlue,
                    contentColor = Color.White,
                    disabledContainerColor = EditBudgetBorder,
                    disabledContentColor = EditBudgetMuted
                )
            ) {
                Text(
                    text = if (isSaving) "Updating..." else "Save Changes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = {
                errorMessage = null
            },
            title = {
                Text(
                    text = "Check your budget",
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
                        color = EditBudgetDeepBlue
                    )
                }
            },
            containerColor = EditBudgetCard
        )
    }
}
