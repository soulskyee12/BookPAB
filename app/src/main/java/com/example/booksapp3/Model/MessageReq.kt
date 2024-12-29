package com.example.booksapp3.Model

data class MessageReq(
    val model: String,
    val messages: List<Message>
)

data class Message(
    val role: String,
    val content: String
)