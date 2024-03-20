package com.example.libraryloan.data.entities.book

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book")
data class Book(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "book_image")
    var bookImage: String?,

    @ColumnInfo(name = "quantity")
    var quantity: Int,

    @ColumnInfo(name = "quantity_in_stock")
    var quantityInStock: Int,

    @ColumnInfo(name = "isbn")
    var isbn: String,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "author")
    var author: String,

    @ColumnInfo(name = "published_date")
    var publishedDate: String?,

    @ColumnInfo(name = "category")
    var category: String,

    @ColumnInfo(name = "loan_period")
    var loanPeriod: String

    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(bookImage)
        parcel.writeInt(quantity)
        parcel.writeInt(quantityInStock)
        parcel.writeString(isbn)
        parcel.writeString(title)
        parcel.writeString(author)
        parcel.writeString(publishedDate)
        parcel.writeString(category)
        parcel.writeString(loanPeriod)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }
}