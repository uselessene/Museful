package com.example.museful.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.museful.R
import com.example.museful.data.AuthRepository
import com.example.museful.data.ReadingRepository
import com.example.museful.data.SessionViewModel
import com.example.museful.network.Quote
import com.example.museful.ui.theme.Dop
import com.example.museful.ui.theme.Osnova

@Composable
fun QuotesScreen(
    session: SessionViewModel,
    onBackClick: () -> Unit,
    onWriteQuoteClick: () -> Unit
) {
    val auth = remember { AuthRepository() }
    val repo = remember { ReadingRepository() }
    val viewModel: BookViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    var quotes by remember { mutableStateOf<List<Quote>>(emptyList()) }
    val booksMap = viewModel.books.associateBy { it.id }

    LaunchedEffect(Unit) { session.refresh() }
    LaunchedEffect(session.isLoggedIn) {
        val userId = auth.currentUserId() ?: return@LaunchedEffect
        quotes = repo.getQuotes(userId)
    }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize()) {
            Spacer(Modifier.height(30.dp))

            Box(Modifier.fillMaxWidth().height(70.dp).padding(horizontal = 24.dp)) {
                Image(
                    painter = painterResource(R.drawable.nazad),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp).align(Alignment.CenterStart).clickable { onBackClick() }
                )
                Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon),
                        contentDescription = null,
                        modifier = Modifier.size(38.dp, 23.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("Цитаты", fontFamily = Dop, fontSize = 36.sp, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                Modifier.padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .clickable { onWriteQuoteClick() }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Записать", fontFamily = Osnova, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.width(6.dp))
                Image(painter = painterResource(R.drawable.pero), contentDescription = null,
                    modifier = Modifier.size(14.dp))
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(quotes) { q ->
                    val book = booksMap[q.bookId]
                    Box(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp))
                            .background(MaterialTheme.colorScheme.surface).padding(16.dp)
                    ) {
                        Column {
                            Row(Modifier.fillMaxWidth()) {
                                Image(painter = painterResource(R.drawable.kav1), contentDescription = null,
                                    modifier = Modifier.size(20.dp))
                                Spacer(Modifier.weight(1f))
                            }
                            Text(
                                q.content,
                                fontFamily = Osnova,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                            )
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                Image(painter = painterResource(R.drawable.kav2), contentDescription = null,
                                    modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.height(16.dp))
                            Text("${book?.author ?: ""} - ${book?.title ?: ""}",
                                fontFamily = Osnova, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

                            Text("2026-10-20", fontFamily = Osnova, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}