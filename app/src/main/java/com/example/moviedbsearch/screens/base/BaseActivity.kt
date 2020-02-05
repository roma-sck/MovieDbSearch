package com.example.moviedbsearch.screens.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.moviedbsearch.BuildConfig
import com.example.moviedbsearch.R
import com.example.moviedbsearch.api.MoviesApiService
import com.example.moviedbsearch.extensions.beGone
import com.example.moviedbsearch.extensions.beVisible
import com.novoda.merlin.Merlin
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_movies_list.*
import kotlinx.coroutines.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton

abstract class BaseActivity : AppCompatActivity() {
    private val job = SupervisorJob()
    private val errorHandler = CoroutineExceptionHandler { _, throwable -> handleError(throwable) }
    protected val scopeUi = CoroutineScope(Dispatchers.Main + job + errorHandler)
    private val errorHandlerSilent = CoroutineExceptionHandler { _, throwable -> handleSilentError(throwable) }
    protected val scopeUiSilent = CoroutineScope(Dispatchers.Main + job + errorHandlerSilent)
    protected lateinit var moviesApiService: MoviesApiService
    private lateinit var merlin: Merlin
    protected var connectedToInternet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_up,  R.anim.slide_down)
        initInstances()
    }

    private fun initInstances() {
        merlin = Merlin.Builder().withConnectableCallbacks().withDisconnectableCallbacks().build(this)
        merlin.apply {
            registerConnectable { connectedToInternet = true }
            registerDisconnectable { connectedToInternet = false }
        }
        moviesApiService = MoviesApiService()
        Hawk.init(this).build()
    }

    override fun onResume() {
        super.onResume()
        merlin.bind()
    }

    override fun onPause() {
        merlin.unbind()
        super.onPause()
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
        logError(throwable)
        showErrorMessage(R.string.general_error_message)
    }

    private fun handleSilentError(throwable: Throwable) {
        logError(throwable)
    }

    protected fun showErrorMessage(errorMessage: Int) {
        alert(
            errorMessage,
            R.string.error
        ) {
            okButton {  }
        }.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        scopeUi.coroutineContext.cancelChildren()
    }

    private fun logError(throwable: Throwable) {
        if (BuildConfig.DEBUG) throwable.printStackTrace()
    }
}