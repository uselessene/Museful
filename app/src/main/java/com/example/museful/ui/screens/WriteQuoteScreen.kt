package com.example.museful.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.museful.R
import com.example.museful.data.AuthRepository
import com.example.museful.data.ReadingRepository
import com.example.museful.data.SessionViewModel
import com.example.museful.network.Quote
import com.example.museful.ui.theme.Dop
import com.example.museful.ui.theme.Osnova
import kotlinx.coroutines.launch

@Composable
fun WriteQuoteScreen(
    session: SessionViewModel,
    onBackClick: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    var selectedBook by remember { mutableStateOf<com.example.museful.network.Book?>(null) }
    var bookMenu by remember { mutableStateOf(false) }

    val viewModel: BookViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val scope = rememberCoroutineScope()
    val repo = remember { ReadingRepository() }
    val auth = remember { AuthRepository() }

    Column(
        Modifier.fillMaxSize().background(Color(0xFFF8E9E2))
            .padding(horizontal = 24.dp).verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(30.dp))

        Box(Modifier.fillMaxWidth().height(70.dp)) {
            Image(
                painter = painterResource(R.drawable.nazad),
                contentDescription = null,
                modifier = Modifier.size(20.dp).align(Alignment.CenterStart).clickable { onBackClick() }
            )
            Column(
                Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(painter = painterResource(R.drawable.icon), contentDescription = null,
                    modifier = Modifier.size(38.dp, 23.dp))
                Spacer(Modifier.height(4.dp))
                Text("Цитата", fontFamily = Dop, fontSize = 36.sp, color = Color(0xFF803D4D))
            }
        }

        Spacer(Modifier.height(30.dp))

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Выберите книгу:", fontFamily = Osnova, fontSize = 12.sp, color = Color(0xFF3B2D2D),
                modifier = Modifier.weight(1f))
            Box {
                Box(
                    Modifier.width(160.dp).height(30.dp).clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFEBC8C2)).clickable { bookMenu = true },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(selectedBook?.title ?: " ", fontFamily = Osnova, fontSize = 12.sp,
                            color = Color(0xFF3B2D2D), modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 1)
                        Image(painter = painterResource(R.drawable.spisok), contentDescription = null,
                            modifier = Modifier.size(12.dp).padding(end = 8.dp))
                    }
                }
                DropdownMenu(expanded = bookMenu, onDismissRequest = { bookMenu = false },
                    modifier = Modifier.width(150.dp).background(Color(0xFFF1DED7)).clip(RoundedCornerShape(20.dp))) {
                    viewModel.books.forEach { b ->
                        DropdownMenuItem(
                            text = { Text(b.title, fontFamily = Osnova, fontSize = 12.sp) },
                            onClick = { selectedBook = b; bookMenu = false }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Box(
            Modifier.fillMaxWidth().heightIn(min = 350.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color(0xFFF1DED7)).padding(16.dp)
        ) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                textStyle = TextStyle(fontFamily = Osnova, fontSize = 13.sp, color = Color(0xFF3B2D2D)),
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(30.dp))

        Button(
            onClick = {
                val userId = auth.currentUserId() ?: return@Button
                val bookId = selectedBook?.id ?: return@Button
                scope.launch {
                    repo.saveQuote(Quote(userId = userId, bookId = bookId, content = text))
                    onBackClick()
                }
            },
            modifier = Modifier.fillMaxWidth(0.75f).height(40.dp).align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEBC8C1))
        ) {
            Text("Сохранить", fontFamily = Osnova, fontSize = 13.sp, color = Color(0xFF3B2D2D))
        }

        Spacer(Modifier.height(40.dp))
    }
}