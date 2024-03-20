package com.example.libraryloan.screens.allBooks

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryloan.data.entities.book.Book
import com.example.libraryloan.databinding.AllBooksRecyclerBinding
import com.example.libraryloan.databinding.HomeRecyclerBinding

object ProductComparator : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem == newItem
    }
}

class AllBooksPagingDataAdapter (
    private val context: Context,
) :
    PagingDataAdapter<Book, AllBooksPagingDataAdapter.HomeViewHolder>(ProductComparator) {
    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        // create a new view
        val inflater = LayoutInflater.from(parent.context)
        val layoutBinding = AllBooksRecyclerBinding.inflate(inflater, parent, false)

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
    inner class HomeViewHolder(private val binding: AllBooksRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book?, position: Int) {
            try {
                if (book == null) {
                    return
                }

                binding.authorText.text = book.author
                binding.titleText.text = book.title
                binding.totalQuantityText.text=book.quantity.toString()
                binding.quantityInStockText.text=book.quantityInStock.toString()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}