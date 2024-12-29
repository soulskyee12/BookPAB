package com.example.booksapp3.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import com.example.booksapp3.Model.MessageReq
import com.example.booksapp3.Model.MessageRes

interface
GroqApiService {
    @Headers(
        "Authorization: Bearer GroqAPIHERE",
        "Content-Type: application/json"
    )
    @POST("chat/completions")
    fun sendMessage(@Body request: MessageReq): Call<MessageRes>
}