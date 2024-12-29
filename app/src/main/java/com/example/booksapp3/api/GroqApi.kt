package com.example.booksapp3.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GroqApi {
    private const val BASE_URL = "https://api.groq.com/openai/v1/"

    val instance: GroqApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GroqApiService::class.java)
    }
}