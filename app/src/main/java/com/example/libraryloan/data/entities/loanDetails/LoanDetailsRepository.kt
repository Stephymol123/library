package com.example.libraryloan.data.entities.loanDetails

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.libraryloan.data.entities.book.Book
import com.example.libraryloan.utils.Constants

interface LoanDetailsRepository {
    suspend fun insert(loanDetails: LoanDetails)

    suspend fun delete(loanDetails: LoanDetails)

    fun getAllLoanDetailsByUserIdLive(userId: Int): LiveData<List<LoanDetails>>

    suspend fun getAllLoanDetailsByUserId(userId: Int): List<LoanDetails>?

    suspend fun getLoanDetails(userId: Int): LoanDetails?

    fun getAllPendingLoanDetailsByUserId(pageSize: Int, userId: Int): LiveData<PagingData<LoanDetails>>

    suspend fun changeStatus(loanDetailsId: Int, status: Constants.Status)
}

class OfflineLoanDetailsRepository(
    private val loanDetailsDao: LoanDetailsDao
) : LoanDetailsRepository {
    override suspend fun insert(loanDetails: LoanDetails) {
        loanDetailsDao.insert(loanDetails)
    }

    override suspend fun delete(loanDetails: LoanDetails) {
        loanDetailsDao.delete(loanDetails)
    }

    override fun getAllLoanDetailsByUserIdLive(userId: Int): LiveData<List<LoanDetails>> =
        loanDetailsDao.getAllLoanDetailsByUserIdLive(userId)

    override suspend fun getAllLoanDetailsByUserId(userId: Int): List<LoanDetails>? =
        loanDetailsDao.getAllLoanDetailsByUserId(userId)

    override suspend fun getLoanDetails(userId: Int): LoanDetails? {
        return loanDetailsDao.getLoanDetails(userId)
    }

    override fun getAllPendingLoanDetailsByUserId(
        pageSize: Int,
        userId: Int
    ): LiveData<PagingData<LoanDetails>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            loanDetailsDao.getAllPendingLoanDetailsByUserId(userId)
        }
    ).liveData

    override suspend fun changeStatus(loanDetailsId: Int, status: Constants.Status) {
        loanDetailsDao.changeStatus(loanDetailsId, status)
    }
}