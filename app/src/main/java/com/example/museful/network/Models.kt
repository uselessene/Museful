package com.example.museful.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReadingDay(
    val id: Long? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("book_id") val bookId: Long? = null,
    val date: String,
    @SerialName("pages_read") val pagesRead: Int = 0,
    val impression: String? = null
)

@Serializable
data class UserBook(
    val id: Long? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("book_id") val bookId: Long,
    val status: String,
    val rating: Int? = null
)

@Serializable
data class Review(
    val id: Long? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("book_id") val bookId: Long,
    val rating: Int? = null,
    val mood: String? = null,
    val content: String,
    val quote: String? = null
)

@Serializable
data class Quote(
    val id: Long? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("book_id") val bookId: Long,
    val content: String
)

@Serializable
data class UserProfileFull(
    val id: String,
    val username: String,
    val email: String,
    @SerialName("avatar_id") val avatarId: Int = 1,
    @SerialName("created_at") val createdAt: String? = null
)