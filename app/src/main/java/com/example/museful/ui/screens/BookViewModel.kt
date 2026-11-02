package com.example.museful.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.museful.data.supabase
import com.example.museful.network.Book
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {
    var books by mutableStateOf<List<Book>>(emptyList())
        private set

    init {
        viewModelScope.launch {
            try {
                books = supabase.from("books").select {
                    order("id", Order.ASCENDING)
                }.decodeList<Book>()
            } catch (e: Exception) {
                books = emptyList()
            }
        }
    }

    suspend fun getBookById(id: Long): Book? {
        return try {
            supabase.from("books").select {
                filter { eq("id", id) }
            }.decodeSingleOrNull<Book>()
        } catch (e: Exception) {
            null
        }
    }
}