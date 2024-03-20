package com.example.libraryloan.screens.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.libraryloan.R
import com.example.libraryloan.screens.adminHome.HomeAdmin
import com.example.libraryloan.databinding.ActivityLoginBinding
import com.example.libraryloan.screens.registration.RegisterModel
import com.example.libraryloan.screens.registration.RegisterViewModel
import com.example.libraryloan.screens.registration.RegistrationActivity
import com.example.libraryloan.utils.FormFunctions.validateLoginEmail
import com.example.libraryloan.utils.FormFunctions.validateLoginPassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels { LoginViewModel.Factory }
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        with(binding) {

            username.doAfterTextChanged {
                validateLoginEmail(it.toString(), binding.usernameLayout)
            }
            password.doAfterTextChanged {
                validateLoginPassword(it.toString(), binding.passwordLayout)
            }
            password.setOnEditorActionListener { _, actionId, event ->
                if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                    loginbtn.performClick()
                }
                false
            }



            loginbtn.setOnClickListener {
                val (email, password) = viewModel?.loginModel?.value ?: LoginModel()
                val isFormValid = validateFields(email = email, password = password)
                if (isFormValid) {
                    proceedToHomePage(email, password)
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Some fields require your attention.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            registrationbtn.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
                startActivity(intent)
            }
        }
    }
    private fun proceedToHomePage(
        email: String,
        password: String
    ) {
        lifecycleScope.launch {
            if (email.equals(other = "admin", ignoreCase = true) && password.equals(
                    other = "admin",
                    ignoreCase = true
                )
            ) {
                viewModel.saveAdminPreferences()

              val intent = Intent(this@LoginActivity, HomeAdmin::class.java)
               startActivity(intent)
            } else {
                val user =
                    withContext(Dispatchers.IO) { viewModel.getUserDetails(email = email) }
                if (user != null) {
                    if (user.password == password) {
                        viewModel.savePreferences(user)
//                        val intent = Intent(this, AppActivity::class.java)
//                        startActivity(intent)
                        viewModel.resetLoginModel()
                    } else {
                        binding.passwordLayout.error = "Incorrect password"
                    }
                } else {
                    binding.usernameLayout.error = "This email is not registered"
                }
            }
        }
    }

    private fun navigateToRegisterScreen() {

    }

    private fun validateFields(email: String, password: String): Boolean {
        val isEmailValid = validateLoginEmail(email, binding.usernameLayout)
        val isPasswordValid = validateLoginPassword(password, binding.passwordLayout)

        return isEmailValid && isPasswordValid
    }
}
