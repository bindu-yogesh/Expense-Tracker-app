
package com.example.expensetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.expenseCategories
import com.example.expensetracker.data.local.BudgetEntity
import com.example.expensetracker.viewmodel.BudgetViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.util.Calendar

private val BudgetText = Color(0xFF0F172A)
private val BudgetBackground = Color(0xFFF8FAFC)
private val BudgetFieldBackground = Color.White
private val BudgetBorder = Color(0xFF64748B)
private val BudgetBlue = Color(0xFF2563EB)

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
        containerColor = BudgetBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Budget",
                        color = BudgetText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = BudgetText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BudgetBackground,
                    titleContentColor = BudgetText,
                    navigationIconContentColor = BudgetText
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Set a monthly spending limit",
                color = BudgetText,
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium
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
                    label = {
                        Text("Category")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = categoryExpanded
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BudgetText,
                        unfocusedTextColor = BudgetText,
                        focusedLabelColor = BudgetText,
                        unfocusedLabelColor = BudgetText,
                        focusedBorderColor = BudgetBlue,
                        unfocusedBorderColor = BudgetBorder,
                        focusedContainerColor = BudgetFieldBackground,
                        unfocusedContainerColor = BudgetFieldBackground,
                        focusedTrailingIconColor = BudgetText,
                        unfocusedTrailingIconColor = BudgetText
                    )
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = {
                        categoryExpanded = false
                    }
                ) {
                    expenseCategories.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = category,
                                    color = BudgetText
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

            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    amountText = input.filter { it.isDigit() }
                },
                label = {
                    Text("Monthly Budget")
                },
                prefix = {
                    Text("₹")
                },
                placeholder = {
                    Text("Enter amount")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = BudgetText,
                    unfocusedTextColor = BudgetText,
                    focusedLabelColor = BudgetText,
                    unfocusedLabelColor = BudgetText,
                    focusedPlaceholderColor = BudgetBorder,
                    unfocusedPlaceholderColor = BudgetBorder,
                    focusedBorderColor = BudgetBlue,
                    unfocusedBorderColor = BudgetBorder,
                    focusedContainerColor = BudgetFieldBackground,
                    unfocusedContainerColor = BudgetFieldBackground,
                    focusedPrefixColor = BudgetText,
                    unfocusedPrefixColor = BudgetText,
                    cursorColor = BudgetBlue
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    if (isSaving) {
                        return@Button
                    }

                    val amount = amountText.toLongOrNull()

                    if (amount == null || amount <= 0L) {
                        errorMessage =
                            "Please enter a valid budget amount."
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isSaving) {
                        "Saving..."
                    } else {
                        "Save Budget"
                    }
                )
            }
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
                    color = BudgetText
                )
            },
            text = {
                Text(
                    text = errorMessage.orEmpty(),
                    color = BudgetText
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        errorMessage = null
                    }
                ) {
                    Text("OK")
                }
            },
            containerColor = Color.White
        )
    }
}
