package com.example.booksapp3.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.booksapp3.Repository.ChatRepository

class ChatViewModel : ViewModel() {
    private val repository = ChatRepository()

    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages

    private val currentMessages = mutableListOf<ChatMessage>()

    fun sendMessage(userMessage: String) {
        // Tambahkan pesan pengguna ke daftar
        currentMessages.add(ChatMessage(userMessage, true)) // True: pesan dari pengguna
        _messages.value = currentMessages.toList()
        Log.d("ChatViewModel", "User message added: $userMessage")

        // Panggil ChatRepository untuk mendapatkan respons dari API
        repository.sendMessage(userMessage) { aiResponse ->
            if (aiResponse != null) {
                Log.d("ChatViewModel", "AI response received: $aiResponse")
                currentMessages.add(ChatMessage(aiResponse, false)) // False: pesan dari AI
                _messages.postValue(currentMessages.toList()) // Update LiveData
            } else {
                Log.e("ChatViewModel", "Failed to get AI response")
                currentMessages.add(ChatMessage("Maaf, terjadi kesalahan. Silakan coba lagi.", false))
                _messages.postValue(currentMessages.toList())
            }
        }
    }
}