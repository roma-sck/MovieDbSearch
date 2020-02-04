package com.example.moviedbsearch.api

import com.example.moviedbsearch.models.GenresResponse
import com.example.moviedbsearch.models.MoviesResponse
import com.example.moviedbsearch.models.PopularPersonsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesService {
    @GET("genre/movie/list?api_key=${ApiConstants.THE_MOVIES_DB_API_KEY}&language=en-US")
    suspend fun getMoviesGenres(): GenresResponse

    @GET("person/popular?api_key=${ApiConstants.THE_MOVIES_DB_API_KEY}&language=en-US")
    suspend fun getPopularPersons(
        @Query("page") page: Int? = null
    ): PopularPersonsResponse

    @GET("discover/movie?api_key=${ApiConstants.THE_MOVIES_DB_API_KEY}")
    suspend fun getMovies(
        @Query("page") page: Int? = null,
        @Query("year") year: Int? = null,
        @Query("sort_by") titleSort: String? = null,
        @Query("with_genres") genresIds: String? = null,
        @Query("with_cast") actorIds: String? = null,
        @Query("with_crew") crewIds: String? = null,
        @Query("include_adult") includeAdult: Boolean? = null
    ): MoviesResponse
}