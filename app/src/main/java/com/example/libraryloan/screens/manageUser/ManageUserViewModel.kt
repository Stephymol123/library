package com.example.libraryloan.screens.manageUser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.libraryloan.LibraryApplication
import com.example.libraryloan.data.LibraryLoanPreferencesRepository
import com.example.libraryloan.data.entities.book.Book
import com.example.libraryloan.data.entities.loanDetails.LoanDetails
import com.example.libraryloan.data.entities.loanDetails.LoanDetailsRepository
import com.example.libraryloan.data.entities.user.User
import com.example.libraryloan.data.entities.user.UserRepository
import com.example.libraryloan.screens.login.LoginModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ManageUserViewModel(
    private val userRepository: UserRepository,
    private val loanDetailsRepository: LoanDetailsRepository,
    val libraryLoanPreferencesRepository: LibraryLoanPreferencesRepository,
) : ViewModel() {
    private var filterText: MutableLiveData<String> = MutableLiveData("%%")
    var mFilterText: String? = "%%"

    internal val users: LiveData<PagingData<User>> = filterText.switchMap {
        userRepository.getAllUsers(
            10,
            it,
        ).cachedIn(viewModelScope)
    }

    fun search(searchQuery: String) {
        val query = "%$searchQuery%"
        filterText.value = query
    }

    suspend fun getLoanDetails(userId: Int): List<LoanDetails>? {
        return loanDetailsRepository.getAllLoanDetailsByUserId(userId = userId)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LibraryApplication)
                val userRepository = application.container.userRepository
                val loanDetailsRepository = application.container.loanDetailsRepository
                val libraryLoanPreferencesRepository = application.libraryLoanPreferencesRepository
                ManageUserViewModel(
                    userRepository = userRepository,
                    loanDetailsRepository = loanDetailsRepository,
                    libraryLoanPreferencesRepository = libraryLoanPreferencesRepository
                )
            }
        }
    }
}