package com.example.moviedbsearch.api

import com.example.moviedbsearch.BuildConfig

object ApiConstants {
    internal const val THE_MOVIES_DB_API_KEY = BuildConfig.MOVIE_DB_API_KEY
    internal const val THE_MOVIES_DB_BASE_URL = "https://api.themoviedb.org/3/"
    private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
    private const val IMAGE_SIZE_DEFAULT = "w500/"
    internal const val THE_MOVIES_DB_IMAGE_BASE_URL_WITH_SIZE = IMAGE_BASE_URL + IMAGE_SIZE_DEFAULT
    internal const val THE_MOVIES_DB_BASE_OPEN_WEB_URL = "https://themoviedb.org/movie/"
}