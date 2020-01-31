package com.example.moviedbsearch.api

import com.example.moviedbsearch.models.MoviesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesService {
    @GET("movie?api_key=${ApiConstants.THE_MOVIES_DB_API_KEY}")
    suspend fun getMovies(
        @Query("page") page: Int? = null,
        @Query("year") year: Int? = null,
        @Query("sort_by") titleSort: String? = null,
        @Query("with_genres") genres: String? = null,
        @Query("with_cast") actorId: String? = null,
        @Query("with_crew") crewId: String? = null,
        @Query("include_adult") includeAdult: Boolean? = null
    ): MoviesResponse
}