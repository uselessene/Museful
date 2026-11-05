package com.example.museful.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.museful.data.ReadingRepository
import com.example.museful.data.supabase
import com.example.museful.network.Book
import com.example.museful.network.BookGenreEntry
import com.example.museful.network.GenreEntry
import com.example.museful.network.Review
import com.example.museful.network.UserBook
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BookDetailState(
    val book: Book? = null,
    val genres: List<String> = emptyList(),
    val status: String? = null,
    val review: Review? = null,
    val isLoading: Boolean = true
)

class BookDetailViewModel : ViewModel() {
    private val _state = MutableStateFlow(BookDetailState())
    val state: StateFlow<BookDetailState> = _state.asStateFlow()

    private val repo = ReadingRepository()

    fun load(bookId: Long, userId: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val book = try {
                supabase.from("books").select { filter { eq("id", bookId) } }.decodeSingleOrNull<Book>()
            } catch (e: Exception) { null }

            val genresList = try {
                // Скачиваем ВСЕ жанры и ВСЕ связи, чтобы избежать ошибок фильтрации
                val allGenres = supabase.from("genres").select().decodeList<GenreEntry>()
                val allBookGenres = supabase.from("book_genres").select().decodeList<BookGenreEntry>()

                // Ищем какие ID жанров привязаны к этой конкретной книге
                val genreIdsForBook = allBookGenres.filter { it.bookId == bookId }.map { it.genreId }

                // Сопоставляем ID с названиями
                allGenres.filter { it.id in genreIdsForBook }.map { it.name }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }

            var status: String? = null
            var review: Review? = null
            if (userId != null) {
                review = repo.getReviewForBook(userId, bookId)
                val ub = repo.getUserBooks(userId).find { it.bookId == bookId }
                status = ub?.status
            }

            _state.value = BookDetailState(
                book = book,
                genres = genresList,
                status = status,
                review = review,
                isLoading = false
            )
        }
    }

    fun saveStatus(bookId: Long, userId: String, status: String) {
        viewModelScope.launch {
            val res = repo.saveUserBook(UserBook(userId = userId, bookId = bookId, status = status))
            if (res.isSuccess) {
                _state.value = _state.value.copy(status = status)
            }
        }
    }
}