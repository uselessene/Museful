package com.example.museful.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: Long,
    val title: String,
    val author: String,
    val description: String? = null,
    @SerialName("cover_url") val coverUrl: String? = null,
    val isbn: String? = null,
    @SerialName("published_year") val publishedYear: Int? = null,
    @SerialName("page_count") val pageCount: Int? = null,
    val language: String? = null
)

@Serializable
data class BookGenreEntry(
    @SerialName("book_id") val bookId: Long,
    @SerialName("genre_id") val genreId: Long
)

@Serializable
data class GenreEntry(
    val id: Long,
    val name: String
)