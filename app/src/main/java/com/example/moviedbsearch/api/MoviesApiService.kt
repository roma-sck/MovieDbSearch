package com.example.moviedbsearch.api

import com.example.moviedbsearch.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MoviesApiService {

    private val webService by lazy {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        val authInterceptor = Interceptor {chain->
            val newUrl = chain.request().url()
                .newBuilder()
                .addQueryParameter("api_key", ApiConstants.THE_MOVIES_DB_API_KEY)
                .build()
            val newRequest = chain.request().newBuilder().url(newUrl).build()
            chain.proceed(newRequest)
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
        Retrofit.Builder()
            .baseUrl(ApiConstants.THE_MOVIES_DB_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(MoviesService::class.java)
    }

    suspend fun getMoviesGenres() = webService.getMoviesGenres()

    suspend fun getPopularPersons(
        page: Int?
    ) = webService.getPopularPersons(page)

    suspend fun getMovies(
        page: Int?,
        year: Int?,
        titleSort: String?,
        genres: String?,
        actorId: String?,
        crewId: String?,
        includeAdult: Boolean?
    ) = webService.getMovies(page, year, titleSort, genres, actorId, crewId, includeAdult)
}