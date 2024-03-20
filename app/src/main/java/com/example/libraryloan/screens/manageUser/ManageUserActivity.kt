package com.example.libraryloan.screens.manageUser

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.libraryloan.R
import com.example.libraryloan.screens.adminHome.AdminHomePagingDataAdapter
import com.example.libraryloan.screens.adminHome.AdminHomeViewModel
import com.example.libraryloan.data.entities.loanDetails.LoanDetails
import com.example.libraryloan.databinding.ActivityHomeAdminBinding
import com.example.libraryloan.databinding.ActivityLoginBinding
import com.example.libraryloan.databinding.ActivityManageUserBinding
import com.example.libraryloan.screens.loanDetails.LoanDetailsActivity
import com.example.libraryloan.screens.login.LoginViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ManageUserActivity : AppCompatActivity(), ManageUserPagingDataAdapter.HomeListener {
    // Provide the viewModel's filter text
    private var viewModelFilterText: String? = null

    // Provide method search method to run after text change
    private var searchCallback: ((String) -> Unit)? = null

    // Provide binding's search button/image
    private var searchButton: ImageButton? = null

    // Provide binding's search edit text
    private var searchText: TextInputEditText? = null

    private var shouldClickToSearch = false

    private lateinit var adapter: ManageUserPagingDataAdapter
    private val viewModel: ManageUserViewModel by viewModels { ManageUserViewModel.Factory }
    private lateinit var binding: ActivityManageUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_user)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

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
        initRecycler()
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
        adapter = ManageUserPagingDataAdapter(this, this)
        viewModel.users.observe(this@ManageUserActivity) { pagingData ->
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

    override suspend fun getAllLoanDetails(userId: Int): List<LoanDetails>? =
        viewModel.getLoanDetails(userId)

    override fun navigateToLoanDetails(userId: Int) {
        val intent = Intent(this, LoanDetailsActivity::class.java)
        intent.putExtra("user_id", userId)
        startActivity(intent)
    }
}