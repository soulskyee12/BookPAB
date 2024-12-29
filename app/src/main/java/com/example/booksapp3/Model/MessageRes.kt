package com.example.booksapp3.Model

data class MessageRes(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>
)

data class Choice(
    val index: Int,
    val message: MessageContent,
    val finish_reason: String
)

data class MessageContent(
    val role: String,
    val content: String
)