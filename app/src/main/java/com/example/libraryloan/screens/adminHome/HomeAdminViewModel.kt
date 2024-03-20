package com.example.libraryloan.screens.adminHome

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
import kotlinx.coroutines.launch

class AdminHomeViewModel(
    private val bookRepository: BookRepository,
    libraryLoanPreferencesRepository: LibraryLoanPreferencesRepository
) : ViewModel() {

    private var filterText: MutableLiveData<String> = MutableLiveData("%%")
    var mFilterText: String? = "%%"

    internal val products: LiveData<PagingData<Book>> = filterText.switchMap {
        bookRepository.getAllBook(
            10,
            it,
        ).cachedIn(viewModelScope)
    }

    fun search(searchQuery: String) {
        val query = "%$searchQuery%"
        filterText.value = query
    }

    fun insertBooks() {
        val books = arrayListOf(
            Book(
                id = 1,
                title = "First",
                author = "author",
                bookImage = null,
                isbn = "",
                category = "Fiction",
                publishedDate = "19-03-2022",
                quantity = 10,
                quantityInStock = 10,
                loanPeriod = ""
            ),
            Book(
                id = 2,
                title = "Second",
                author = "author",
                bookImage = null,
                isbn = "",
                category = "Non-fiction",
                publishedDate = "19-03-2023",
                quantity = 10,
                quantityInStock = 10,
                loanPeriod = "7"
            )
        )
        books.forEach { book ->
            viewModelScope.launch {
                bookRepository.insert(book = book)
            }
        }
    }

    suspend fun deleteBook(book: Book) {
        bookRepository.delete(book)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LibraryApplication)
                val bookRepository = application.container.bookRepository
                val libraryLoanPreferencesRepository = application.libraryLoanPreferencesRepository
                AdminHomeViewModel(
                    bookRepository = bookRepository,
                    libraryLoanPreferencesRepository = libraryLoanPreferencesRepository
                )
            }
        }
    }
}