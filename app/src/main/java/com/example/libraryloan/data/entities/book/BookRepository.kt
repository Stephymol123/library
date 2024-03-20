package com.example.libraryloan.data.entities.book

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData

interface BookRepository  {
    suspend fun insert(book: Book)
    suspend fun update(book: Book)
    suspend fun delete(book: Book)
    fun getAllBook(pageSize: Int, filterText: String?): LiveData<PagingData<Book>>
    suspend fun getBook(id: Int): Book?
}

 class OfflineBookRepository(
    private val bookDao: BookDao
) : BookRepository {
     override suspend fun insert(book: Book) {
         bookDao.insert(book)
     }

     override suspend fun update(book: Book) {
         bookDao.update(book)
     }

     override suspend fun delete(book: Book) {
         bookDao.delete(book)
     }

     override fun getAllBook(pageSize: Int, filterText: String?): LiveData<PagingData<Book>> = Pager(
         config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
         pagingSourceFactory = {
             bookDao.getAllBook(filterText)
         }
     ).liveData

     override suspend fun getBook(id: Int): Book? {
         return bookDao.getBook(id)
     }

 }