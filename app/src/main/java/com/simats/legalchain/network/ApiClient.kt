package com.simats.legalchain.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // ⚠️ Update this EVERY TIME you restart ngrok
    private const val BASE_URL = "https://max-unsmoothed-endorsingly.ngrok-free.dev/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
