package com.example.libraryloan.screens.adminHome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.libraryloan.R
import com.example.libraryloan.data.entities.book.Book
import com.example.libraryloan.databinding.ActivityHomeAdminBinding
import com.example.libraryloan.databinding.ActivityLoginBinding
import com.example.libraryloan.screens.addNewBook.AddNewBook
import com.example.libraryloan.screens.login.LoginViewModel
import com.example.libraryloan.screens.manageUser.ManageUserActivity
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class HomeAdmin : AppCompatActivity(), AdminHomePagingDataAdapter.HomeListener {
    // Provide the viewModel's filter text
    var viewModelFilterText: String? = null

    // Provide method search method to run after text change
    var searchCallback: ((String) -> Unit)? = null

    // Provide binding's search button/image
    var searchButton: ImageButton? = null

    // Provide binding's search edit text
    var searchText: TextInputEditText? = null

    var shouldClickToSearch = false

    private val viewModel: AdminHomeViewModel by viewModels { AdminHomeViewModel.Factory }
    private lateinit var binding: ActivityHomeAdminBinding
    private lateinit var adapter: AdminHomePagingDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_admin)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        searchButton = binding.imageButtonStopSearch
        searchText = binding.etSearch
        viewModelFilterText = viewModel.mFilterText
        searchCallback = { it -> viewModel.search(it) }

        if (shouldClickToSearch) {
            setupClickToSearch()
            return
        }
        searchText?.addTextChangedListener(SearchTextWatcher())
        setupStopSearchImageButton()

        binding.manageUsers.setOnClickListener {
            val intent = Intent(this, ManageUserActivity::class.java)
            startActivity(intent)
        }
        setOnBackPressedCallBack()

        lifecycleScope.launch {
            viewModel.insertBooks()
        }

        binding.addNew.setOnClickListener {
            val intent = Intent(this, AddNewBook::class.java)
            startActivity(intent)
        }
        initRecycler()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setOnBackPressedCallBack() {
        val callback: OnBackPressedCallback =
            object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finishAffinity()
                    exitProcess(0)
                }
            }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun setupClickToSearch() {
        searchText?.doAfterTextChanged {
            if (it.isNullOrBlank()) {
                searchCallback?.invoke("")
            }
        }
        searchButton?.setOnClickListener {
            searchCallback?.invoke(searchText?.text.toString())
        }
    }

    private fun setupStopSearchImageButton() {
        searchButton?.setOnClickListener {
            searchText?.setText("")
            searchText?.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(this, R.drawable.search_icon),
                null
            )
            it.visibility = View.GONE
        }
    }

    private fun setSearchIconAndStopSearchImageButtonVisibility() {
        if (viewModelFilterText == "%%") {
            searchButton?.visibility = View.GONE
            searchText?.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(
                    this,
                    R.drawable.search_icon
                ),
                null
            )
        } else {
            searchButton?.visibility = View.VISIBLE
            searchText?.setCompoundDrawables(
                null, null,
                null, null
            )
        }
    }

    /**
     * Text watcher class for handling recycler item filter
     */
    private inner class SearchTextWatcher : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(s: Editable?) {
            search(s.toString())
        }
    }

    private fun search(enteredText: String) {
        try {
            val filterString = enteredText.lowercase()
            viewModelFilterText = filterString
            searchCallback?.invoke(filterString)
            if (!shouldClickToSearch) {
                setSearchIconAndStopSearchImageButtonVisibility()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initRecycler() {
        adapter = AdminHomePagingDataAdapter(this, this)
        viewModel.products.observe(this@HomeAdmin) { pagingData ->
            // submitData suspends until loading this generation of data stops
            // so be sure to use collectLatest {} when presenting a Flow<PagingData>
            adapter.submitData(lifecycle, pagingData)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = adapter
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
    }

    override fun editBook(book: Book) {
        val intent = Intent(this, AddNewBook::class.java)
        intent.putExtra("book", book)
        startActivity(intent)
    }

    override fun deleteBook(book: Book) {
        lifecycleScope.launch {
            viewModel.deleteBook(book)
        }
    }
}