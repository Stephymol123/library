package com.example.libraryloan.data.entities.loanDetails

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.example.libraryloan.data.entities.book.Book
import com.example.libraryloan.utils.Constants

@Dao
interface LoanDetailsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(loanDetails: LoanDetails)

    @Delete
    suspend fun delete(loanDetails: LoanDetails)

    @Query(
        "SELECT * FROM loan_details  WHERE user_id LIKE :userId"
    )
    fun getAllLoanDetailsByUserIdLive(userId: Int): LiveData<List<LoanDetails>>

    @Query(
        "SELECT * FROM loan_details  WHERE user_id LIKE :userId"
    )
    suspend fun getAllLoanDetailsByUserId(userId: Int): List<LoanDetails>?

    @Query(
        "SELECT * FROM loan_details where user_id = :userId")
    suspend fun getLoanDetails(userId: Int): LoanDetails?

    @Query(
        "SELECT * FROM loan_details  WHERE user_id LIKE :userId and status = 'BORROWED'"
    )
    fun getAllPendingLoanDetailsByUserId(userId: Int): PagingSource<Int, LoanDetails>

    @Query(
        "UPDATE loan_details SET status = :status WHERE id = :loanDetailsId"
    )
    suspend fun changeStatus(loanDetailsId: Int, status: Constants.Status)
}