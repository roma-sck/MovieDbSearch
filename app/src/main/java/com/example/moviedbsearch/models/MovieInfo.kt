package com.example.moviedbsearch.models

import java.io.Serializable

data class MovieInfo(
    val popularity: Double = 0.0,
    val vote_count: Int = 0,
    val video: Boolean = false,
    val poster_path: String = "",
    val id: Int = 0,
    val adult: Boolean = false,
    val backdrop_path: String = "",
    val original_language: String = "",
    val original_title: String = "",
    val genre_ids: List<Int> = listOf(),
    val title: String = "",
    val vote_average: Float = 0.0f,
    val overview: String = "",
    val release_date: String = ""
) : Serializable