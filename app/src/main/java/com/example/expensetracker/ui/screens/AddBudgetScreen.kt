
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.expenseCategories
import com.example.expensetracker.data.local.BudgetEntity
import com.example.expensetracker.viewmodel.BudgetViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.util.Calendar

private val AddBudgetBackground = Color(0xFF0B1120)
private val AddBudgetCard = Color(0xFF151F32)
private val AddBudgetBorder = Color(0xFF29364B)
private val AddBudgetText = Color(0xFFF8FAFC)
private val AddBudgetMuted = Color(0xFF94A3B8)
private val AddBudgetBlue = Color(0xFF60A5FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetScreen(
    budgetViewModel: BudgetViewModel,
    onBudgetSaved: () -> Unit,
    onBack: () -> Unit
) {
    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    var selectedCategory by remember {
        mutableStateOf(expenseCategories.first())
    }

    var amountText by remember {
        mutableStateOf("")
    }

    var categoryExpanded by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH) + 1
    val currentYear = calendar.get(Calendar.YEAR)

    Scaffold(
        containerColor = AddBudgetBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Add Budget",
                            color = AddBudgetText,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Plan your monthly spending",
                            color = AddBudgetMuted,
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AddBudgetText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AddBudgetBackground,
                    titleContentColor = AddBudgetText,
                    navigationIconContentColor = AddBudgetText
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AddBudgetBackground)
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        AddBudgetCard,
                        RoundedCornerShape(22.dp)
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = "Create a budget",
                    color = AddBudgetText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Choose a category and set how much you want to spend this month.",
                    color = AddBudgetMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "CATEGORY",
                    color = AddBudgetMuted,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = {
                        categoryExpanded = !categoryExpanded
                    }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null,
                                tint = AddBudgetBlue
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = categoryExpanded
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(16.dp),
                        colors = budgetFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = {
                            categoryExpanded = false
                        },
                        modifier = Modifier.background(AddBudgetCard)
                    ) {
                        expenseCategories.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = category,
                                        color = AddBudgetText
                                    )
                                },
                                onClick = {
                                    selectedCategory = category
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "MONTHLY LIMIT",
                    color = AddBudgetMuted,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        amountText = input.filter { it.isDigit() }
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
                            tint = AddBudgetBlue
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
                    colors = budgetFieldColors()
                )

                Text(
                    text = "Set a realistic limit that works for your monthly expenses.",
                    color = AddBudgetMuted,
                    style = MaterialTheme.typography.bodySmall
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
                    errorMessage = null

                    scope.launch {
                        try {
                            val existingBudget =
                                budgetViewModel.getBudgetForCategory(
                                    category = selectedCategory,
                                    month = currentMonth,
                                    year = currentYear
                                )

                            if (existingBudget != null) {
                                errorMessage =
                                    "A budget for $selectedCategory already exists for this month. Please edit the existing budget."
                            } else {
                                budgetViewModel.addBudget(
                                    BudgetEntity(
                                        category = selectedCategory,
                                        amount = amount,
                                        month = currentMonth,
                                        year = currentYear
                                    )
                                )

                                onBudgetSaved()
                            }
                        } catch (exception: CancellationException) {
                            throw exception
                        } catch (exception: Exception) {
                            errorMessage =
                                "Couldn't check or save the budget. Please try again."
                        } finally {
                            isSaving = false
                        }
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
                    disabledContentColor = AddBudgetMuted
                )
            ) {
                Text(
                    text = if (isSaving) "Saving..." else "Save Budget",
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
                    text = "Budget not saved",
                    color = AddBudgetText,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = errorMessage.orEmpty(),
                    color = AddBudgetMuted
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
                        color = AddBudgetBlue
                    )
                }
            },
            containerColor = AddBudgetCard
        )
    }
}

@Composable
private fun budgetFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = AddBudgetText,
    unfocusedTextColor = AddBudgetText,
    focusedLabelColor = AddBudgetBlue,
    unfocusedLabelColor = AddBudgetMuted,
    focusedPlaceholderColor = AddBudgetMuted,
    unfocusedPlaceholderColor = AddBudgetMuted,
    focusedBorderColor = AddBudgetBlue,
    unfocusedBorderColor = AddBudgetBorder,
    focusedContainerColor = AddBudgetCard,
    unfocusedContainerColor = AddBudgetCard,
    focusedLeadingIconColor = AddBudgetBlue,
    unfocusedLeadingIconColor = AddBudgetMuted,
    focusedTrailingIconColor = AddBudgetBlue,
    unfocusedTrailingIconColor = AddBudgetMuted,
    focusedPrefixColor = AddBudgetText,
    unfocusedPrefixColor = AddBudgetText,
    cursorColor = AddBudgetBlue
)
