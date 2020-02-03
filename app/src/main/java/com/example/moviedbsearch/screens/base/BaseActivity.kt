package com.example.moviedbsearch.screens.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.moviedbsearch.BuildConfig
import com.example.moviedbsearch.R
import com.example.moviedbsearch.api.MoviesApiService
import com.example.moviedbsearch.extensions.beGone
import com.example.moviedbsearch.extensions.beVisible
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_movies_list.*
import kotlinx.coroutines.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton

abstract class BaseActivity : AppCompatActivity() {
    private val job = SupervisorJob()
    private val errorHandler = CoroutineExceptionHandler { _, throwable -> handleError(throwable) }
    protected val scopeUi = CoroutineScope(Dispatchers.Main + job + errorHandler)
    protected lateinit var moviesApiService: MoviesApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_up,  R.anim.slide_down)
        moviesApiService = MoviesApiService()
        Hawk.init(this).build()
    }

    protected fun showLoader() {
        progressBar?.beVisible()
    }

    protected fun hideLoader() {
        progressBar?.beGone()
    }

    private fun handleError(throwable: Throwable) {
        hideLoader()
        loadMoreProgressBar?.beGone()
        showError(throwable)
    }

    private fun showError(throwable: Throwable) {
        if (BuildConfig.DEBUG) throwable.printStackTrace()
        alert(
            R.string.general_error_message,
            R.string.error
        ) {
            okButton {  }
        }.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        scopeUi.coroutineContext.cancelChildren()
    }
}