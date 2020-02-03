package com.example.moviedbsearch.screens.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.moviedbsearch.R
import com.example.moviedbsearch.api.ApiConstants
import com.example.moviedbsearch.extensions.beVisible
import com.example.moviedbsearch.models.MovieInfo
import com.example.moviedbsearch.screens.base.BaseActivity
import com.example.moviedbsearch.utils.ExtraNames
import kotlinx.android.synthetic.main.activity_movie_detail.*

class MovieDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
        (intent?.getSerializableExtra(ExtraNames.EXTRA_MOVIE_INFO) as? MovieInfo)?.let {
            updateUi(it)
        }
    }

    private fun updateUi(movie: MovieInfo) {
        val posterBgUrl = ApiConstants.THE_MOVIES_DB_IMAGE_BASE_URL_WITH_SIZE + movie.backdrop_path
        val posterUrl = ApiConstants.THE_MOVIES_DB_IMAGE_BASE_URL_WITH_SIZE + movie.poster_path
        Glide.with(this)
            .load(posterBgUrl)
            .into(movieBgPoster)
        Glide.with(this)
            .load(posterUrl)
            .into(moviePoster)
        movieTitle.text = movie.original_title
        year.text = movie.release_date.substring(0, 4)
        voteRating.rating = movie.vote_average
        overview.text = movie.overview
        votesCountHint.text = String.format(getString(R.string.votes_count), movie.vote_count)
        popularity.text = String.format(getString(R.string.popularity), movie.popularity)
        openInWeb.beVisible()
        openInWeb.setOnClickListener {
            val url = ApiConstants.THE_MOVIES_DB_BASE_OPEN_WEB_URL + movie.id
            openInBrowser(url)
        }
    }

    private fun openInBrowser(url: String) {
        val openUrlIntent = Intent(Intent.ACTION_VIEW)
        openUrlIntent.data = Uri.parse(url)
        startActivity(openUrlIntent)
    }
}
