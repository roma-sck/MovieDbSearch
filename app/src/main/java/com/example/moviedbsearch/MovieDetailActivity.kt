package com.example.moviedbsearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.moviedbsearch.models.MovieInfo
import kotlinx.android.synthetic.main.activity_movie_detail.*

class MovieDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        (intent.getSerializableExtra("MOVIE_INFO") as? MovieInfo)?.let {
            updateUi(it)
        }
    }

    private fun updateUi(movie: MovieInfo) {
        text.text = movie.original_title
    }
}
