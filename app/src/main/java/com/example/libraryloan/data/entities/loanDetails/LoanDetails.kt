package com.example.libraryloan.data.entities.loanDetails

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.libraryloan.utils.Constants

@Entity(tableName = "loan_details")
data class LoanDetails(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "book_id")
    var bookId: Int,

    @ColumnInfo(name = "user_id")
    var userId: Int,

    @ColumnInfo(name = "borrow_date")
    var borrowedDate: String,

    @ColumnInfo(name = "due_date")
    var dueDate: String,

    @ColumnInfo(name = "status")
    var status: Constants.Status,
    )