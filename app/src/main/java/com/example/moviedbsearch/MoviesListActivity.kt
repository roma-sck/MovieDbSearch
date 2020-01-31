package com.example.moviedbsearch

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.moviedbsearch.api.MoviesApiService
import com.example.moviedbsearch.extensions.beVisibleIf
import com.example.moviedbsearch.models.MovieInfo
import com.example.moviedbsearch.models.MoviesResponse
import kotlinx.android.synthetic.main.activity_movies_list.*
import kotlinx.coroutines.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton

class MoviesListActivity : AppCompatActivity() {
    private val job = SupervisorJob()
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }
    private val scopeUi = CoroutineScope(Dispatchers.Main + job + errorHandler)
    private lateinit var adapter: MoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies_list)
        loadMovies()
    }

    private fun loadMovies() {
        scopeUi.launch {
            showLoader()
            val apiResponse = withContext(Dispatchers.IO) {
                MoviesApiService().getMovies(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                )
            }
            initAdapter(apiResponse)
            hideLoader()
        }
    }

    private fun initAdapter(response: MoviesResponse?) {
        adapter = MoviesAdapter(
            response?.results.orEmpty()
        ) { openMovieDetails(it) }
        moviesList.adapter = adapter
        val itemsExist = adapter.itemCount > 0
        moviesList.beVisibleIf(itemsExist)
        emptyListText.beVisibleIf(!itemsExist)
    }

    private fun openMovieDetails(info: MovieInfo) {
        Intent(this, MovieDetailActivity::class.java).apply {
            putExtra("MOVIE_INFO", info)
            startActivity(this)
        }
    }

    private fun showLoader() {

    }

    private fun hideLoader() {

    }

    private fun handleError(throwable: Throwable) {
        hideLoader()
        showError(throwable)
    }

    private fun showError(throwable: Throwable) {
        if (BuildConfig.DEBUG) throwable.printStackTrace()
        alert(R.string.general_error_message, R.string.error) {
            okButton {  }
        }.show()
    }
}
