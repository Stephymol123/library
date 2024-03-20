package com.example.libraryloan

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.libraryloan.data.AppContainer
import com.example.libraryloan.data.DefaultAppContainer
import com.example.libraryloan.data.LibraryLoanPreferencesRepository

private const val LIBRARY_LOAN_PREFERENCE_NAME = "libraryLoan_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = LIBRARY_LOAN_PREFERENCE_NAME
)

class LibraryApplication : Application() {
    lateinit var container: AppContainer
    lateinit var libraryLoanPreferencesRepository: LibraryLoanPreferencesRepository
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        libraryLoanPreferencesRepository = LibraryLoanPreferencesRepository(dataStore)
    }
}