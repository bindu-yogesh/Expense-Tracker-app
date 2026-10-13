
package com.example.expensetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.expenseCategories
import com.example.expensetracker.data.local.BudgetEntity
import com.example.expensetracker.viewmodel.BudgetViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

private val AddBudgetBackground = Color(0xFFF3F8FC)
private val AddBudgetCard = Color.White
private val AddBudgetBorder = Color(0xFFE0EAF3)
private val AddBudgetText = Color(0xFF243B53)
private val AddBudgetMuted = Color(0xFF71849A)
private val AddBudgetBlue = Color(0xFF5AA9E6)
private val AddBudgetBlueDark = Color(0xFF356F9F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetScreen(
    budgetViewModel: BudgetViewModel,
    onBudgetSaved: () -> Unit,
    onBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val calendar = remember { Calendar.getInstance() }
    val month = calendar.get(Calendar.MONTH) + 1
    val year = calendar.get(Calendar.YEAR)

    Scaffold(
        containerColor = AddBudgetBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Budget",
                        color = AddBudgetText,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back",
                            tint = AddBudgetText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AddBudgetBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AddBudgetBackground)
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AddBudgetCard
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFE4F2FF)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = AddBudgetBlueDark,
                            modifier = Modifier.padding(12.dp).size(30.dp)
                        )
                    }

                    Text(
                        text = "Plan your spending",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = AddBudgetText
                    )

                    Text(
                        text = "Set a monthly limit for a category and stay in control of your expenses.",
                        fontSize = 14.sp,
                        color = AddBudgetMuted,
                        lineHeight = 21.sp
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AddBudgetCard
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text(
                        text = "Budget details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AddBudgetText
                    )

                    Text(
                        text = "Expense category",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AddBudgetText
                    )

                    // Category dropdown: the field itself handles the tap.
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            singleLine = true,
                            placeholder = {
                                Text(
                                    "Select a category",
                                    color = AddBudgetMuted
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Category,
                                    contentDescription = null,
                                    tint = AddBudgetBlueDark
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Choose category",
                                    tint = AddBudgetBlueDark
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = AddBudgetText,
                                disabledPlaceholderColor = AddBudgetMuted,
                                disabledLeadingIconColor = AddBudgetBlueDark,
                                disabledTrailingIconColor = AddBudgetBlueDark,
                                disabledBorderColor = AddBudgetBorder,
                                disabledContainerColor = AddBudgetCard
                            )
                        )

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { categoryExpanded = true }
                        )

                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            modifier = Modifier
                                .background(AddBudgetCard)
                                .heightIn(max = 280.dp)
                        ) {
                            expenseCategories.forEach { category ->
                                DropdownMenuItem(
                                    text = {
                                        Text(category, color = AddBudgetText)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Category,
                                            contentDescription = null,
                                            tint = AddBudgetBlueDark
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

                    Text(
                        text = "Monthly budget amount (₹)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AddBudgetText
                    )

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { value ->
                            if (
                                value.isEmpty() ||
                                value.matches(Regex("^\\d*\\.?\\d*$"))
                            ) {
                                amount = value
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("e.g. 5000", color = AddBudgetMuted)
                        },
                        leadingIcon = {
                            Text(
                                "₹",
                                color = AddBudgetBlueDark,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AddBudgetText,
                            unfocusedTextColor = AddBudgetText,
                            focusedBorderColor = AddBudgetBlue,
                            unfocusedBorderColor = AddBudgetBorder,
                            focusedContainerColor = AddBudgetCard,
                            unfocusedContainerColor = AddBudgetCard,
                            cursorColor = AddBudgetBlue
                        )
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFE4F2FF)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = AddBudgetBlueDark,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.size(10.dp))

                            Column {
                                Text(
                                    "Budget period",
                                    fontSize = 12.sp,
                                    color = AddBudgetMuted
                                )

                                Text(
                                    text = "${calendar.getDisplayName(
                                        Calendar.MONTH,
                                        Calendar.LONG,
                                        Locale.getDefault()
                                    )} $year",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AddBudgetBlueDark
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val parsedAmount = amount.toDoubleOrNull()

                            when {
                                selectedCategory.isBlank() -> {
                                    errorMessage = "Please select an expense category."
                                }

                                parsedAmount == null ||
                                        !parsedAmount.isFinite() ||
                                        parsedAmount <= 0.0 -> {
                                    errorMessage =
                                        "Please enter a valid amount greater than zero."
                                }

                                parsedAmount > Long.MAX_VALUE.toDouble() -> {
                                    errorMessage = "The budget amount is too large."
                                }

                                parsedAmount != parsedAmount.toLong().toDouble() -> {
                                    errorMessage =
                                        "Please enter a whole-number amount."
                                }

                                else -> {
                                    scope.launch {
                                        isSaving = true
                                        try {
                                            val existingBudget =
                                                budgetViewModel.getBudgetForCategory(
                                                    selectedCategory,
                                                    month,
                                                    year
                                                )

                                            if (existingBudget != null) {
                                                errorMessage =
                                                    "A budget for this category already exists this month. Please edit the existing budget."
                                            } else {
                                                budgetViewModel.addBudget(
                                                    BudgetEntity(
                                                        category = selectedCategory,
                                                        amount = parsedAmount.toLong(),
                                                        month = month,
                                                        year = year
                                                    )
                                                )
                                                onBudgetSaved()
                                            }
                                        } catch (e: Exception) {
                                            errorMessage =
                                                "Unable to save the budget. Please try again."
                                        } finally {
                                            isSaving = false
                                        }
                                    }
                                }
                            }
                        },
                        enabled = !isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AddBudgetBlue,
                            contentColor = Color.White,
                            disabledContainerColor = AddBudgetBorder
                        )
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = if (isSaving) "Saving..." else "Save Budget",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = {
                Text(
                    "Check budget details",
                    color = AddBudgetText,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(message, color = AddBudgetMuted)
            },
            confirmButton = {
                Button(
                    onClick = { errorMessage = null },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AddBudgetBlue
                    )
                ) {
                    Text("OK")
                }
            },
            containerColor = AddBudgetCard,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
