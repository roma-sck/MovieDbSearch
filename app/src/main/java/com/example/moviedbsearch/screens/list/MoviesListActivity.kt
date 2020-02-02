package com.example.moviedbsearch.screens.list

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviedbsearch.BuildConfig
import com.example.moviedbsearch.FakeData
import com.example.moviedbsearch.screens.details.MovieDetailActivity
import com.example.moviedbsearch.R
import com.example.moviedbsearch.api.MoviesApiService
import com.example.moviedbsearch.extensions.beVisibleIf
import com.example.moviedbsearch.models.MovieInfo
import com.example.moviedbsearch.models.MoviesResponse
import com.example.moviedbsearch.utils.ExtraNames
import com.example.moviedbsearch.utils.LoadMoreScrollListener
import com.example.moviedbsearch.utils.MoviesDiffUtilCallback
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
    private var pageToLoad: Int? = null
    private var searchYear: Int? = null
    private var searchTitle: String? = null
    private var searchGenres: ArrayList<String>? = null
    private var searcActorName: String? = null
    private var searchDirectorName: String? = null
    private var searchShowAdult: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies_list)
        overridePendingTransition(R.anim.slide_up,  R.anim.slide_down)
        initSearchParams()
        loadMovies()
        initListeners()
    }

    private fun initSearchParams() {
        searchYear = intent?.getIntExtra(ExtraNames.EXTRA_SEARCH_YEAR, 0)
        searchTitle = intent?.getStringExtra(ExtraNames.EXTRA_SEARCH_TITLE)
        searchGenres = intent?.getStringArrayListExtra(ExtraNames.EXTRA_SEARCH_GENRES)
        searcActorName = intent?.getStringExtra(ExtraNames.EXTRA_SEARCH_ACTOR)
        searchDirectorName = intent?.getStringExtra(ExtraNames.EXTRA_SEARCH_DIRECTOR)
        searchShowAdult = intent?.getBooleanExtra(ExtraNames.EXTRA_SEARCH_SHOW_ADULT, false)
    }

    private fun loadMovies() {
        scopeUi.launch {
            showLoader()
            
            val apiResponse = withContext(Dispatchers.IO) {
                MoviesApiService().getMovies(
                    pageToLoad,
                    if (searchYear != 0) searchYear else null,
                    if (!title.isNullOrEmpty()) "original_title.desc" else null,
                    if (!searchGenres.isNullOrEmpty()) searchGenres!!.joinToString(",") else null,
                    null,
                    null,
                    searchShowAdult
                )
            }
            pageToLoad = apiResponse.page + 1
            initMoviesAdapter(apiResponse)

            hideLoader()
        }
    }

    private fun initListeners() {
        moviesList.addOnScrollListener(
            LoadMoreScrollListener(
                { loadMovies() },
                moviesList.layoutManager as LinearLayoutManager
            )
        )
    }

    private fun initMoviesAdapter(response: MoviesResponse?) {
        adapter = MoviesAdapter(
            response?.results.orEmpty()
        ) { openMovieDetails(it) }
        moviesList.adapter = adapter
        val itemsExist = adapter.itemCount > 0
        moviesList.beVisibleIf(itemsExist)
        emptyListText.beVisibleIf(!itemsExist)
    }

    private fun updateMoviesList(list: List<MovieInfo>) {
        val diffUtilCallback = MoviesDiffUtilCallback(adapter.movies, list)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        adapter.movies = list.toMutableList()
        diffResult.dispatchUpdatesTo(adapter)
    }

    private fun openMovieDetails(info: MovieInfo) {
        Intent(this, MovieDetailActivity::class.java).apply {
            putExtra(ExtraNames.EXTRA_MOVIE_INFO, info)
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
