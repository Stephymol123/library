package com.example.libraryloan.screens.addNewBook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.libraryloan.LibraryApplication
import com.example.libraryloan.data.entities.book.Book
import com.example.libraryloan.data.entities.book.BookRepository
import com.example.libraryloan.utils.Constants
import com.example.libraryloan.utils.HelperFunctions
import java.text.SimpleDateFormat
import java.util.Locale

data class AddNewModel(
    var title: String = "",
    var author: String = "",
    var isbn: String = "",
    var quantity: String = "",
    var bookImage: String? = null,
    var publishedDate: String = "",
    var category: String = "",
    var quantityInStock: String = "",
    var loanPeriod: String = "",

    )

class AddNewBookViewModel(private val bookRepository: BookRepository) : ViewModel() {
    var addNewModel: AddNewModel = AddNewModel()
    var book: Book? = null
    val categoryList = listOf("Fiction", "Non-fiction")
    val periodList = listOf("1", "2", "7", "14")

    suspend fun getBookDetails(id: Int): Book? {
        return bookRepository.getBook(id = id)
    }

    suspend fun insertBook() {
        val (

            title,
            author,
            isbn,
            quantity,
            bookImage,
            publishedDate,
            category,
            quantityInStock,
            loanPeriod
        ) = addNewModel
        bookRepository.insert(
            book = Book(
                bookImage = bookImage,
                isbn = isbn,
                title = title,
                author = author,
                publishedDate = publishedDate,
                category = category,
                quantity = quantity.toInt(),
                quantityInStock = quantityInStock.toInt(),
                loanPeriod = loanPeriod,
            )
        )
    }

    private fun productToProductModel(book: Book): AddNewModel {
        val sdf = SimpleDateFormat(Constants.DATE_FORMAT_HYPHEN_DMY, Locale.getDefault())
        val date = book.publishedDate?.let { sdf.parse(it) }
        val dateStr = date?.let { HelperFunctions.getDateString(Constants.DATE_FORMAT_FULL, it) }
        return AddNewModel(
            title = book.title,
            author = book.author ?: "",
            isbn = book.isbn ?: "",
            quantity = book.quantity.toString(),
            bookImage = book.bookImage,
            publishedDate = dateStr.toString(),
            category = book.category ?: "",
            loanPeriod = book.loanPeriod ?: ""

            )
    }

    fun initializeAddNewModel(book: Book) {
        addNewModel = productToProductModel(book)
    }

    suspend fun insertProduct(book: Book) {
        bookRepository.insert(book = book)
    }

    suspend fun updateProduct(book: Book) {
        bookRepository.update(book = book)
    }

    suspend fun update(book: Book) {
        bookRepository.update(book = book)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LibraryApplication)
                val bookRepository = application.container.bookRepository
                AddNewBookViewModel(
                    bookRepository = bookRepository,
                )
            }
        }
    }
}