package com.example.libraryloan.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.libraryloan.data.entities.book.Book
import com.example.libraryloan.data.entities.book.BookDao
import com.example.libraryloan.data.entities.loanDetails.LoanDetails
import com.example.libraryloan.data.entities.loanDetails.LoanDetailsDao
import com.example.libraryloan.data.entities.user.User
import com.example.libraryloan.data.entities.user.UserDao

@Database(
    entities = [
        User::class,
        Book::class,
    LoanDetails::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class LibraryDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bookDao(): BookDao
    abstract fun loanDetailsDao(): LoanDetailsDao


    companion object {
        @Volatile
        private var INSTANCE: LibraryDatabase? = null

        fun getInstance(context: Context): LibraryDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LibraryDatabase::class.java,
                        "library_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}