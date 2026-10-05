package com.example.expensetracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert
    suspend fun insertBudget(budget: BudgetEntity)

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    @Query("SELECT * FROM budgets ORDER BY category ASC")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Query(
        "SELECT * FROM budgets " +
                "WHERE month = :month AND year = :year"
    )
    fun getBudgetsForMonth(
        month: Int,
        year: Int
    ): Flow<List<BudgetEntity>>

    @Query(
        "SELECT * FROM budgets " +
                "WHERE category = :category " +
                "AND month = :month " +
                "AND year = :year " +
                "LIMIT 1"
    )
    suspend fun getBudgetForCategory(
        category: String,
        month: Int,
        year: Int
    ): BudgetEntity?
}