package com.example.moviedbsearch.models

data class MoviesResponse(
    val page: Int = 0,
    val total_results: Int = 0,
    val total_pages: Int = 0,
    val results: List<MovieInfo> = listOf()
)