package com.example.booksapp3.Repository

import android.util.Log
import com.example.booksapp3.api.GroqApi
import com.example.booksapp3.Model.Message
import com.example.booksapp3.Model.MessageReq
import com.example.booksapp3.Model.MessageRes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatRepository {
    fun sendMessage(prompt: String, onResult: (String?) -> Unit) {
        // Konteks spesifik untuk membatasi lingkup jawaban
        val context = """
            Anda adalah asisten AI yang ahli di bidang kesehatan kulit. Jawaban Anda harus fokus pada:
            - com.example.skiva.model.Penyakit kulit dan penyebabnya.
            - com.example.skiva.model.Penyakit kulit yang menular dan cara penanganannya.
            - Jenis-jenis kulit wajah dan karakteristiknya.
            - Skincare dan saran perawatan kulit.
            - Saran pengobatan untuk masalah kulit umum.
            Jangan memberikan jawaban di luar topik ini.
        """.trimIndent()

        // Atur request body dengan konteks tambahan
        val request = MessageReq(
            model = "llama-3.3-70b-versatile", // Model yang valid
            messages = listOf(
                Message(role = "system", content = context),
                Message(role = "user", content = prompt)
            )
        )



        Log.d("ChatRepository", "Sending request: $request")

        GroqApi.instance.sendMessage(request).enqueue(object : Callback<MessageRes> {
            override fun onResponse(call: Call<MessageRes>, response: Response<MessageRes>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("ChatRepository", "Full API Response: ${response.body()}")

                    // Ambil respons dari `choices` → `message` → `content`
                    val reply = response.body()?.choices?.firstOrNull()?.message?.content
                    if (reply != null) {
                        Log.d("ChatRepository", "AI Response: $reply")
                        onResult(reply)
                    } else {
                        Log.e("ChatRepository", "No valid response in choices.")
                        onResult("Maaf, saya tidak dapat memberikan jawaban saat ini.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ChatRepository", "API Error: $errorBody")
                    onResult("Maaf, terjadi kesalahan pada server. Coba lagi nanti.")
                }
            }




            override fun onFailure(call: Call<MessageRes>, t: Throwable) {
                Log.e("ChatRepository", "API Call Failed: ${t.message}")
                onResult(null)
            }
        })
    }
}