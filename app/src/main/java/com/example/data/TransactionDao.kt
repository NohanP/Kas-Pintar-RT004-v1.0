package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.TransactionEntity
import com.example.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC, id DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE month = :month AND year = :year ORDER BY dateMillis DESC, id DESC")
    fun getTransactionsByMonthYear(month: Int, year: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE citizenId = :citizenId ORDER BY dateMillis DESC")
    fun getTransactionsByCitizen(citizenId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type AND month = :month AND year = :year")
    suspend fun getTransactionsByTypeAndPeriod(type: TransactionType, month: Int, year: Int): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE citizenId = :citizenId AND month = :month AND year = :year LIMIT 1")
    suspend fun getCitizenDuesForMonth(citizenId: Long, month: Int, year: Int): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'PEMASUKAN'")
    fun getTotalIncomeFlow(): Flow<Long>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'PENGELUARAN'")
    fun getTotalExpenseFlow(): Flow<Long>
}
