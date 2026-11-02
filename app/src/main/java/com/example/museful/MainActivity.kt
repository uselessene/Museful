package com.example.museful

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.museful.data.ThemeManager
import com.example.museful.ui.MusefulNavGraph
import com.example.museful.ui.theme.MusefulTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ThemeManager.init(this)

        setContent {
            MusefulTheme(darkTheme = ThemeManager.isDarkTheme) {
                MusefulNavGraph()
            }
        }
    }
}