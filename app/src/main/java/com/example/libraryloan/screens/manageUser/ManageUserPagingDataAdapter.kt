package com.example.libraryloan.screens.manageUser

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryloan.data.entities.loanDetails.LoanDetails
import com.example.libraryloan.data.entities.user.User
import com.example.libraryloan.databinding.ManageUserRecyclerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ProductComparator : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}

class ManageUserPagingDataAdapter(
    private val context: Context,
    private val listener: HomeListener,
) :
    PagingDataAdapter<User, ManageUserPagingDataAdapter.HomeViewHolder>(ProductComparator) {
    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        // create a new view
        val inflater = LayoutInflater.from(parent.context)
        val layoutBinding = ManageUserRecyclerBinding.inflate(inflater, parent, false)

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
    inner class HomeViewHolder(private val binding: ManageUserRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User?, position: Int) {
            try {
                if (user == null) {
                    return
                }

                binding.details.setOnClickListener {
                    listener.navigateToLoanDetails(user.id)
                }

                binding.nameText.text = "${user.firstName} ${user.lastName}"

                CoroutineScope(Dispatchers.Main).launch {
                    val loanDetails = listener.getAllLoanDetails(user.id)
                    binding.noBookText.text = loanDetails?.size.toString()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface HomeListener {
        suspend fun getAllLoanDetails(userId: Int): List<LoanDetails>?
        fun navigateToLoanDetails(userId: Int)
    }
}