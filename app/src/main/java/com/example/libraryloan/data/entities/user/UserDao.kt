package com.example.libraryloan.data.entities.user

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.libraryloan.data.entities.book.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM user")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM user where email = :email")
    fun getUser(email: String): User?

    @Query(
        "SELECT * FROM user WHERE LOWER(first_name) LIKE LOWER(:filterText) OR LOWER(last_name) LIKE LOWER(:filterText) OR LOWER(first_name || ' ' || last_name) LIKE LOWER(:filterText)"
    )
    fun getAllUsers(filterText: String?): PagingSource<Int, User>
}