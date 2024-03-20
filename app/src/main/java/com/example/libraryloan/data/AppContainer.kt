package com.example.libraryloan.data

import android.content.Context
import com.example.libraryloan.data.entities.book.BookRepository
import com.example.libraryloan.data.entities.book.OfflineBookRepository
import com.example.libraryloan.data.entities.loanDetails.LoanDetailsRepository
import com.example.libraryloan.data.entities.loanDetails.OfflineLoanDetailsRepository
import com.example.libraryloan.data.entities.user.OfflineUserRepository
import com.example.libraryloan.data.entities.user.UserRepository

interface AppContainer {
    val userRepository: UserRepository
    val bookRepository: BookRepository
    val loanDetailsRepository:LoanDetailsRepository
}

class DefaultAppContainer(private val context: Context): AppContainer {
    override val userRepository: UserRepository by lazy {
        OfflineUserRepository(LibraryDatabase.getInstance(context = context).userDao())
    }
    override val bookRepository: BookRepository by lazy {
        OfflineBookRepository(LibraryDatabase.getInstance(context = context).bookDao())
    }
    override val loanDetailsRepository: LoanDetailsRepository by lazy {
        OfflineLoanDetailsRepository(LibraryDatabase.getInstance(context = context).loanDetailsDao())
    }

}