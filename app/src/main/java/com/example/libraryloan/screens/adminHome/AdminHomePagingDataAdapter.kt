package com.example.libraryloan.screens.adminHome

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.libraryloan.data.entities.book.Book
import com.example.libraryloan.databinding.AdminHomeItemBinding

object ProductComparator : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem == newItem
    }
}

class AdminHomePagingDataAdapter(
    private val context: Context,
    private val listener: HomeListener,
) :
    PagingDataAdapter<Book, AdminHomePagingDataAdapter.HomeViewHolder>(ProductComparator) {
    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        // create a new view
        val inflater = LayoutInflater.from(parent.context)
        val layoutBinding = AdminHomeItemBinding.inflate(inflater, parent, false)

        return HomeViewHolder(layoutBinding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val item = getItem(position)
        // Note that item may be null. ViewHolder must support binding a
        // null item as a placeholder.
        holder.bind(item, position)
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a Product object.
    inner class HomeViewHolder(private val binding: AdminHomeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book?, position: Int) {
            try {
                if (book == null) {
                    return
                }

                Glide.with(context).load(book.bookImage)
                    .into(binding.imageView)
                //setup cart qty control
                binding.edit.setOnClickListener {
                    listener.editBook(book = book)
                }
                binding.delete.setOnClickListener {
                    listener.deleteBook(book = book)
                }

                binding.titleText.text = book.title
                binding.authorText.text = book.author
                binding.category.text = book.category


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface HomeListener {
        fun editBook(book: Book)
        fun deleteBook(book: Book)
    }
}