package com.example.libraryloan.screens.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.libraryloan.LibraryApplication
import com.example.libraryloan.data.entities.user.User
import com.example.libraryloan.data.entities.user.UserRepository

data class RegisterModel(
    var firstName: String = "",
    var middleName: String = "",
    var lastName: String = "",
    var email: String = "",
    var password: String = "",
    var confirmPassword: String = "",
)

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    private var _registerModel: RegisterModel = RegisterModel()
    val registerModel: RegisterModel = _registerModel

    suspend fun getUserDetails(email: String): User? {
        return userRepository.getUser(email = email)
    }

    suspend fun insertUser() {
        val (
            firstName,
            middleName,
            lastName,
            email,
            password
        ) = registerModel
        userRepository.insert(
            user = User(
                firstName = firstName,
                middleName = middleName,
                lastName = lastName,
                email = email,
                password = password

            )
        )
    }

    fun resetRegisterModel() {
        _registerModel = RegisterModel()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LibraryApplication)
                val userRepository = application.container.userRepository
                RegisterViewModel(userRepository = userRepository)
            }
        }
    }
}