package com.example.museful.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.museful.network.UserProfileFull
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class SessionViewModel : ViewModel() {
    var isLoggedIn by mutableStateOf(false)
        private set
    var profile by mutableStateOf<UserProfileFull?>(null)
        private set

    private val auth = AuthRepository()

    init {
        refresh()
    }

    fun refresh() {
        isLoggedIn = auth.isLoggedIn()
        val userId = auth.currentUserId()
        if (userId != null) {
            viewModelScope.launch {
                profile = try {
                    supabase.from("users").select {
                        filter { eq("id", userId) }
                    }.decodeSingleOrNull<UserProfileFull>()
                } catch (e: Exception) { null }
            }
        } else {
            profile = null
        }
    }

    fun logout(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try { auth.logout() } catch (e: Exception) { }
            isLoggedIn = false
            profile = null
            onDone()
        }
    }
}