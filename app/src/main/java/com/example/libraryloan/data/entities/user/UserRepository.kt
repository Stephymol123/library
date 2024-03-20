package com.example.libraryloan.data.entities.user

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.libraryloan.data.entities.book.Book
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun insert(user: User)
    suspend fun getAllUsers(): LiveData<List<User>>
    suspend fun getUser(email: String): User?
    fun getAllUsers(pageSize: Int, filterText: String?): LiveData<PagingData<User>>
}

class OfflineUserRepository(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun insert(user: User) {
        userDao.insert(user = user)
    }

    override suspend fun getAllUsers(): LiveData<List<User>> =
        userDao.getAllUsers()

    override fun getAllUsers(pageSize: Int, filterText: String?): LiveData<PagingData<User>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            userDao.getAllUsers(filterText)
        }
    ).liveData

    override suspend fun getUser(email: String): User? =
        userDao.getUser(email = email)
}