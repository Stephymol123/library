package com.example.libraryloan.utils

import com.example.libraryloan.MainActivity
import com.example.libraryloan.R
import com.example.libraryloan.data.LibraryLoanPreferencesRepository
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import androidx.core.view.MenuProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.TimeZone

object HelperFunctions {
    /**
     * Creates a file directory and a .nomedia file if it does not exist in external files dir
     */
    fun createMediaDirectory(applicationContext: Context, directory: String, root: String) {
        val imageDirectory =
            File(
                Objects.requireNonNull<File>(
                    applicationContext.getExternalFilesDir(
                        root
                    )
                ).absoluteFile,
                directory
            )
        if (!imageDirectory.isDirectory) {
            imageDirectory.mkdirs()
        }
        val noMediaFile = File(imageDirectory.absoluteFile, Constants.NO_MEDIA_FILE)

        if (!noMediaFile.exists()) {
            val outNoMedia = FileOutputStream(noMediaFile)
            outNoMedia.flush()
            outNoMedia.close()
        }
    }

    fun setMenu(
        activity: FragmentActivity,
        libraryLoanPreferencesRepository: LibraryLoanPreferencesRepository
    ) {
        // Add menu items without using the Fragment Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State (here, RESUMED) to indicate when
        // the menu should be visible
        activity.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                menuItem.setChecked(true)
                Log.d("MainActivity", menuItem.itemId.toString())
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        activity.onBackPressedDispatcher.onBackPressed()
                        true
                    }

                    R.id.log_out -> {
                        activity.lifecycleScope.launch {
                            logout(libraryLoanPreferencesRepository, activity)
                        }
                        true
                    }

                    else -> false
                }
            }
        }, activity, Lifecycle.State.RESUMED)
    }

    private suspend fun logout(
        libraryLoanPreferencesRepository: LibraryLoanPreferencesRepository,
        activity: FragmentActivity
    ) {
        libraryLoanPreferencesRepository.clearDataStore()
        activity.finish()
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        activity.startActivity(intent)
    }

    fun displayDatePicker(
        editText: EditText,
        context: Context,
        minDate: Long? = null,
        maxDate: Long? = null,
    ) {
        val cldr = Calendar.getInstance()
        val day = cldr[Calendar.DAY_OF_MONTH]
        val month = cldr[Calendar.MONTH]
        val year = cldr[Calendar.YEAR]

        // date picker dialog
        val picker = DatePickerDialog(
            context,
            { _, selectedYear, monthOfYear, dayOfMonth ->
                // selected month usually starts from 0
                var mm = (monthOfYear + 1).toString()

                if (mm.length == 1) {
                    mm = "0$mm"
                }
                var dd = dayOfMonth.toString()
                if (dd.length == 1) {
                    dd = "0$dd"
                }
                val yearStr = "$dd-$mm-$selectedYear"
                val sdf = SimpleDateFormat(Constants.DATE_FORMAT_HYPHEN_DMY, Locale.getDefault())
                val date = sdf.parse(yearStr)
                val dateStr = date?.let { getDateString(Constants.DATE_FORMAT_FULL, it) }
                editText.setText(dateStr)
            }, year, month, day
        )
        if (minDate != null) {
            picker.datePicker.minDate = cldr.timeInMillis
        }
        if (maxDate != null) {
            picker.datePicker.maxDate = maxDate
        }
        picker.show()
    }

    /**
     * Get Date string in required format
     * Returns today's date string if no date is passed
     * @param keyDateFormat: options can be found in constants class
     */
    fun getDateString(
        keyDateFormat: String,
        date: Date = Date(),
        timeZone: String? = null
    ): String {
        val dateFormat: SimpleDateFormat = when (keyDateFormat) {
            Constants.DATE_FORMAT_HYPHEN_DMY -> {
                SimpleDateFormat(Constants.DATE_FORMAT_HYPHEN_DMY, Locale.getDefault())
            }

            Constants.DATE_FORMAT_FULL -> {
                SimpleDateFormat(Constants.DATE_FORMAT_FULL, Locale.getDefault())
            }

            else -> {
                SimpleDateFormat(Constants.DATE_FORMAT_HYPHEN_DMY, Locale.getDefault())
            }
        }

        if (!timeZone.isNullOrEmpty()) {
            dateFormat.timeZone = TimeZone.getTimeZone(timeZone)
        }
        return dateFormat.format(date)
    }
}