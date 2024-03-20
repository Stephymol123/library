package com.example.libraryloan.data.entities.book

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(book: Book)

    @Update
    suspend fun update(book: Book)

    @Delete
    suspend fun delete(book: Book)

    @Query(
        "SELECT * FROM book WHERE LOWER(title) LIKE LOWER(:filterText)"
    )
    fun getAllBook(filterText: String?): PagingSource<Int, Book>

    @Query(
        "SELECT * FROM book where id = :id")
    fun getBook(id: Int): Book?
}