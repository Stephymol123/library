package com.example.libraryloan.screens.addNewBook

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.libraryloan.R
import com.example.libraryloan.data.entities.book.Book
import com.example.libraryloan.databinding.ActivityAddNewBookBinding
import com.example.libraryloan.utils.Constants
import com.example.libraryloan.utils.FormFunctions
import com.example.libraryloan.utils.HelperFunctions
import com.example.libraryloan.utils.HelperFunctions.displayDatePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AddNewBook : AppCompatActivity() {
    private val viewModel: AddNewBookViewModel by viewModels { AddNewBookViewModel.Factory }
    private lateinit var binding: ActivityAddNewBookBinding
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_book)
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.isImageAvailable = false

        viewModel.book = intent.getParcelableExtra("book", Book::class.java)
        viewModel?.book?.toString()?.let { Log.d("book", it) }
        setBinding()
        viewModel.book?.let { book ->
            viewModel.initializeAddNewModel(book)
        }
        setOnBackPressedCallback()
    }

    private fun loadImage(imgSrc: String) =
        Glide.with(this).load(imgSrc)
            .into(binding.productImageView)

    private fun setBinding() {
        with(binding) {
            titleEditText.doAfterTextChanged {
                FormFunctions.validateName(it.toString(), binding.titleLayout)
            }
            authorEditText.doAfterTextChanged {
                FormFunctions.validateName(it.toString(), binding.authorLayout)
            }

            isbnEditText.doAfterTextChanged {
                FormFunctions.validateISBN(it.toString(), binding.isbnLayout)
            }
            quantityEditText.doAfterTextChanged {
                FormFunctions.validateNumber(it.toString(), binding.quantityLayout)
            }
            imageEditText.doAfterTextChanged {
                FormFunctions.validateGeneral(it.toString(), binding.imageLayout)
            }
            publishedDateEditText.doAfterTextChanged {
                FormFunctions.validateGeneral(it.toString(), binding.pubDateLayout)
            }

            categoryEditText.doAfterTextChanged {
                FormFunctions.validateGeneral(it.toString(), binding.categoryLayout)
            }

            loanPeriodEditText.doAfterTextChanged {
                FormFunctions.validateGeneral(it.toString(), binding.loanPeriodLayout)
            }

            imageEditText.doOnTextChanged { text, _, _, _ ->
                isImageAvailable = !text.isNullOrEmpty()
                viewModel?.addNewModel?.bookImage = text.toString()
                viewModel?.addNewModel?.bookImage?.let { loadImage(it) }
            }
            publishedDateEditText.setOnClickListener {
                displayDatePicker(
                    binding.publishedDateEditText,
                    this@AddNewBook
                )
            }
            categoryEditText.setAdapter(
                viewModel?.categoryList?.let {
                    ArrayAdapter(
                        this@AddNewBook,
                        android.R.layout.simple_list_item_1,
                        it
                    )
                }
            )
            loanPeriodEditText.setAdapter(
                viewModel?.periodList?.let {
                    ArrayAdapter(
                        this@AddNewBook,
                        android.R.layout.simple_list_item_1,
                        it
                    )
                }
            )
            categoryEditText.setOnEditorActionListener { _, actionId, event ->
                if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                    btnSubmit.performClick()
                }
                false
            }
            btnSubmit.setOnClickListener {
                val (
                    title,
                    author,
                    isbn,
                    quantity,
                    bookImage,
                    publishedDate,
                    category,
                    loanPeriod
                ) = viewModel?.addNewModel?: AddNewModel()
                val isFormValid = validateFields(
                    title = title,
                    author = author,
                    isbn = isbn,
                    quantity = quantity,
                    bookImage = bookImage ?: "",
                    publishedDate = publishedDate,
                    category = category,
                    loanPeriod = loanPeriod,
                )
                if (isFormValid) {
                    lifecycleScope.launch {
                        Log.d("Book", viewModel?.addNewModel.toString())
                        Log.d("Book", viewModel?.addNewModel.toString())
                        val sdf = SimpleDateFormat(Constants.DATE_FORMAT_FULL, Locale.getDefault())
                        val date = viewModel?.addNewModel?.publishedDate?.let { it1 ->
                            sdf.parse(it1)
                        }
                        val dateStr =
                            date?.let {
                                HelperFunctions.getDateString(
                                    Constants.DATE_FORMAT_HYPHEN_DMY,
                                    it
                                )
                            }
                        viewModel?.addNewModel?.let { it1 ->
                            if (viewModel?.book == null) {
                                dateStr?.let { it2 ->
                                    Book(
                                        title = it1.title,
                                        author = it1.author,
                                        isbn = it1.isbn,
                                        quantity = it1.quantity.toInt(),
                                        quantityInStock = it1.quantity.toInt(),
                                        bookImage = it1.bookImage,
                                        publishedDate = it2,
                                        category = it1.category,
                                        loanPeriod = it1.loanPeriod
                                    )
                                }?.let { it3 ->
                                    viewModel?.insertProduct(
                                        it3
                                    )
                                }
                            } else {
                                viewModel?.book?.id?.let { it2 ->
                                    Book(
                                        id = it2,
                                        title = it1.title,
                                        author = it1.author,
                                        isbn = it1.isbn,
                                        quantity = it1.quantity.toInt(),
                                        quantityInStock = it1.quantity.toInt(),
                                        bookImage = it1.bookImage,
                                        publishedDate = dateStr,
                                        category = it1.category,
                                        loanPeriod = it1.loanPeriod
                                    )
                                }?.let { it3 -> viewModel?.updateProduct(it3) }
                            }
                        }
                        onBackPressedDispatcher.onBackPressed()
                    }
                } else {
                    Toast.makeText(
                        this@AddNewBook,
                        "Some fields require your attention.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun validateFields(
        title: String,
        author: String,
        isbn: String,
        quantity: String,
        bookImage: String,
        publishedDate: String,
        category: String,
        loanPeriod: String
    ): Boolean {
        val isNameValid = FormFunctions.validateName(title, binding.titleLayout)
        val isAuthorValid = FormFunctions.validateName(author, binding.authorLayout)
        val isISBNValid = FormFunctions.validateISBN(isbn, binding.isbnLayout)
        val isQuantityValid = FormFunctions.validateNumber(quantity, binding.quantityLayout)
        val isImgValid = FormFunctions.validateGeneral(bookImage, binding.imageLayout)
        val isPubDateValid = FormFunctions.validateGeneral(publishedDate, binding.pubDateLayout)
        val isCategoryValid = FormFunctions.validateGeneral(category, binding.categoryLayout)
        val isLoanPeriodValid = FormFunctions.validateGeneral(loanPeriod, binding.loanPeriodLayout)

        return isNameValid && isAuthorValid &&  isISBNValid && isQuantityValid && isImgValid && isPubDateValid && isCategoryValid && isLoanPeriodValid
    }

    private fun setOnBackPressedCallback() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        onBackPressedDispatcher.addCallback(this, callback)
    }
}