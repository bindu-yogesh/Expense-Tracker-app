package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.BudgetDao
import com.example.expensetracker.data.local.BudgetEntity
import kotlinx.coroutines.flow.Flow

class BudgetRepository(
    private val budgetDao: BudgetDao
) {

    val allBudgets: Flow<List<BudgetEntity>> =
        budgetDao.getAllBudgets()

    fun getBudgetsForMonth(
        month: Int,
        year: Int
    ): Flow<List<BudgetEntity>> {
        return budgetDao.getBudgetsForMonth(
            month = month,
            year = year
        )
    }

    suspend fun insertBudget(
        budget: BudgetEntity
    ) {
        budgetDao.insertBudget(budget)
    }

    suspend fun updateBudget(
        budget: BudgetEntity
    ) {
        budgetDao.updateBudget(budget)
    }

    suspend fun deleteBudget(
        budget: BudgetEntity
    ) {
        budgetDao.deleteBudget(budget)
    }

    suspend fun getBudgetForCategory(
        category: String,
        month: Int,
        year: Int
    ): BudgetEntity? {
        return budgetDao.getBudgetForCategory(
            category = category,
            month = month,
            year = year
        )
    }
}