package com.example.libraryloan.screens.loanDetails

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.libraryloan.R
import com.example.libraryloan.data.entities.book.Book
import com.example.libraryloan.databinding.ActivityLoanDetailsBinding
import com.example.libraryloan.databinding.ActivityManageUserBinding
import com.example.libraryloan.screens.manageUser.ManageUserPagingDataAdapter
import com.example.libraryloan.screens.manageUser.ManageUserViewModel

class LoanDetailsActivity : AppCompatActivity(), LoanDetailsPagingDataAdapter.HomeListener {

    private lateinit var adapter: LoanDetailsPagingDataAdapter
    private val viewModel: LoanDetailsViewModel by viewModels { LoanDetailsViewModel.Factory }
    private lateinit var binding: ActivityLoanDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_loan_details)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        viewModel.userId = intent.getIntExtra("user_id", 0)

        initRecycler()
    }

    private fun initRecycler() {
        adapter = LoanDetailsPagingDataAdapter(this, this)
        viewModel.userId?.let {
            viewModel.getLoanDetails(it).observe(this@LoanDetailsActivity) { pagingData ->
                // submitData suspends until loading this generation of data stops
                // so be sure to use collectLatest {} when presenting a Flow<PagingData>
                adapter.submitData(lifecycle, pagingData)
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = adapter
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
    }

    override suspend fun getBook(bookId: Int): Book? {
        return viewModel.getBookById(bookId)
    }

    override suspend fun returnBook(loanDetailsId: Int, bookId: Int) {
        viewModel.returnBook(loanDetailsId, bookId)
    }
}