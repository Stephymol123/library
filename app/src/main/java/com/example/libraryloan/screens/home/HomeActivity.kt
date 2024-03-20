package com.example.libraryloan.screens.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.libraryloan.R
import com.example.libraryloan.data.entities.book.Book
import com.example.libraryloan.databinding.ActivityHomeBinding
import com.example.libraryloan.databinding.ActivityLoanDetailsBinding
import com.example.libraryloan.screens.loanDetails.LoanDetailsPagingDataAdapter
import com.example.libraryloan.screens.loanDetails.LoanDetailsViewModel

class HomeActivity : AppCompatActivity(), HomePagingDataAdapter.HomeListener {

    private lateinit var adapter: HomePagingDataAdapter
    private val viewModel: HomeViewModel by viewModels { HomeViewModel.Factory }
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        initRecycler()
    }

    private fun initRecycler() {
        adapter = HomePagingDataAdapter(this, this)
        viewModel.loggedInUser.observe(this) { userInfo ->
            userInfo?.id?.let {
                viewModel.getLoanDetails(it).observe(this@HomeActivity) { pagingData ->
                    // submitData suspends until loading this generation of data stops
                    // so be sure to use collectLatest {} when presenting a Flow<PagingData>
                    adapter.submitData(lifecycle, pagingData)
                }
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
}