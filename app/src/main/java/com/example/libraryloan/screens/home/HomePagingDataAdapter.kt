package com.example.libraryloan.screens.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryloan.data.entities.book.Book
import com.example.libraryloan.data.entities.loanDetails.LoanDetails
import com.example.libraryloan.databinding.HomeRecyclerBinding
import com.example.libraryloan.databinding.LoanDetailsRecyclerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ProductComparator : DiffUtil.ItemCallback<LoanDetails>() {
    override fun areItemsTheSame(oldItem: LoanDetails, newItem: LoanDetails): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LoanDetails, newItem: LoanDetails): Boolean {
        return oldItem == newItem
    }
}

class HomePagingDataAdapter(
    private val context: Context,
    private val listener: HomeListener,
) :
    PagingDataAdapter<LoanDetails, HomePagingDataAdapter.HomeViewHolder>(ProductComparator) {
    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        // create a new view
        val inflater = LayoutInflater.from(parent.context)
        val layoutBinding = HomeRecyclerBinding.inflate(inflater, parent, false)

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
    inner class HomeViewHolder(private val binding: HomeRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(loanDetails: LoanDetails?, position: Int) {
            try {
                if (loanDetails == null) {
                    return
                }

                binding.borrowText.text = ""
                binding.returnText.text = ""

                CoroutineScope(Dispatchers.Main).launch {
                    val book = listener.getBook(loanDetails.bookId)
                    binding.titleText.text = book?.title
                    binding.authorText.text = book?.author
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface HomeListener {
        suspend fun getBook(bookId: Int): Book?
    }
}