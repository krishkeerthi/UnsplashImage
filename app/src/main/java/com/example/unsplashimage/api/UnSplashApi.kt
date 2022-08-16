package com.example.unsplashimage.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


const val BASE_URL = "https://api.unsplash.com/"
const val CLIENT_ID = "9KN9w3c0qYgiIo4cKDWVjIMN1qiSnQa-7OuWSaTtcCs"

val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

interface ApiService {


    @GET("search/photos/?client_id=$CLIENT_ID")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int) : UnSplashResponse

}

object UnsplashApi{
    val service: ApiService by lazy{
        retrofit.create(ApiService::class.java)
    }
}