package com.example.museful.data

import com.example.museful.network.Quote
import com.example.museful.network.ReadingDay
import com.example.museful.network.Review
import com.example.museful.network.UserBook
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class ReadingRepository {

    suspend fun getDays(userId: String): List<ReadingDay> {
        return try {
            supabase.from("reading_days").select {
                filter { eq("user_id", userId) }
                order("date", Order.ASCENDING)
            }.decodeList<ReadingDay>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun saveDay(day: ReadingDay) {
        try {
            val existing = supabase.from("reading_days").select {
                filter { eq("user_id", day.userId); eq("date", day.date) }
            }.decodeList<ReadingDay>().firstOrNull()

            if (existing != null) {
                supabase.from("reading_days").update({
                    set("book_id", day.bookId)
                    set("pages_read", day.pagesRead)
                    set("impression", day.impression)
                }) { filter { eq("id", existing.id ?: 0L) } }
            } else {
                supabase.from("reading_days").insert(day)
            }
        } catch (e: Exception) { }
    }

    suspend fun getUserBooks(userId: String): List<UserBook> {
        return try {
            supabase.from("user_books").select {
                filter { eq("user_id", userId) }
            }.decodeList<UserBook>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun saveUserBook(userBook: UserBook): Result<Unit> {
        return try {
            val existing = supabase.from("user_books").select {
                filter { eq("user_id", userBook.userId); eq("book_id", userBook.bookId) }
            }.decodeList<UserBook>().firstOrNull()

            if (existing != null) {
                supabase.from("user_books").update({
                    set("status", userBook.status)
                }) { filter { eq("id", existing.id ?: 0L) } }
            } else {
                supabase.from("user_books").insert(userBook.copy(id = null))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReviews(userId: String): List<Review> {
        return try {
            supabase.from("reviews").select {
                filter { eq("user_id", userId) }
                order("created_at", Order.DESCENDING)
            }.decodeList<Review>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getReviewForBook(userId: String, bookId: Long): Review? {
        return try {
            supabase.from("reviews").select {
                filter { eq("user_id", userId); eq("book_id", bookId) }
            }.decodeSingleOrNull<Review>()
        } catch (e: Exception) { null }
    }

    suspend fun saveReview(review: Review) {
        try {
            val existing = getReviewForBook(review.userId, review.bookId)
            if (existing != null) {
                supabase.from("reviews").update({
                    set("content", review.content)
                    set("rating", review.rating)
                    set("mood", review.mood)
                    set("quote", review.quote)
                }) { filter { eq("id", existing.id ?: 0L) } }
            } else {
                supabase.from("reviews").insert(review)
            }
        } catch (e: Exception) { }
    }

    suspend fun getQuotes(userId: String): List<Quote> {
        return try {
            supabase.from("quotes").select {
                filter { eq("user_id", userId) }
                order("created_at", Order.DESCENDING)
            }.decodeList<Quote>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun saveQuote(quote: Quote) {
        try {
            supabase.from("quotes").insert(quote)
        } catch (e: Exception) { }
    }

    suspend fun deleteUserBook(userId: String, bookId: Long) {
        try {
            supabase.from("user_books").delete {
                filter {
                    eq("user_id", userId)
                    eq("book_id", bookId)
                }
            }
        } catch (e: Exception) { }
    }
}