package com.example.moviedbsearch.screens.list

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviedbsearch.R
import com.example.moviedbsearch.extensions.beGone
import com.example.moviedbsearch.extensions.beVisible
import com.example.moviedbsearch.extensions.beVisibleIf
import com.example.moviedbsearch.models.MovieInfo
import com.example.moviedbsearch.models.MoviesResponse
import com.example.moviedbsearch.screens.base.BaseActivity
import com.example.moviedbsearch.screens.details.MovieDetailActivity
import com.example.moviedbsearch.utils.ExtraNames
import com.example.moviedbsearch.utils.LoadMoreScrollListener
import kotlinx.android.synthetic.main.activity_movies_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MoviesListActivity : BaseActivity() {
    private lateinit var adapter: MoviesAdapter
    private var pageToLoad: Int? = null
    private var searchYear: Int? = null
    private var searchTitle: String? = null
    private var searchGenres: ArrayList<Int>? = null
    private var searcActorIds: ArrayList<Int>? = null
    private var searchCrewMembersIds: ArrayList<Int>? = null
    private var searchShowAdult: Boolean? = null
    private var canLoadMore = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies_list)
        initSearchParams()
        initMoviesAdapter()
        loadMovies(true)
        initListeners()
    }

    private fun initSearchParams() {
        searchYear = intent?.getIntExtra(ExtraNames.EXTRA_SEARCH_YEAR, 0)
        searchTitle = intent?.getStringExtra(ExtraNames.EXTRA_SEARCH_TITLE)
        searchGenres = intent?.getIntegerArrayListExtra(ExtraNames.EXTRA_SEARCH_GENRES)
        searcActorIds = intent?.getIntegerArrayListExtra(ExtraNames.EXTRA_SEARCH_ACTORS)
        searchCrewMembersIds = intent?.getIntegerArrayListExtra(ExtraNames.EXTRA_SEARCH_CREW_MEMBERS)
        searchShowAdult = intent?.getBooleanExtra(ExtraNames.EXTRA_SEARCH_SHOW_ADULT, false)
    }

    private fun loadMovies(initialLoading: Boolean) {
        if (!canLoadMore) return
        scopeUi.launch {
            if (initialLoading) showLoader()
            else loadMoreProgressBar.beVisible()

            val apiResponse = withContext(Dispatchers.IO) {
                moviesApiService.getMovies(
                    pageToLoad,
                    if (searchYear != 0) searchYear else null,
                    if (!title.isNullOrEmpty()) "original_title.desc" else null,
                    getRequestStringFromList(searchGenres),
                    getRequestStringFromList(searcActorIds),
                    getRequestStringFromList(searchCrewMembersIds),
                    searchShowAdult
                )
            }
            pageToLoad = apiResponse.page + 1

            if (initialLoading) hideLoader()
            else loadMoreProgressBar.beGone()

            updateMoviesList(apiResponse)
        }
    }

    private fun getRequestStringFromList(dataList: ArrayList<Int>?): String? {
        return if (!dataList.isNullOrEmpty()) dataList.joinToString(",") else null
    }

    private fun initListeners() {
        moviesList.addOnScrollListener(
            LoadMoreScrollListener(
                { loadMovies(false) },
                moviesList.layoutManager as LinearLayoutManager
            )
        )
        header.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initMoviesAdapter() {
        adapter = MoviesAdapter(emptyList()) { openMovieDetails(it) }
        moviesList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        moviesList.adapter = adapter
    }

    @SuppressLint("DefaultLocale")
    private fun updateMoviesList(response: MoviesResponse) {
        val list = if (searchTitle.isNullOrEmpty()) {
            response.results
        } else {
            response.results.filter {
                it.title.toLowerCase().contains(searchTitle!!.toLowerCase()) ||
                        it.original_title.toLowerCase().contains(searchTitle!!.toLowerCase())
            }
        }
        canLoadMore = response.total_pages > (response.page + 1)
        if (adapter.movies.isEmpty() && list.isEmpty() && canLoadMore && (pageToLoad == null || pageToLoad!! < 6)) {
            loadMovies(true)
        }

        val diffUtilCallback =
            MoviesDiffUtilCallback(
                adapter.movies,
                list
            )
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        adapter.movies = list.toMutableList()
        diffResult.dispatchUpdatesTo(adapter)

        val itemsExist = adapter.itemCount > 0
        moviesList.beVisibleIf(itemsExist)
        emptyListText.beVisibleIf(!itemsExist)
    }

    private fun openMovieDetails(info: MovieInfo) {
        Intent(this, MovieDetailActivity::class.java).apply {
            putExtra(ExtraNames.EXTRA_MOVIE_INFO, info)
            startActivity(this)
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
        }
    }
}
