package com.example.museful.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object ThemeManager {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_DARK_THEME = "dark_theme"

    var isDarkTheme by mutableStateOf(false)
        private set

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isDarkTheme = prefs.getBoolean(KEY_DARK_THEME, false)
    }

    fun toggleTheme(context: Context) {
        isDarkTheme = !isDarkTheme
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DARK_THEME, isDarkTheme).apply()
    }
}