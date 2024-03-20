package com.example.libraryloan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.example.libraryloan.screens.adminHome.HomeAdmin
import com.example.libraryloan.screens.login.LoginActivity
import com.example.libraryloan.screens.login.LoginViewModel
import com.example.libraryloan.utils.Constants.IS_ADMIN
import com.example.libraryloan.utils.Constants.IS_LOGGED_IN

class MainActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels { LoginViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val isLoggedIn = viewModel.libraryLoanPreferencesRepository.getPreference(
            Boolean::class.java,
            IS_LOGGED_IN
        )
        val isAdmin = viewModel.libraryLoanPreferencesRepository.getPreference(
            Boolean::class.java,
            IS_ADMIN
        )
        isLoggedIn.observe(this) {
            if (it == true) {

                isAdmin.observe(this) { admin ->
                    if (admin == true) {
                        val intent = Intent(this, HomeAdmin::class.java)
                        startActivity(intent)
                    } else {

                    }
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
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
}