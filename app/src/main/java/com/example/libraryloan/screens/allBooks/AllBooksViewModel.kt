package com.example.libraryloan.screens.allBooks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.libraryloan.LibraryApplication
import com.example.libraryloan.data.LibraryLoanPreferencesRepository
import com.example.libraryloan.data.entities.book.Book
import com.example.libraryloan.data.entities.book.BookRepository
import com.example.libraryloan.data.entities.loanDetails.LoanDetails
import com.example.libraryloan.data.entities.loanDetails.LoanDetailsRepository
import com.example.libraryloan.data.entities.user.User
import com.example.libraryloan.utils.Constants
import kotlinx.coroutines.launch

class AllBooksViewModel(
    private val bookRepository: BookRepository,
    private val loanDetailsRepository: LoanDetailsRepository,
    libraryLoanPreferencesRepository: LibraryLoanPreferencesRepository
) : ViewModel() {

    private var filterText: MutableLiveData<String> = MutableLiveData("%%")
    var mFilterText: String? = "%%"

    fun search(searchQuery: String) {
        val query = "%$searchQuery%"
        filterText.value = query
    }

    internal val books: LiveData<PagingData<Book>> =
        bookRepository.getAllBook(
            10,
            mFilterText,
        ).cachedIn(viewModelScope)

    suspend fun getBookById(bookId: Int) =
        bookRepository.getBook(bookId)


    suspend fun returnBook(loanDetailsId: Int, bookId: Int) {
        loanDetailsRepository.changeStatus(loanDetailsId, Constants.Status.RETURNED)

        val book = bookRepository.getBook(bookId)
        book?.quantity?.let { book.quantity = it.plus(1) }
        book?.let { bookRepository.update(it) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LibraryApplication)
                val bookRepository = application.container.bookRepository
                val loanDetailsRepository = application.container.loanDetailsRepository
                val libraryLoanPreferencesRepository = application.libraryLoanPreferencesRepository
                AllBooksViewModel(
                    bookRepository = bookRepository,
                    loanDetailsRepository = loanDetailsRepository,
                    libraryLoanPreferencesRepository = libraryLoanPreferencesRepository
                )
            }
        }
    }
}