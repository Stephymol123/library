package com.example.libraryloan.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.libraryloan.LibraryApplication
import com.example.libraryloan.data.LibraryLoanPreferencesRepository
import com.example.libraryloan.data.entities.user.User
import com.example.libraryloan.data.entities.user.UserRepository
import com.example.libraryloan.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LoginModel(
    var email: String = "",
    var password: String = "",
)

class LoginViewModel(
    private val userRepository: UserRepository,
    val libraryLoanPreferencesRepository: LibraryLoanPreferencesRepository,
) : ViewModel() {
    private var _loginModel: MutableStateFlow<LoginModel> = MutableStateFlow(LoginModel())
    val loginModel: StateFlow<LoginModel> = _loginModel.asStateFlow()

    suspend fun getUserDetails(email: String): User? {
        return userRepository.getUser(email = email)
    }

    fun resetLoginModel() {
        _loginModel.value = LoginModel()
    }
    suspend fun savePreferences(it: User) {
        libraryLoanPreferencesRepository.savePreference(Constants.USER, it)
        libraryLoanPreferencesRepository.savePreference(Constants.IS_LOGGED_IN, true)
    }

    suspend fun saveAdminPreferences() {
        libraryLoanPreferencesRepository.savePreference(Constants.IS_ADMIN, true)
        libraryLoanPreferencesRepository.savePreference(Constants.IS_LOGGED_IN, true)
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LibraryApplication)
                val userRepository = application.container.userRepository
                val libraryLoanPreferencesRepository = application.libraryLoanPreferencesRepository
                LoginViewModel(userRepository = userRepository, libraryLoanPreferencesRepository = libraryLoanPreferencesRepository)
            }
        }
    }
}