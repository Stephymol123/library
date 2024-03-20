package com.example.libraryloan.screens.registration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.libraryloan.MainActivity
import com.example.libraryloan.R
import com.example.libraryloan.databinding.ActivityRegistrationBinding
import com.example.libraryloan.utils.FormFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private val viewModel: RegisterViewModel by viewModels { RegisterViewModel.Factory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        with(binding) {
            firstName.doAfterTextChanged {
                FormFunctions.validateName(it.toString(), binding.firstNameLayout)
            }

            middleName.doAfterTextChanged {
                FormFunctions.validateName(it.toString(), binding.middleNameLayout)
            }
            lastName.doAfterTextChanged {
                FormFunctions.validateName(it.toString(), binding.lastNameLayout)
            }
            email.doAfterTextChanged {
                FormFunctions.validateEmail(it.toString(), binding.emailLayout)
            }
            password.doAfterTextChanged {
                FormFunctions.validatePassword(it.toString(), binding.passwordLayout)
            }
            confirmPassword.doAfterTextChanged {
                viewModel?.registerModel?.password?.let { password ->
                    FormFunctions.validateConfirmPassword(
                        it.toString(),
                        password,
                        binding.confirmPasswordLayout
                    )
                }
            }
            confirmPassword.setOnEditorActionListener { _, actionId, event ->
                if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                    register.performClick()
                }
                false
            }
            register.setOnClickListener {
                val (
                    firstName,
                    middleName,
                    lastName,
                    email,
                    password,
                    confirmPassword
                ) = viewModel?.registerModel ?: RegisterModel()
                val isFormValid = validateFields(
                    firstName = firstName,
                    middleName = middleName,
                    lastName = lastName,
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword
                )
                if (isFormValid) {
                    proceedToLoginScreen(email = email)
                } else {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Some fields require your attention.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    private fun proceedToLoginScreen(email: String) {
        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO) { viewModel.getUserDetails(email = email) }
            if (user == null) {
                viewModel.insertUser()
                navigateToLoginScreen()
                viewModel.resetRegisterModel()
            } else {
                Toast.makeText(
                    this@RegistrationActivity,
                    "User already exists!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun validateFields(
        firstName: String,
        middleName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        val isFirstNameValid = FormFunctions.validateName(firstName, binding.firstNameLayout)
        val isMiddleNameValid = FormFunctions.validateName(middleName, binding.middleNameLayout)
        val isLastNameValid = FormFunctions.validateName(lastName, binding.lastNameLayout)
        val isEmailValid = FormFunctions.validateEmail(email, binding.emailLayout)
        val isPasswordValid = FormFunctions.validatePassword(password, binding.passwordLayout)
        val isConfirmPasswordValid = FormFunctions.validateConfirmPassword(confirmPassword,
            password, binding.confirmPasswordLayout
        )

        return isFirstNameValid && isMiddleNameValid && isLastNameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid
    }
}